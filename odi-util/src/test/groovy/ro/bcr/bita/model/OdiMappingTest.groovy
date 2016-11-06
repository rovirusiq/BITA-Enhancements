package ro.bcr.bita.model

import ro.bcr.bita.odi.proxy.BitaOdiException;
import ro.bcr.bita.odi.proxy.IOdiOperationsService;

import oracle.odi.domain.mapping.Mapping

//TODO add tests for query generation and execution
class OdiMappingTest extends BitaSpockSpecification{
	
	def OdiMapping subject;
	def Mapping odiObject;
	IBitaDomainFactory mFactory;
	
	def setup() {
		odiObject=Mock();
		mFactory=Mock();
	}
	
	def "Constructor - when the constructor parameter for mapping is null, an exception is thrown"(){
		given:	"""The objects from the setup method"""
		when:	"The object is constructed with null parameter"
				subject=new OdiMapping(null,mFactory);
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
	
	def "Delegation - when an existing interface method is used, it is delegated directly to the odi object"(){
		given:	"""The objects from the setup method"""
				def cutu=3;
				def nbr;
		when:	"The object is constructed and a simple method is called"
				subject=new OdiMapping(odiObject,mFactory);
				nbr=subject.getInternalId();
		then:	"An exception is thrown"
				1*odiObject.getInternalId() >> cutu;
				0*_;//no other interactions
				nbr==cutu;
	}
	
	def "Delegation - an non-existing interface method is used, it is delegated directly to the odi object"(){
		given:	"""The objects from the setup method"""
				def cutu=3;
				def nbr;
		when:	"The object is constructed and a simple method is called"
				subject=new OdiMapping(odiObject,mFactory);
				subject.getPhysicalDesign(0);
		then:	"An exception is thrown"
				1*odiObject.getPhysicalDesign(0);
				0*_;//no other interactions
	}
	
	
	def "Identify Sources - when no SOURCE is present, an exception is thrown"(){
		given:	"""The objects from the setup method"""				
				def nd1=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"LC_A"
					,"UPSTREAM_NODES":[1]
					,"DOWNSTREAM_NODES":[]
				);
	
				odiObject=BITA_MOCK_ODI_MAPPING nd1;
				
				
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mFactory);
				List<String> srcs=subject.identifySources();
		then:	"An exception is thrown"
				BitaModelException ex=thrown();
				ex.message.startsWith("No source table was");
	}
	
	def "Identify Sources - when no TARGET is present, an exception is thrown"(){
		given:	"""The objects from the setup method"""
				def nd1=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"LC_A"
					,"UPSTREAM_NODES":[]
					,"DOWNSTREAM_NODES":[1]
				);
	
				odiObject=BITA_MOCK_ODI_MAPPING nd1;
				
				
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mFactory);
				List<String> srcs=subject.identifySources();
		then:	"An exception is thrown"
				BitaModelException ex=thrown();
				ex.message.startsWith("No target table was");
	}
	
	
	def "Identify Sources - when MULTIPLE targets are identified , an exception is thrown"(){
		given:	"""The objects from the setup method"""
				def nd1=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"LC_A"
					,"UPSTREAM_NODES":[1]
					,"DOWNSTREAM_NODES":[]
				);
			
				def nd2=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"LC_A"
					,"UPSTREAM_NODES":[1]
					,"DOWNSTREAM_NODES":[]
				);
	
				odiObject=BITA_MOCK_ODI_MAPPING nd1,nd2;
				
				
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mFactory);
				List<String> srcs=subject.identifySources();
		then:	"An exception is thrown"
				BitaModelException ex=thrown();
				ex.message.startsWith("More than one target");
	}
	
	def "Identify Sources -when mapping is analyzed, source,targets, and mapping type are correclty identified"(){
		
		given:	"""The objects from the setup method"""
				def nd1=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"LC_A"
					,"UPSTREAM_NODES":[]
					,"DOWNSTREAM_NODES":[3]
				);
			
				def nd2=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"LC_B"
					,"UPSTREAM_NODES":[]
					,"DOWNSTREAM_NODES":[1]
				);
			
				def nd3=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"LC_C"
					,"UPSTREAM_NODES":[]
					,"DOWNSTREAM_NODES":[2]
				);
			
				def nd4=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"ST_A"
					,"UPSTREAM_NODES":[2]
					,"DOWNSTREAM_NODES":[]
				);
			
				def nd5=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"TEMP_X"
					,"UPSTREAM_NODES":[1]
					,"DOWNSTREAM_NODES":[2]
				);
			
				def nd6=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"TEMP_Y"
					,"UPSTREAM_NODES":[3]
					,"DOWNSTREAM_NODES":[5]
				);
			
				odiObject=BITA_MOCK_ODI_MAPPING nd1,nd2,nd3,nd4,nd5,nd6;
				
				
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mFactory);
				List<String> srcs=subject.identifySources();
				String target=subject.identifyTarget();
		then:	"Source and target are correctly identified"
				srcs==["LC_A","LC_B","LC_C"];
				target=="ST_A";
				subject.isDataAcquisitionMapping()==false;
		
	}
	
	def "Identify Sources -identify correclty a data acquisition mapping for BASE"(){
		
		given:	"""The objects from the setup method"""
				def nd1=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"XXX_A"
					,"UPSTREAM_NODES":[]
					,"DOWNSTREAM_NODES":[1]
				);
			
				def nd2=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"LC_B"
					,"UPSTREAM_NODES":[1]
					,"DOWNSTREAM_NODES":[]
				);
			
			
				odiObject=BITA_MOCK_ODI_MAPPING nd1,nd2;
				
				
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mFactory);
				List<String> srcs=subject.identifySources();
				String target=subject.identifyTarget();
		then:	"Source and target are correctly identified"
				srcs==["XXX_A"];
				target=="LC_B";
				subject.isDataAcquisitionMapping()==true;
		
	}
	
	def "Identify Sources -identify correclty a data acquisition mapping for Landing Zone"(){
		
		given:	"""The objects from the setup method"""
				def nd1=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"XXX_A"
					,"UPSTREAM_NODES":[]
					,"DOWNSTREAM_NODES":[1]
				);
			
				def nd2=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"LZ_B"
					,"UPSTREAM_NODES":[1]
					,"DOWNSTREAM_NODES":[]
				);
			
			
				odiObject=BITA_MOCK_ODI_MAPPING nd1,nd2;
				
				
		when:	"The object is constructed and the identify sources is called"
				subject=new OdiMapping(odiObject,mFactory);
				List<String> srcs=subject.identifySources();
				String target=subject.identifyTarget();
		then:	"Source and target are correctly identified"
				srcs==["XXX_A"];
				target=="LZ_B";
				subject.isDataAcquisitionMapping()==true;
		
	}
	
	def "GetScenario - check interaction with other objects when scenario is found"(){
		given:	"The objects from the setup method and our subject"
				IOdiOperationsService mckOpService=Mock();
				IOdiScenario mckScenario=Mock();
				subject=new OdiMapping(odiObject,mFactory);
		when:	"The methog getScenario is called and a scenario is identified"
				IOdiScenario rsp=subject.getScenario("001");
				
		then:	"The interactions are as expected and the result is correct"
				1*mFactory.newOdiOperationsService()>> mckOpService;
				1*mckOpService.findScenarioForMapping(subject,"001")>>mckScenario;
				rsp==mckScenario;
	}
	
	def "GetScenario - check exception throwing when scenatio not found"(){
		given:	"The objects from the setup method and our subject"
				IOdiOperationsService mckOpService=Mock();
				IOdiScenario mckScenario=Mock();
				subject=new OdiMapping(odiObject,mFactory);
		when:	"The methog getScenario is called and a scenario is NOT identified"
				IOdiScenario rsp=subject.getScenario("001");
				
		then:	"The interactions are as expected "
				1*mFactory.newOdiOperationsService()>> mckOpService;
				1*mckOpService.findScenarioForMapping(subject,"001")>>{throw new BitaOdiException("Scenario not found")};
		then:	"And a BitaModelException is thrown"
				thrown BitaModelException;
	}
	
	def "GetParameterListOfScenario - check interaction with other objects when scenario is found"(){
		given:	"The objects from the setup method and our subject"
				IOdiOperationsService mckOpService=Mock();
				IOdiScenario mckScenario=Mock();
				List<String> expectedResult=["CUTU"];
				subject=new OdiMapping(odiObject,mFactory);
		when:	"The methog getScenario is called and a scenario is identified"
				List<String> rsp=subject.getParameterListOfScenario("001");
				
		then:	"The interactions are as expected and the result is correct"
				1*mFactory.newOdiOperationsService()>> mckOpService;
				1*mckOpService.findScenarioForMapping(subject,"001")>>mckScenario;
				1*mckScenario.getParameterList()>>expectedResult;
				rsp==expectedResult;
	}
	
	def "GetParameterListOfScenario - check interaction with other objects when scenario is NOT found"(){
		given:	"The objects from the setup method and our subject"
				IOdiOperationsService mckOpService=Mock();
				IOdiScenario mckScenario=Mock();
				List<String> expectedResult=["CUTU"];
				subject=new OdiMapping(odiObject,mFactory);
		when:	"The methog getScenario is called and a scenario is identified"
				List<String> rsp=subject.getParameterListOfScenario("001");
				
		then:	"The interactions are as expected"
				1*mFactory.newOdiOperationsService()>> mckOpService;
				1*mckOpService.findScenarioForMapping(subject,"001")>>{throw new BitaOdiException("Scenario not found")};
				
		then:	"And a BitaModelException is thrown"
				thrown BitaModelException;
	}


}
