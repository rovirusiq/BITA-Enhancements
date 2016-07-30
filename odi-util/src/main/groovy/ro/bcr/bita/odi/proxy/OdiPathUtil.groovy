package ro.bcr.bita.odi.proxy

import ro.bcr.bita.model.BitaModelFactory
import ro.bcr.bita.model.IBitaModelFactory

import groovy.transform.CompileStatic

import java.util.regex.Matcher

import oracle.odi.domain.project.OdiFolder

@CompileStatic
class OdiPathUtil {
	
	
	private IOdiEntityFactory odiEntityFactory;
	private IBitaModelFactory bitaModelFactory=new BitaModelFactory();
	
	/**
	 * @param odiEntityFactory
	 */
	public OdiPathUtil(IOdiEntityFactory odiEntityFactory) {
		super();
		this.odiEntityFactory = odiEntityFactory;
	}

	/**
	 * Check if the supplied argument is a valid path.
	 * Empty list if the expression is not valid
	 * List<String> if the expression is valid:
	 * **** first element the sign: +,-
	 * **** second element the project code
	 * **** third element the name of the folder
	 */
	protected List<String> checkSyntax(String p) {
		List<String> rsp=[];
		
		Matcher m=p=~ /^(\+|\-)?([a-zA-Z0-9_]+)(?::([a-zA-Z0-9_]+))?$/;
		
		if (m.matches()) {
			
			rsp << (m.group(1)? m.group(1):"+");
			rsp << m.group(2);
			rsp << (m.group(3)? m.group(3):"");
			
		}
		return rsp;
	}
	
	/**
	 * @param projectCode - The ODI project code for the targeted project. Any string accepted by ODI. The method does not perform any validation
	 * @return - a list of folder names that belong to that project
	 * @throws BitaOdiException In case of unexpected exceptions
	 */
	public List<String> getFoldersForProject(String projectCode) throws BitaOdiException{
		List<String> rsp=[];
		try {
			rsp.addAll(
				this.odiEntityFactory.createProjectFolderFinder()
				.findByProject(projectCode).collect{OdiFolder it -> return it.name}
			);
		} catch (RuntimeException ex) {
			throw new BitaOdiException("An unexpected error was encountered when trying to get the folders for the project[${projectCode}].",ex);
		}
		return rsp;
	}
	
	
	/**
	 * @param mapPaths HahMap for which to add the 
	 * @param lst
	 */
	protected void populateMapPaths(Map<String,Set<String>> mapPaths,List<String> lst) {
			List folders=[];
			if (lst[2]=="") {
				folders=this.getFoldersForProject(lst[1]);
			} else {
				folders << lst[2];
			}
			
			Set alreadyExistingFolders=mapPaths.get(lst[1]);
			if (alreadyExistingFolders==null) {
				alreadyExistingFolders=[]
			}
			alreadyExistingFolders.addAll(folders);
			
			mapPaths.put(lst[1],alreadyExistingFolders);
		
	}
	
	/**
	 * @param paths Array of paths that need to be inspected. The format of the path is "(+|-)?:PROJECT_CODE:FOLDER_NAME"
	 * <ul>
	 * <li>+|- Sign that will tell if the path should be included or excluded. If no sign is provided + is default.</li>
	 * <li>PROJECT_CODE - The code of the project. It is mandatory</li>
	 * <li>FOLDER_NAME - The name of the folder. Optional</li>
	 * </ul> 
	 * @return A HashMap wit details about the paths that should be inspected. The key it will be the project and the value will be a Set of folder names. If the parameters do not
	 * contain the folders explicit the method will retrieve the folders.
	 * @throws BitaOdiException
	 */
	//TODO make it more performant. Also maybe implement a cache for already retrieved project folders.
	public IOdiProjectPaths extractProjectPaths(String... paths) throws BitaOdiException{
		//List<String> faultyPaths=[];
		Map<String,Set<String>> includePaths=[:];
		Map<String,Set<String>> excludePaths=[:];
		
		for (String p:paths) {
			List<String> lst=this.checkSyntax(p);
			
			//check if the path was invalid
			if (lst.size()<=0){
				//faultyPaths<<p;
				continue;
			}
			
			if (lst[0]=="+") {
				populateMapPaths(includePaths,lst);
			}
			if (lst[0]=="-") {
				populateMapPaths(excludePaths,lst);
			}	
			
		}
		
		//combine include and exclude paths. Exclude is more powerful. Exclude entire projects if they have no folders.
		Set<String> keys=[];
		keys.addAll(includePaths.keySet());
		
		
		for (int i=0;i<keys.size();i++) {
			String k=keys[i];
			
			Set<String> includeP=includePaths.get(k);
			Set<String> excludeP=excludePaths.get(k); 
			
			if ((excludeP!=null) && (includeP!=null)) {
				includeP=includeP-excludeP;
				includePaths.put(k,includeP);
			}
			if ((includeP==null) || (includeP.size()<=0)) {
				includePaths.remove(k);
			}
		}
		return bitaModelFactory.newOdiProjectPaths(includePaths);
	}

}