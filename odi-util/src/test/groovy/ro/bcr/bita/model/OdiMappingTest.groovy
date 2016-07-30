package ro.bcr.bita.model

import oracle.odi.domain.mapping.Mapping

//TODO add more specifc tests for source identification and target identification
//TODO add tests for query generation and execution
class OdiMappingTest extends BitaMockModelFactory{
	
	def OdiMapping subject;
	def Mapping odiObject;
	
	def setup() {
		odiObject=Mock();
	}
	
	def "Constructor - when the constructor parameter is null, an exception is thrown"(){
		given:	"""The objects from the setup method"""
		when:	"The object is constructed with null parameter"
				subject=new OdiMapping(null);
		then:	"An exception is thrown"
				thrown BitaModelException;
	}
	
	def "Delegation - when a simple interface method is used, it is delegated directly to the odi object"(){
		given:	"""The objects from the setup method"""
				def cutu=3;
				def nbr;
		when:	"The object is constructed and a simple method is called"
				subject=new OdiMapping(odiObject);
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
				subject=new OdiMapping(odiObject);
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
				subject=new OdiMapping(odiObject);
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
				subject=new OdiMapping(odiObject);
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
				subject=new OdiMapping(odiObject);
				List<String> srcs=subject.identifySources();
		then:	"An exception is thrown"
				BitaModelException ex=thrown();
				ex.message.startsWith("More than one target");
	}
	
	def "Identify Sources -when mapping is analyzed, source and targets are correclty identified"(){
		
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
				subject=new OdiMapping(odiObject);
				List<String> srcs=subject.identifySources();
				String target=subject.identifyTarget();
		then:	"Source and target are correctly identified"
				srcs==["LC_A","LC_B","LC_C"];
				target=="ST_A";
		
	}


}
