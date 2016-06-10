package ro.bcr.bita.proxy.odi

import oracle.odi.domain.project.finder.IOdiFolderFinder
import spock.lang.Specification
import spock.lang.Unroll;

class OdiPathsUtilTest extends Specification {
	
	OdiPathUtil subject;
	IOdiEntityFactory odiEntityFactory;
	IOdiFolderFinder odiFolderFinder;
	
	def setup() {
		
		odiEntityFactory=Mock();
		odiFolderFinder=Mock();
		
		odiEntityFactory.createProjectFolderFinder() >> odiFolderFinder;
		
		
		subject=new OdiPathUtil(odiEntityFactory);
	}
	
	
	@Unroll
	def "check valid paths"(){
		when:	"A valid expression paths is provided"
				List<String> rsp=subject.checkSyntax(inputString);
		
		then:	"The response indicates is a include path"
				rsp.size()==3
				rsp[0]==firstElement
				rsp[1]==secondElement;
				rsp[2]==thirdElement;
				
		where:	"data used is"
				inputString|firstElement|secondElement|thirdElement
				"+PRJ_CODE:F_NAME"|"+"|"PRJ_CODE"|"F_NAME"
				"PRJCODE:FNAME"|"+"|"PRJCODE"|"FNAME"
				"-PrjC001:f45N"|"-"|"PrjC001"|"f45N"
	}
	
	@Unroll
	def "check invalid paths"(){
		when:	"A valid expression for include paths is provided"
				List<String> rsp=subject.checkSyntax(inputString);
		
		then:	"The response indicates is a include path"
				rsp.size()==0
		where:	"data used is"
				inputString|_
				"@PRJ_CODE"|_
				"PRJ_CODE:C@VA"|_
				"PRJ_CODEC@VA"|_
				"PRJ VA"|_
	}
	
	def "Retrieve folders from an ODI project with 0 folers"(){
		given:	"a valid ODI project code for a project with 0 folders"
				String projectCode="SMTH";
				odiFolderFinder.findByProject(_)>>[];
		when:	"we try to extract the folders from it"
				List lst=subject.getFoldersForProject(projectCode);
		then:	"an empty list is returned and the odi folder was "
				lst.size()==0;
				
	}
	
	def "Retrieve folders from an ODI project with 1 or more folders"(){
		given:	"a valid ODI project code for a project with more than 1 folders"
				String projectCode="SMTH";
				odiFolderFinder.findByProject(_)>>["FOLDER1","FOLDER2"];
		when:	"we try to extract the folders from it"
				List lst=subject.getFoldersForProject(projectCode);
		then:	"an empty list is returned and the odi folder was "
				lst.size()==2;
				lst.containsAll("FOLDER1","FOLDER2");
				
	}
	
	def "When retrieve folders from an ODI project an exception is thrown"(){
		given:	"a valid ODI project code for a project with more than 1 folders"
				String projectCode="SMTH";
				odiFolderFinder.findByProject(_) >>{throw new RuntimeException("Fake Exception")}
		when:	"we try to extract the folders from it"
				List lst=subject.getFoldersForProject(projectCode);
		then:	"an empty list is returned and the odi folder was "
				thrown BitaOdiException;
				
	}
	
	def "Map Paths Popoulation - Multiple calls with complete paths - No Retrieval needed"(){
		given:	"an empty map path, and multiple valid paths"
				Map<String,Set<String>> mapPaths=[:];
				List path1=["+","PRJ1","F1"];
				List path2=["+","PRJ2","FX"];
				List path3=["+","PRJ1","F3"];
				List path4=["+","PRJ3","FA"];
				List path5=["+","PRJ1","F1"];
				List path6=["+","PRJ2","FY"]
		when:	"we to add valid project paths to the map path"
				subject.populateMapPaths(mapPaths,path1);
				subject.populateMapPaths(mapPaths,path2);
				subject.populateMapPaths(mapPaths,path3);
				subject.populateMapPaths(mapPaths,path4);
				subject.populateMapPaths(mapPaths,path5);
				subject.populateMapPaths(mapPaths,path6);
		then:	"The mapPtah is populated correctly"
				mapPaths.size()==3;
				mapPaths.get("PRJ1").containsAll("F1","F3");//set check
				mapPaths.get("PRJ1").size()==2;
				mapPaths.get("PRJ2").containsAll("FX","FY");
				mapPaths.get("PRJ2").size()==2;
				mapPaths.get("PRJ3").containsAll("FA");
				mapPaths.get("PRJ3").size()==1;
				
	}
	
	def "Map Paths Popoulation - Multiple calls with a mix of complete and incomplete paths"(){
		given:	"an empty map path, and multiple valid paths"
				Map<String,Set<String>> mapPaths=[:];
				List path1=["+","PRJ1",""];
				List path2=["+","PRJ2",""];
				List path3=["+","PRJ3",""];
				List path5=["+","PRJ1",""];
				List path6=["+","PRJ2","FY"];
				odiFolderFinder.findByProject("PRJ1") >>[];
				odiFolderFinder.findByProject("PRJ2") >>["FX","FX","FY"];
				odiFolderFinder.findByProject("PRJ3") >>["FA","FB"];
		when:	"we to add valid project paths to the map path"
				subject.populateMapPaths(mapPaths,path1);
				subject.populateMapPaths(mapPaths,path2);
				subject.populateMapPaths(mapPaths,path3);
				subject.populateMapPaths(mapPaths,path5);
				subject.populateMapPaths(mapPaths,path6);
		then:	"mapPaths will have the expected size"
				mapPaths.size()==3;
		then:	"The projects wtih no folders they will have an empty collection. Those will be removed later on in the processing"
				mapPaths.get("PRJ1").size()==0;
		then:	"The other projects will have the correct SET of folders"
				mapPaths.get("PRJ2").containsAll("FX","FY");
				mapPaths.get("PRJ2").size()==2;
				mapPaths.get("PRJ3").containsAll("FA","FB");
				mapPaths.get("PRJ3").size()==2;
	}
	
	def "Extract Paths-Empty parameter"(){
		given:	"several projects and their folders"
				odiFolderFinder.findByProject("PRJ1") >>[];
				odiFolderFinder.findByProject("PRJ2") >>["FX","FX","FY"];
				odiFolderFinder.findByProject("PRJ3") >>["FA","FB"];
				
		when:	"no parameter specifed as paths"
				IOdiProjectPaths rsp=subject.extractProjectPaths();
		then:	"the reposnse has o paths in it"
				rsp!=null;
				rsp.getProjects().size()==0;
		
	}
	
	def "Extract Paths-Test concatenation"(){
		given:	"several projects and their folders"
				odiFolderFinder.findByProject("PRJ1") >>[];
				odiFolderFinder.findByProject("PRJ2") >>["FX","FX","FY"];
				odiFolderFinder.findByProject("PRJ3") >>["FA","FB"];
				
		when:	"multile paths specified"
				IOdiProjectPaths rsp=subject.extractProjectPaths(
					"+PRJ1",
					"PRJ2:FZ",
					"+PRJ3:FC",
					"PRJ2",
					"+PRJ3"
				);
		then:	"we will have an anser"
				rsp!=null;
		then:	"we will not have projects with no folders"
				!rsp.getProjects().contains("PRJ1");
		then:	"the concatenation will be calculated correctly for the other projects"
				rsp.getFoldersForProject("PRJ2").containsAll("FX","FY","FZ");
				rsp.getFoldersForProject("PRJ2").size()==3;
				
				rsp.getFoldersForProject("PRJ3").containsAll("FA","FB","FC");
				rsp.getFoldersForProject("PRJ3").size()==3;
		then:	"it contains only those projects"
				rsp.getProjects().containsAll("PRJ2","PRJ3");
				rsp.getProjects().size()==2;
	}
	
	def "Extract Paths-Test path arithemetics"(){
		given:	"several projects and their folders"
				odiFolderFinder.findByProject("PRJ1") >>[];
				odiFolderFinder.findByProject("PRJ2") >>["FX","FX","FY"];
				odiFolderFinder.findByProject("PRJ3") >>["FA","FB"];
				
		when:	"multile paths specified"
				IOdiProjectPaths rsp=subject.extractProjectPaths(
					"+PRJ1"
					,"PRJ2:FZ"
					,"+PRJ3:FC"
					,"PRJ2"
					,"-PRJ2"
					,"+PRJ3"
					,"+PRJ1:CUTU"
					,"-PRJ1:CUTU2"
					,"-PRJ1:CUTU"
					
				);
		then:	"we will have an anser"
				rsp!=null;
		then:	"we will not have projects with no folders"
				!rsp.getProjects().contains("PRJ1");
		then:	"the concatenation will be calculated correctly for the other projects"
				rsp.getFoldersForProject("PRJ2").containsAll("FZ");
				rsp.getFoldersForProject("PRJ2").size()==1;

				rsp.getFoldersForProject("PRJ3").containsAll("FA","FB","FC");
				rsp.getFoldersForProject("PRJ3").size()==3;
		then:	"it contains only those projects"
				rsp.getProjects().containsAll("PRJ2","PRJ3");
				rsp.getProjects().size()==2;
	}


}
