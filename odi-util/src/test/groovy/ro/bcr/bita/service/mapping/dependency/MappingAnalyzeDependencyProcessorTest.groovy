package ro.bcr.bita.service.mapping.dependency

import ro.bcr.bita.mapping.dependency.MappingAnalyzeDependencyProcessor;
import ro.bcr.bita.model.BitaSpockSpecification;
import ro.bcr.bita.model.BitaModelFactoryForTesting;
import ro.bcr.bita.model.IBitaModelFactory;



import ro.bcr.bita.model.MappingDependency;

import oracle.odi.domain.mapping.Mapping;
import oracle.odi.domain.mapping.physical.MapPhysicalNode;
import spock.lang.Specification;


class MappingAnalyzeDependencyProcessorTest extends BitaSpockSpecification{

	MappingAnalyzeDependencyProcessor subject;
	IBitaModelFactory bitaModelFactory=BitaModelFactoryForTesting.newInstance();
	
	def setup() {
		subject=new MappingAnalyzeDependencyProcessor(bitaModelFactory.newMappingDeppendencyRepository());
	}
	
	def "Test result methods - 1 mapping"(){
		given:	"the objects from the setup and 1 mapping"
				MapPhysicalNode nd1=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"LC_A"
					,"UPSTREAM_NODES":[]
					,"DOWNSTREAM_NODES":[1]
				);
				MapPhysicalNode nd2=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"ST_X"
					,"UPSTREAM_NODES":[1]
					,"DOWNSTREAM_NODES":[]
				);
				Mapping mp1=BITA_MOCK_ODI_MAPPING("MP_1",nd1,nd2);
		when:	"the processor is called for that mapping "
				subject.processMapping(bitaModelFactory.newOdiMapping(mp1));
		then:	"the results returned should be correct"
				subject.getAllMappingNames().contains("MP_1");
				subject.getAllMappingNames().size()==1;
				subject.getAllMappings().size()==1;
				subject.getAllMappings().collect{it.getName()}.contains("MP_1");
				Set<MappingDependency> st=subject.getDependencies();
				st.size()==0;
	}
	
	def "Test result methods - 2 mapping no dependency"(){
		given:	"the objects from the setup and 1 mapping"
				MapPhysicalNode nd1=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"LC_A"
					,"UPSTREAM_NODES":[]
					,"DOWNSTREAM_NODES":[1]
				);
				MapPhysicalNode nd2=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"ST_X"
					,"UPSTREAM_NODES":[1]
					,"DOWNSTREAM_NODES":[]
				);
				Mapping mp1=BITA_MOCK_ODI_MAPPING("MP_1",nd1,nd2);
				
				MapPhysicalNode xn1=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"LC_B"
					,"UPSTREAM_NODES":[]
					,"DOWNSTREAM_NODES":[1]
				);
				MapPhysicalNode xn2=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"ST_Y"
					,"UPSTREAM_NODES":[1]
					,"DOWNSTREAM_NODES":[]
				);
				Mapping mp2=BITA_MOCK_ODI_MAPPING("MP_2",xn1,xn2);
		when:	"the processor is called for that mapping "
				subject.processMapping(bitaModelFactory.newOdiMapping(mp1));
				subject.processMapping(bitaModelFactory.newOdiMapping(mp2));
		then:	"the results returned should be correct"
				subject.getAllMappingNames().contains("MP_1");
				subject.getAllMappingNames().contains("MP_2");
				subject.getAllMappingNames().size()==2;
				subject.getAllMappings().size()==2;
				def namesOfMapping=subject.getAllMappings().collect{it.getName()};
				namesOfMapping.contains("MP_1");
				namesOfMapping.contains("MP_2");
				Set<MappingDependency> st=subject.getDependencies();
				st.size()==0;
	}
	
	def "Test result methods - 3 mappings, 2 dependencies"(){
		given:	"the objects from the setup and 1 mapping"
				MapPhysicalNode nd1=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"LC_A"
					,"UPSTREAM_NODES":[]
					,"DOWNSTREAM_NODES":[1]
				);
				MapPhysicalNode nd2=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"ST_X"
					,"UPSTREAM_NODES":[1]
					,"DOWNSTREAM_NODES":[]
				);
				Mapping mp1=BITA_MOCK_ODI_MAPPING("MP_1",nd1,nd2);
				
				
				MapPhysicalNode xn1=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"ST_X"
					,"UPSTREAM_NODES":[]
					,"DOWNSTREAM_NODES":[1]
				);
				MapPhysicalNode xn2=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"ST_Y"
					,"UPSTREAM_NODES":[1]
					,"DOWNSTREAM_NODES":[]
				);
				Mapping mp2=BITA_MOCK_ODI_MAPPING("MP_2",xn1,xn2);
				
				MapPhysicalNode yn1=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"ST_X"
					,"UPSTREAM_NODES":[]
					,"DOWNSTREAM_NODES":[1]
				);
				MapPhysicalNode yn2=BITA_MOCK_ODI_PN(
					"IS_DATA_STORE":Boolean.TRUE
					,"BOUND_OBJECT_NAME":"ST_Z"
					,"UPSTREAM_NODES":[1]
					,"DOWNSTREAM_NODES":[]
				);
				Mapping mp3=BITA_MOCK_ODI_MAPPING("MP_3",yn1,yn2);
		when:	"the processor is called for that mapping "
				subject.processMapping(bitaModelFactory.newOdiMapping(mp1));
				subject.processMapping(bitaModelFactory.newOdiMapping(mp2));
				subject.processMapping(bitaModelFactory.newOdiMapping(mp3));
		then:	"the results returned should be correct"
				subject.getAllMappingNames().contains("MP_1");
				subject.getAllMappingNames().contains("MP_2");
				subject.getAllMappingNames().contains("MP_3");
				subject.getAllMappingNames().size()==3;
				subject.getAllMappings().size()==3;
				def namesOfMapping=subject.getAllMappings().collect{it.getName()};
				namesOfMapping.contains("MP_1");
				namesOfMapping.contains("MP_2");
				namesOfMapping.contains("MP_3");
				Set<MappingDependency> st=subject.getDependencies();
				st.size()==2;
	}
}
