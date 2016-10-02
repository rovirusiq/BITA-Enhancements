package ro.bcr.bita.odi.proxy

import oracle.odi.domain.project.OdiFolder;
import oracle.odi.domain.project.finder.IOdiFolderFinder

import ro.bcr.bita.model.BitaSpockSpecification
import ro.bcr.bita.odi.proxy.BitaOdiException;
import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.proxy.IOdiProjectPaths;
import ro.bcr.bita.odi.proxy.OdiPathUtil;
import ro.bcr.bita.odi.proxy.OdiPathUtil.MappingPaths;

import spock.lang.Specification
import spock.lang.Unroll;

class OdiPathsUtilTest extends BitaSpockSpecification {
	
	OdiPathUtil subject;
	IOdiEntityFactory mOdiEntityFactory;
	IOdiFolderFinder mOdiFolderFinder;
	
	def setup() {
		
		mOdiFolderFinder=Mock();
		mOdiEntityFactory=Mock();
		
		mOdiEntityFactory.createProjectFolderFinder() >> mOdiFolderFinder;
		
		mOdiEntityFactory.newOdiProjectPaths(_ as Map)>> {Map x->
			return new OdiProjectPaths(x);
		}
		
		mOdiEntityFactory.newOdiMappingFullPath(_ as String,_ as String,_ as String) >> {String a, String b,String c->
			return new OdiFullMappingPath(a,b,c);
		}
		
		subject=new OdiPathUtil(mOdiEntityFactory);
	}
	
	
	@Unroll
	def "check valid paths"(){
		when:	"A valid expression paths is provided"
				List<String> rsp=subject.checkSyntax(inputString);
		
		then:	"The response indicates is a include path"
				rsp.size()==4
				rsp[0]==firstElement
				rsp[1]==secondElement;
				rsp[2]==thirdElement;
				rsp[3]==forthElement;
				
		where:	"data used is"
				inputString|firstElement|secondElement|thirdElement|forthElement
				"+PRJ_CODE:F_NAME"|"+"|"PRJ_CODE"|"F_NAME"|""
				"PRJCODE:FNAME"|"+"|"PRJCODE"|"FNAME"|""
				"-PrjC001:f45N"|"-"|"PrjC001"|"f45N"|""
				"-PrjC001:fA:M1_X"|"-"|"PrjC001"|"fA"|"M1_X"
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
				"PRJ VA:CUTU:CVB"|_
	}
	
	def "Retrieve folders from an ODI project with 0 folers"(){
		given:	"a valid ODI project code for a project with 0 folders"
				String projectCode="SMTH";
				mOdiFolderFinder.findByProject(_)>>[];
		when:	"we try to extract the folders from it"
				List lst=subject.getFoldersForProject(projectCode);
		then:	"an empty list is returned and the odi folder was "
				lst.size()==0;
				
	}
	
	def "Retrieve folders from an ODI project with 1 or more folders"(){
		given:	"a valid ODI project code for a project with more than 1 folders"
				String projectCode="SMTH";
				def fld1=BITA_MOCK_ODI_FOLDER(NAME:"FOLDER1");
				def fld2=BITA_MOCK_ODI_FOLDER(NAME:"FOLDER2");
				mOdiFolderFinder.findByProject(projectCode)>>[fld1,fld2];
		when:	"we try to extract the folders from it"
				List lst=subject.getFoldersForProject(projectCode);
		then:	"an empty list is returned and the odi folder was "
				lst.size()==2;
				lst.containsAll("FOLDER1","FOLDER2");
				
	}
	
	def "When retrieve folders from an ODI project an exception is thrown"(){
		given:	"a valid ODI project code for a project with more than 1 folders"
				String projectCode="SMTH";
				mOdiFolderFinder.findByProject(_) >>{throw new RuntimeException("Fake Exception")}
		when:	"we try to extract the folders from it"
				List lst=subject.getFoldersForProject(projectCode);
		then:	"an empty list is returned and the odi folder was "
				thrown BitaOdiException;
				
	}
	
	def "Map Paths Population - Multiple calls with complete paths - No Retrieval needed"(){
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
	
	def "Map Paths Population - Multiple calls with a mix of complete and incomplete paths"(){
		
		given:	"an empty map path, and multiple valid paths,projects, folders"
				Map<String,Set<String>> mapPaths=[:];
				List path1=["+","PRJ1",""];
				List path2=["+","PRJ2",""];
				List path3=["+","PRJ3",""];
				List path5=["+","PRJ1",""];
				List path6=["+","PRJ2","FY"];
				
				OdiFolder fX=BITA_MOCK_ODI_FOLDER(NAME:"FX");
				OdiFolder fY=BITA_MOCK_ODI_FOLDER(NAME:"FY");
				OdiFolder fA=BITA_MOCK_ODI_FOLDER(NAME:"FA");
				OdiFolder fB=BITA_MOCK_ODI_FOLDER(NAME:"FB");
				
				mOdiFolderFinder.findByProject("PRJ1") >>[];
				mOdiFolderFinder.findByProject("PRJ2") >>[fX,fX,fY];//2 times the same folder
				mOdiFolderFinder.findByProject("PRJ3") >>[fA,fB];
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
	
	def "Extract Mapping Paths-Empty parameter"(){
		given:	"several projects and their folders"
		
				OdiFolder fX=BITA_MOCK_ODI_FOLDER(NAME:"FX");
				OdiFolder fY=BITA_MOCK_ODI_FOLDER(NAME:"FY");
				OdiFolder fA=BITA_MOCK_ODI_FOLDER(NAME:"FA");
				OdiFolder fB=BITA_MOCK_ODI_FOLDER(NAME:"FB");
		
				mOdiFolderFinder.findByProject("PRJ1") >>[];
				mOdiFolderFinder.findByProject("PRJ2") >>[fX,fX,fY];//2 times the same folder
				mOdiFolderFinder.findByProject("PRJ3") >>[fA,fB];
				
		when:	"no parameter specifed as paths"
				MappingPaths rsp=subject.extractMappingPaths();
		then:	"the reposnse has o paths in it"
				rsp!=null;
				rsp.getProjectPaths()!=null;
				rsp.getProjectPaths().getProjects().size()==0;
		
	}
	
	def "Extract Mapping Paths-Test reponse aggregation"(){
		given:	"several projects and their folders"
		
				OdiFolder fX=BITA_MOCK_ODI_FOLDER(NAME:"FX");
				OdiFolder fY=BITA_MOCK_ODI_FOLDER(NAME:"FY");
				OdiFolder fA=BITA_MOCK_ODI_FOLDER(NAME:"FA");
				OdiFolder fB=BITA_MOCK_ODI_FOLDER(NAME:"FB");
		
				mOdiFolderFinder.findByProject("PRJ1") >>[];
				mOdiFolderFinder.findByProject("PRJ2") >>[fX,fX,fY];//2 times the same folder
				mOdiFolderFinder.findByProject("PRJ3") >>[fA,fB];
				
		when:	"multile paths specified"
				MappingPaths rsp=subject.extractMappingPaths(
					"+PRJ1"
					,"PRJ2:FZ"
					,"+PRJ3:FC"
					,"PRJ2"
					,"+PRJ3"
					,"PRJ4:XX:M_1"
					,"+PRJ5:XY:M_2"
					,"-PRJ4:XX:M_3"
				);
				IOdiFullMappingPath mp1=mOdiEntityFactory.newOdiMappingFullPath("PRJ4","XX","M_1");
				IOdiFullMappingPath mp2=mOdiEntityFactory.newOdiMappingFullPath("PRJ5","XY","M_2");
				IOdiFullMappingPath mp3=mOdiEntityFactory.newOdiMappingFullPath("PRJ4","XX","M_3");
		then:	"we will have an anser"
				rsp!=null;
				rsp.getProjectPaths()!=null;
		then:	"we will not have projects with no folders"
				!rsp.getProjectPaths().getProjects().contains("PRJ1");
		then:	"the concatenation will be calculated correctly for the other projects"
				rsp.getProjectPaths()getFoldersForProject("PRJ2").containsAll("FX","FY","FZ");
				rsp.getProjectPaths().getFoldersForProject("PRJ2").size()==3;
				
				rsp.getProjectPaths().getFoldersForProject("PRJ3").containsAll("FA","FB","FC");
				rsp.getProjectPaths().getFoldersForProject("PRJ3").size()==3;
		then:	"it contains only those projects"
				rsp.getProjectPaths().getProjects().containsAll("PRJ2","PRJ3");
				rsp.getProjectPaths().getProjects().size()==2;
		then:	"full path to be included Mappings are in results"
				rsp.getIncludeMappings().containsAll(mp1,mp2);
				rsp.getIncludeMappings().size()==2;
		then:	"full path to be excluded Mappings are in results"
				rsp.getExcludeMappings().containsAll(mp3);
				rsp.getExcludeMappings().size()==1;
	}
	
	def "Extract Mapping Paths-Test path arithemetics"(){
		given:	"several projects and their folders"
		
				OdiFolder fX=BITA_MOCK_ODI_FOLDER(NAME:"FX");
				OdiFolder fY=BITA_MOCK_ODI_FOLDER(NAME:"FY");
				OdiFolder fA=BITA_MOCK_ODI_FOLDER(NAME:"FA");
				OdiFolder fB=BITA_MOCK_ODI_FOLDER(NAME:"FB");
		
		
				mOdiFolderFinder.findByProject("PRJ1") >>[];
				mOdiFolderFinder.findByProject("PRJ2") >>[fX,fX,fY];//2 times the same folder
				mOdiFolderFinder.findByProject("PRJ3") >>[fA,fB];
				
		when:	"multile paths specified"
				MappingPaths rsp=subject.extractMappingPaths(
					"+PRJ1"
					,"PRJ2:FZ"
					,"+PRJ3:FC"
					,"PRJ2"
					,"-PRJ2"
					,"+PRJ3"
					,"+PRJ1:CUTU"
					,"-PRJ1:CUTU2"
					,"-PRJ1:CUTU"
					,"+PRJ2:FX:M_1"
					,"+PRJ3:FC:M_2"
					,"-PRJ3:XX:M_3"
					,"+PRJ4:ZA:M_4"
					,"+PRJ4:ZB:M_5"
					
				);
				IOdiFullMappingPath mp1=mOdiEntityFactory.newOdiMappingFullPath("PRJ2","FX","M_1");
				IOdiFullMappingPath mp2=mOdiEntityFactory.newOdiMappingFullPath("PRJ3","FC","M_2");
				IOdiFullMappingPath mp3=mOdiEntityFactory.newOdiMappingFullPath("PRJ3","XX","M_3");
				IOdiFullMappingPath mp4=mOdiEntityFactory.newOdiMappingFullPath("PRJ4","ZA","M_4");
				IOdiFullMappingPath mp5=mOdiEntityFactory.newOdiMappingFullPath("PRJ4","ZB","M_5");
		then:	"we will have an answer"
				rsp!=null;
				rsp.getProjectPaths()!=null;
		then:	"we will not have projects with no folders"
				!rsp.getProjectPaths().getProjects().contains("PRJ1");
		then:	"the concatenation will be calculated correctly for the other projects"
				rsp.getProjectPaths().getFoldersForProject("PRJ2").containsAll("FZ");
				rsp.getProjectPaths().getFoldersForProject("PRJ2").size()==1;

				rsp.getProjectPaths().getFoldersForProject("PRJ3").containsAll("FA","FB","FC");
				rsp.getProjectPaths().getFoldersForProject("PRJ3").size()==3;
		then:	"it contains only those projects"
				rsp.getProjectPaths().getProjects().containsAll("PRJ2","PRJ3");
				rsp.getProjectPaths().getProjects().size()==2;
		then:	"it excludes the included Mappings that have the path excluded"
				!rsp.getIncludeMappings().contains(mp1);
		then: 	"it excludes the included Mappings that have a path already included"
				!rsp.getIncludeMappings().contains(mp2);
		then: 	"it keps the full path included Mappings that have been provided and are not reason for elimination"
				rsp.getIncludeMappings().containsAll(mp4,mp5);
				rsp.getIncludeMappings().size()==2;
		then: 	"it keeps the full path exclude Mappings as they were provided "
				rsp.getExcludeMappings().contains(mp3);
				rsp.getExcludeMappings().size()==1;
	}
	
	
	def "Extract Mapping Paths - One explicit exclusion of an implicit inclusion"(){
		given:	"several projects and their folders"
		
				OdiFolder fX=BITA_MOCK_ODI_FOLDER(NAME:"FX");
				OdiFolder fY=BITA_MOCK_ODI_FOLDER(NAME:"FY");
				OdiFolder fA=BITA_MOCK_ODI_FOLDER(NAME:"FA");
				OdiFolder fB=BITA_MOCK_ODI_FOLDER(NAME:"FB");
		
		
				mOdiFolderFinder.findByProject("PRJ1") >>[];
				mOdiFolderFinder.findByProject("PRJ2") >>[fX,fY];//2 times the same folder
				mOdiFolderFinder.findByProject("PRJ3") >>[fA,fB];
				
		when:	"multile paths specified"
				MappingPaths rsp=subject.extractMappingPaths(
					"PRJ2:FA:M1"
					,"PRJ2:FA:M2"
					,"PRJ2:FA:M3"
					,"-PRJ2:FA:M3"
					
				);
				IOdiFullMappingPath mp1=mOdiEntityFactory.newOdiMappingFullPath("PRJ2","FA","M1");
				IOdiFullMappingPath mp2=mOdiEntityFactory.newOdiMappingFullPath("PRJ2","FA","M2");
				IOdiFullMappingPath mp3=mOdiEntityFactory.newOdiMappingFullPath("PRJ2","FA","M3");
		then:	"we will have an answer"
				rsp!=null;
				rsp.getProjectPaths()!=null;
		then:	"included Mappings will contain only M1 and M2, brcause M3 is explicitly excluded"
				rsp.getIncludeMappings().containsAll(mp1,mp1);
				rsp.getIncludeMappings().size()==2;
		then:	"excluded Mappings will have the declared exclusion"
				rsp.getExcludeMappings().size()==1;
	}
	
	def "Extract Mapping Paths- One explicit Folder exclusion and an implicit Mappings inclusion"(){
		given:	"several projects and their folders"
		
				OdiFolder fX=BITA_MOCK_ODI_FOLDER(NAME:"FX");
				OdiFolder fY=BITA_MOCK_ODI_FOLDER(NAME:"FY");
				OdiFolder fA=BITA_MOCK_ODI_FOLDER(NAME:"FA");
				OdiFolder fB=BITA_MOCK_ODI_FOLDER(NAME:"FB");
		
		
				mOdiFolderFinder.findByProject("PRJ1") >>[];
				mOdiFolderFinder.findByProject("PRJ2") >>[fX,fY];//2 times the same folder
				mOdiFolderFinder.findByProject("PRJ3") >>[fA,fB];
				
		when:	"multile paths specified"
				MappingPaths rsp=subject.extractMappingPaths(
					"PRJ2:FA:M1"
					,"PRJ2:FA:M2"
					,"PRJ2:FA:M3"
					,"-PRJ2:FA"
					
				);
				IOdiFullMappingPath mp1=mOdiEntityFactory.newOdiMappingFullPath("PRJ2","FA","M1");
				IOdiFullMappingPath mp2=mOdiEntityFactory.newOdiMappingFullPath("PRJ2","FA","M2");
				IOdiFullMappingPath mp3=mOdiEntityFactory.newOdiMappingFullPath("PRJ2","FA","M3");
		then:	"we will have an answer"
				rsp!=null;
				rsp.getProjectPaths()!=null;
		then:	"included Mappings will contain only M1 and M2, brcause M3 is explicitly excluded"
				rsp.getIncludeMappings().size()==0;
		then:	"excluded Mappings will have the declared exclusion, in this case 0"
				rsp.getExcludeMappings().size()==0;
	}
	

}
