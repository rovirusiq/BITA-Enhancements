package ro.bcr.bita.odi.template

import ro.bcr.bita.model.BitaModelFactory
import ro.bcr.bita.model.IBitaModelFactory
import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.model.IOdiScenario
import ro.bcr.bita.odi.proxy.IOdiEntityFactory
import ro.bcr.bita.odi.proxy.IOdiProjectPaths
import ro.bcr.bita.odi.proxy.OdiPathUtil

import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.domain.runtime.scenario.OdiScenario
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFolderFinder

public  class OdiCommandContext implements IOdiCommandContext,IOdiMappingVersions{
	private final IOdiEntityFactory odiEntityFactory;
	private final OdiPathUtil odiPathUtils;
	
	private IBitaModelFactory bitaModelFactory=new BitaModelFactory();
	
	public OdiCommandContext(IOdiEntityFactory odiEntityFactory) {
		this.odiEntityFactory=odiEntityFactory;
		this.odiPathUtils=bitaModelFactory.newOdiPathUtil(odiEntityFactory);
	}
	
	public getOdiEntityFactory() {
		return this.odiEntityFactory;
	}
	
	
	/********************************************************************************************
	 *
	 *IOdiCommandContext - Simple Getters
	 *
	 ********************************************************************************************/
	/*
	 * Simple getters
	 */
	
	@Override
	public IMappingFinder getMappingFinder() {
		return this.odiEntityFactory.createMappingFinder()
	}

	@Override
	public IOdiProjectFinder getProjectFinder() {
		return this.odiEntityFactory.createProjectFinder();
	}

	@Override
	public IOdiScenarioFinder getScenarioFinder() {
		return this.odiEntityFactory.createScenarioFinder();
	}
	
	@Override
	public IOdiScenarioFolderFinder getScenarioFolderFinder() {
		return this.odiEntityFactory.createScenarioFolderFinder();
	}
	
	@Override
	public IOdiFolderFinder getProjectFolderFinder() {
		return this.odiEntityFactory.createProjectFolderFinder();
	}

	/********************************************************************************************
	 *
	 *IOdiCommandContext - Value added methods
	 *
	 ********************************************************************************************/
	
	@Override
	public List<IOdiMapping> findMappings(String... odiPaths) throws OdiTemplateException{
		
		try {
		
			IOdiProjectPaths rsp=this.odiPathUtils.extractProjectPaths(odiPaths);
			
			List<IOdiMapping> mps=[];
			
			
			IMappingFinder finder=this.odiEntityFactory.createMappingFinder();
			
			for (String prj:rsp.getProjects()) {
				
				for (String folder:rsp.getFoldersForProject(prj)) {
				
					mps << (finder.findByProject(prj,folder).collect{bitaModelFactory.newOdiMapping(it)});
				}
			}
			
			return mps.flatten();
		} catch (Exception ex) {
			throw new OdiTemplateException("An exception occured when trying to identify mappings from the pats[$odiPaths].",ex);
		}
	}
	

	@Override
	public IOdiScenario findScenarioForMapping(IOdiMapping map, String pVersion) throws OdiTemplateException {
		 OdiScenario scenOfInterest=this.getScenarioFinder().findBySourceMapping(map.getInternalId())?.find{elem->elem.getVersion()=="$pVersion"};
		 if (scenOfInterest==null) throw new OdiTemplateException("No scenario with version $pVersion was identified for mapping[${map}]");
		 return scenOfInterest;
	}

	@Override
	public IOdiScenario findBitaScenarioForMapping(IOdiMapping map) throws OdiTemplateException {
		return this.findScenarioForMapping(map,MAPPING_VERSION_NUMBER_FOR_PROD);
	}

	@Override
	public IOdiScenario findTestScenarioForMapping(IOdiMapping map) throws OdiTemplateException {
		return this.findScenarioForMapping(map,MAPPING_VERSION_NUMBER_FOR_TESTING);
	}

	
	

	
}