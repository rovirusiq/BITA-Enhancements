package ro.bcr.bita.mapping.analyze

import ro.bcr.bita.mapping.analyze.MappingDependencyAnalyzerProcessor
import ro.bcr.bita.model.BitaDomainFactory;
import ro.bcr.bita.model.BitaSpockSpecification;
import ro.bcr.bita.model.BitaModelFactoryForTesting;
import ro.bcr.bita.model.IBitaDomainFactory;
import ro.bcr.bita.model.IBitaModelFactory;
import ro.bcr.bita.model.BaseMockMappingSpecification
import ro.bcr.bita.model.MappingDependency;
import ro.bcr.bita.odi.proxy.IOdiEntityFactory
import ro.bcr.bita.odi.proxy.OdiProjectPaths
import oracle.odi.domain.mapping.Mapping;
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.mapping.physical.MapPhysicalNode
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiProjectFinder
import spock.lang.Specification
import ro.bcr.bita.odi.proxy.OdiPathUtil
import ro.bcr.bita.odi.proxy.OdiFullMappingPath
import ro.bcr.bita.model.BitaDomainFactoryForMappingsWithMultipleTargets;


class MappingAnalyzeDependencyProcessorSpecification extends BaseMockMappingSpecification{

	MappingDependencyAnalyzerProcessor subject;
	IBitaDomainFactory bitaFactory;
	
	def setup() {
		//there is also a setup in the base class for the common stubs
		bitaFactory=BitaDomainFactoryForMappingsWithMultipleTargets.newInstance(BitaModelFactoryForTesting.newInstance(),stbOdiEntityFactory);
		subject=new MappingDependencyAnalyzerProcessor(bitaFactory.newMappingDeppendencyRepository());
	}
	
	def "Test result methods - 1 mapping"(){
		given:	"the objects from the setup and 1 mapping"
				Mapping mp1=createMockOdiMapping("MP_1","LC_A>>ST_X");
		when:	"the processor is called for that mapping "
				subject.processMapping(bitaFactory.newOdiMapping(mp1));
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
				Mapping mp1=createMockOdiMapping("MP_1","LC_A>>ST_X");
				Mapping mp2=createMockOdiMapping("MP_2","LC_B>>ST_Y");
				
		when:	"the processor is called for that mapping "
				subject.processMapping(bitaFactory.newOdiMapping(mp1));
				subject.processMapping(bitaFactory.newOdiMapping(mp2));
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
				
				Mapping mp1=createMockOdiMapping("MP_1","LC_A>>ST_X");
				Mapping mp2=createMockOdiMapping("MP_2","ST_X>>ST_Y");
				Mapping mp3=createMockOdiMapping("MP_3","ST_X>>ST_Z");
				
		when:	"the processor is called for that mapping "
				subject.processMapping(bitaFactory.newOdiMapping(mp1));
				subject.processMapping(bitaFactory.newOdiMapping(mp2));
				subject.processMapping(bitaFactory.newOdiMapping(mp3));
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
	
	def "Test result methods - complex dependencies"(){
		given:	"the objects from the setup and 1 mapping"
				
				Mapping mp1=createMockOdiMapping("MP_1","""
					LC_A>>ST_X
					LC_B>>ST_X
					LC_C>>ST_Y
					LC_D>>ST_Y
""");
				Mapping mp2=createMockOdiMapping("MP_2",""""
					LC_E>>ST_X
					ST_Y>>ST_X
""");
				Mapping mp3=createMockOdiMapping("MP_3",""""
					LC_D>>ST_Z
					LC_E>>ST_Z
""");
				Mapping mp4=createMockOdiMapping("MP_4",""""
					ST_Z>>CS_A
					ST_X>>CS_A
""");
		when:	"the processor is called for that mapping "
				subject.processMapping(bitaFactory.newOdiMapping(mp1));
				subject.processMapping(bitaFactory.newOdiMapping(mp2));
				subject.processMapping(bitaFactory.newOdiMapping(mp3));
				subject.processMapping(bitaFactory.newOdiMapping(mp4));
		then:	"the results returned should be correct"
				subject.getAllMappingNames().contains("MP_1");
				subject.getAllMappingNames().contains("MP_2");
				subject.getAllMappingNames().contains("MP_3");
				subject.getAllMappingNames().contains("MP_4");
				subject.getAllMappingNames().size()==4;
				subject.getAllMappings().size()==4;
				def namesOfMapping=subject.getAllMappings().collect{it.getName()};
				namesOfMapping.contains("MP_1");
				namesOfMapping.contains("MP_2");
				namesOfMapping.contains("MP_3");
				namesOfMapping.contains("MP_4");
				Set<MappingDependency> st=subject.getDependencies();
				st.size()==4;
	}
}
