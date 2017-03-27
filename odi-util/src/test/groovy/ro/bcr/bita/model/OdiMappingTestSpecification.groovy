package ro.bcr.bita.model


import spock.lang.Specification
import oracle.odi.domain.mapping.Mapping
import ro.bcr.bita.odi.proxy.IOdiOperationsService
import ro.bcr.bita.odi.proxy.BitaOdiException

class OdiMappingTestSpecification extends BaseMockMappingSpecification{
	
	OdiMapping subject;
	IBitaDomainFactory mckFactory;
	Mapping odiObject;
	
	def setup() {
		mckFactory=Mock();
		odiObject=Mock();
	}
	
	
	def "Constructor - when the constructor parameter for mapping is null, an exception is thrown"(){
		given:	"""The objects from the setup method"""
		when:	"The object is constructed with null parameter"
				subject=new OdiMapping(null,mckFactory);
		then:	"An exception is thrown"
				thrown BitaModelException;
	}
	
	def "Constructor - when the constructor parameter for bitaFactory is null, an exception is thrown"(){
		given:	"""The objects from the setup method"""
		when:	"The object is constructed with null parameter"
				subject=new OdiMapping(odiObject,null);
		then:	"An exception is thrown"
				thrown BitaModelException;
	}
	
	def "Delegation - when an existing interface method is used, it is delegated to the odi object"(){
		given:	"""The objects from the setup method"""
				def cutu=3;
				def nbr;
		when:	"The object is constructed and a simple method is called"
				subject=new OdiMapping(odiObject,mckFactory);
				nbr=subject.getInternalId();
		then:	"the id is returned"
				1*odiObject.getInternalId() >> cutu;
				0*_;//no other interactions
				nbr==cutu;
	}
	
	def "Delegation - an non-existing interface method is used, it is delegated directly to the odi object"(){
		given:	"""The objects from the setup method"""
				def cutu=3;
				def nbr;
		when:	"The object is constructed and a simple method is called"
				subject=new OdiMapping(odiObject,mckFactory);
				subject.getPhysicalDesign(0);
		then:	"An exception is thrown"
				1*odiObject.getPhysicalDesign(0);
				0*_;//no other interactions
	}
	
	def "Identify Sources - when no clear SOURCE or TARGET is present, an exception is thrown"(){
		given:	"""The objects from the setup method"""
	
				odiObject=createMockOdiMapping("TestMockMapping1","""
				LC_A
""");
				
				
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mckFactory);
				List<String> srcs=subject.identifySources();
		then:	"An exception is thrown"
				BitaModelException ex=thrown();
				ex.message.startsWith("No source table was");
	}
	
	def "Identify Sources - when MULTIPLE targets are identified, and the map is constructed with explicit option not to allow, an exception is thrown"(){
		given:	"""The objects from the setup method"""
				odiObject=createMockOdiMapping("TestMockMapping1","""
				LC_A>>ST_A
				LC_B>>ST_B
""");
				
				
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mckFactory,false);
				List<String> srcs=subject.identifySources();
		then:	"An exception is thrown"
				BitaModelException ex=thrown();
				ex.message.startsWith("More than one target");
	}
	
	def "Identify Sources - when MULTIPLE targets are identified, and the map is constructed with implicit option not to allow, an exception is thrown"(){
		given:	"""The objects from the setup method"""
				odiObject=createMockOdiMapping("TestMockMapping1","""
				LC_A>>ST_A
				LC_B>>ST_B
""");
				
				
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mckFactory);
				List<String> srcs=subject.identifySources();
		then:	"An exception is thrown"
				BitaModelException ex=thrown();
				ex.message.startsWith("More than one target");
	}
	
	def "Identify Sources - when MULTIPLE targets are identified, and this is allowed, no exception is thrown"(){
		given:	"""The objects from the setup method"""
				odiObject=createMockOdiMapping("TestMockMapping1","""
				LC_A>>ST_A
				LC_B>>ST_B
""");
				
				
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mckFactory,true);
				List<String> srcs=subject.identifySources();
		then:	"no exception is thrown"
				notThrown BitaModelException;
	}
	
	def "Identify Sources -when mapping is analyzed, source,targets, and mapping type are correclty identified"(){
		
		given:	"""The objects from the setup method"""			
				Mapping odiObject=createMockOdiMapping("TestMockMapping1","""
					LC_A>>TEMP_X>>ST_A
					LC_B>>TEMP_X>>ST_A
					LC_C>>TEMP_Y>>ST_A
					LC_A>>TEMP_Y>>ST_A
""");
				
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mckFactory);
				List<String> srcs=subject.identifySources();
				String target=subject.identifyTarget();
		then:	"Source and target are correctly identified"
				srcs==["LC_A","LC_B","LC_C"];
				target=="ST_A";
				subject.isDataAcquisitionMapping()==false;
	}
	
	def "Identify Sources -identify correclty a data acquisition mapping for BASE"(){
		
		given:	"""The objects from the setup method"""
				Mapping odiObject=createMockOdiMapping("TestMockMapping1","""
					LZ_A>>LC_B
""");
				
				
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mckFactory);
				List<String> srcs=subject.identifySources();
				String target=subject.identifyTarget();
		then:	"Source and target are correctly identified"
				srcs==["LZ_A"];
				target=="LC_B";
				subject.isDataAcquisitionMapping()==true;
		
	}
	
	def "Identify Sources -identify correclty a data acquisition mapping for Landing Zone"(){
		
		given:	"""The objects from the setup method"""
				Mapping odiObject=createMockOdiMapping("TestMockMapping1","""
					XXX_A>>LZ_B
""");
								
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mckFactory);
				List<String> srcs=subject.identifySources();
				String target=subject.identifyTarget();
		then:	"Source and target are correctly identified"
				srcs==["XXX_A"];
				target=="LZ_B";
				subject.isDataAcquisitionMapping()==true;
		
	}
	
	def "Identify Sources -identify correclty when is not a data acquisition method"(){
		
		given:	"""The objects from the setup method"""
				Mapping odiObject=createMockOdiMapping("TestMockMapping1","""
					XXX_A>>YYY_B
""");
								
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mckFactory);
				List<String> srcs=subject.identifySources();
				String target=subject.identifyTarget();
		then:	"Source and target are correctly identified"
				srcs==["XXX_A"];
				target=="YYY_B";
				subject.isDataAcquisitionMapping()==false;
	}
	
	def "GetScenario - check interaction with other objects when scenario is found"(){
		given:	"The objects from the setup method and our subject"
				IOdiOperationsService mckOpService=Mock();
				IOdiScenario mckScenario=Mock();
				subject=new OdiMapping(odiObject,mckFactory);
		when:	"The methog getScenario is called and a scenario is identified"
				IOdiScenario rsp=subject.getScenario("001");
				
		then:	"The interactions are as expected and the result is correct"
				1*mckFactory.newOdiOperationsService()>> mckOpService;
				1*mckOpService.findScenarioForMapping(subject,"001")>>mckScenario;
				rsp==mckScenario;
	}
	
	def "GetScenario - check exception throwing when scenatio not found"(){
		given:	"The objects from the setup method and our subject"
				IOdiOperationsService mckOpService=Mock();
				IOdiScenario mckScenario=Mock();
				subject=new OdiMapping(odiObject,mckFactory);
		when:	"The methog getScenario is called and a scenario is NOT identified"
				IOdiScenario rsp=subject.getScenario("001");
				
		then:	"The interactions are as expected "
				1*mckFactory.newOdiOperationsService()>> mckOpService;
				1*mckOpService.findScenarioForMapping(subject,"001")>>{throw new BitaOdiException("Scenario not found")};
		then:	"And a BitaModelException is thrown"
				thrown BitaModelException;
	}
	
	def "GetParameterListOfScenario - check interaction with other objects when scenario is found"(){
		given:	"The objects from the setup method and our subject"
				IOdiOperationsService mckOpService=Mock();
				IOdiScenario mckScenario=Mock();
				List<String> expectedResult=["CUTU"];
				subject=new OdiMapping(odiObject,mckFactory);
		when:	"The methog getScenario is called and a scenario is identified"
				List<String> rsp=subject.getParameterListOfScenario("001");
				
		then:	"The interactions are as expected and the result is correct"
				1*mckFactory.newOdiOperationsService()>> mckOpService;
				1*mckOpService.findScenarioForMapping(subject,"001")>>mckScenario;
				1*mckScenario.getParameterList()>>expectedResult;
				rsp==expectedResult;
	}
	
	def "GetParameterListOfScenario - check interaction with other objects when scenario is NOT found"(){
		given:	"The objects from the setup method and our subject"
				IOdiOperationsService mckOpService=Mock();
				IOdiScenario mckScenario=Mock();
				List<String> expectedResult=["CUTU"];
				subject=new OdiMapping(odiObject,mckFactory);
		when:	"The methog getScenario is called and a scenario is identified"
				List<String> rsp=subject.getParameterListOfScenario("001");
				
		then:	"The interactions are as expected"
				1*mckFactory.newOdiOperationsService()>> mckOpService;
				1*mckOpService.findScenarioForMapping(subject,"001")>>{throw new BitaOdiException("Scenario not found")};
				
		then:	"And a BitaModelException is thrown"
				thrown BitaModelException;
	}

}
