package ro.bcr.bita.model

import spock.lang.Ignore;

class OptimizedMappingDependencyRepositoryTest extends BitaSpockSpecification{
	
	OptimizedMappingDependencyRepository subject;
	
	def setup() {
		subject=new OptimizedMappingDependencyRepository();
	}
	
	def "when NO dependency is added, an empty set is returned"(){
		given:	"The objects in the setup"
		when:	"No dependency is added"
		then:	"An empty SET is returned"
				Set<IDependency<String,String>> dpds=subject.getMappingDependencies();
				dpds!=null
				dpds.size()==0;
	}
	
	def "when NO dependency results from the info, an empty set is returned"(){
		given:	"The objects in the setup"
		when:	"dependencies that do not add to mapping dependencies are added"
				subject.addTableToMapping("LC_A","Mapping1");
				subject.addTableToMapping("LC_B","Mapping1");
				subject.addTableToMapping("LC_C","Mapping2");
				subject.addTableToMapping("LC_A","Mapping2");
				subject.addTableToMapping("LC_A","Mapping1");
				subject.addMappingToTable("Mapping1","ST_A");
				subject.addMappingToTable("Mapping2","ST_B");
		then:	"An empty SET is returned"
				Set<IDependency<String,String>> dpds=subject.getMappingDependencies();
				dpds!=null
				dpds.size()==0;
	}
	
	def "Simple dependency between 2 mappings, producer and conusmer"(){
		given:	"The objects in the setup"
		when:	"dependencies that do not add to mapping dependencies are added"
				subject.addTableToMapping("LC_A","Mapping1");
				subject.addTableToMapping("LC_B","Mapping1");
				subject.addTableToMapping("LC_C","Mapping2");
				subject.addTableToMapping("LC_A","Mapping2");
				subject.addTableToMapping("LC_A","Mapping1");
				subject.addMappingToTable("Mapping1","ST_A");
				subject.addMappingToTable("Mapping2","ST_B");
				subject.addTableToMapping("ST_A","Mapping2");
		then:	"An empty SET is returned"
				Set<IDependency<String,String>> dpds=subject.getMappingDependencies();
				dpds!=null
				dpds.size()==1;
				dpds[0].who()=="Mapping2"
				dpds[0].on()=="Mapping1"
	}
	
	def "Simple dependency between 3 mappings, 2 producers and 1 conusmer"(){
		given:	"The objects in the setup"
		when:	"dependencies that do not add to mapping dependencies are added"
				subject.addTableToMapping("LC_A","Mapping1");
				subject.addTableToMapping("LC_B","Mapping1");
				subject.addTableToMapping("LC_C","Mapping2");
				subject.addTableToMapping("LC_A","Mapping2");
				subject.addTableToMapping("LC_A","Mapping1");
				subject.addMappingToTable("Mapping1","ST_A");
				subject.addMappingToTable("Mapping2","ST_B");
				subject.addMappingToTable("Mapping2","ST_D");
				subject.addMappingToTable("Mapping3","ST_C");
				subject.addTableToMapping("ST_A","Mapping2");
				subject.addTableToMapping("ST_D","Mapping3");
		then:	"An empty SET is returned"
				Set<IDependency<String,String>> dpds=subject.getMappingDependencies();
				dpds!=null
				dpds.contains(new MappingDependency("Mapping2","Mapping1"));
				dpds.contains(new MappingDependency("Mapping3","Mapping2"));
				dpds.size()==2;
	}
	
	
	def "Complex depedencies between mappings"(){
		given:	"The correct set is retuned"
		when:	"dependencies that do not add to mapping dependencies are added"
				//Mapping 1
				subject.addTableToMapping("LC_A","Mapping1");
				subject.addTableToMapping("LC_B","Mapping1");
				subject.addMappingToTable("Mapping1","ST_A");
				//Mapping 2
				subject.addTableToMapping("LC_C","Mapping2");
				subject.addTableToMapping("LC_A","Mapping2");
				subject.addMappingToTable("Mapping2","ST_B");
				//Mapping 3
				subject.addTableToMapping("LC_A","Mapping3");
				subject.addTableToMapping("LC_Y","Mapping3");
				subject.addTableToMapping("LC_D","Mapping3");
				subject.addTableToMapping("ST_A","Mapping3");
				subject.addMappingToTable("Mapping3","ST_C");
				subject.addMappingToTable("Mapping3","ST_B");
				//Mapping 4
				subject.addTableToMapping("ST_B","Mapping4");
				subject.addTableToMapping("ST_D","Mapping4");
				subject.addMappingToTable("Mapping4","CR_A");
				//Mapping 5
				subject.addTableToMapping("ST_A","Mapping5");
				subject.addTableToMapping("ST_E","Mapping4");
				subject.addMappingToTable("Mapping5","ST_D");
		then:	"The correct set is retuned"
				Set<IDependency<String,String>> dpds=subject.getMappingDependencies();
				dpds!=null
				dpds.contains(new MappingDependency("Mapping4","Mapping3"));
				dpds.contains(new MappingDependency("Mapping4","Mapping2"));
				dpds.contains(new MappingDependency("Mapping5","Mapping1"));
				dpds.contains(new MappingDependency("Mapping4","Mapping5"));
				dpds.contains(new MappingDependency("Mapping3","Mapping1"));
				dpds.size()==5;
	}
	
	def "0 Cyclic depedencies between mappings"(){
		given:	"The correct set is retuned"
		when:	"dependencies that do not add to mapping dependencies are added"
				//Mapping 1
				subject.addTableToMapping("LC_A","Mapping1");
				subject.addTableToMapping("LC_B","Mapping1");
				subject.addMappingToTable("Mapping1","ST_A");
				//Mapping 2
				subject.addTableToMapping("LC_C","Mapping2");
				subject.addTableToMapping("LC_A","Mapping2");
				subject.addMappingToTable("Mapping2","ST_B");
				//Mapping 3
				subject.addTableToMapping("LC_A","Mapping3");
				subject.addTableToMapping("LC_Y","Mapping3");
				subject.addTableToMapping("LC_D","Mapping3");
				subject.addTableToMapping("ST_A","Mapping3");
				subject.addMappingToTable("Mapping3","ST_C");
				subject.addMappingToTable("Mapping3","ST_B");
				//Mapping 4
				subject.addTableToMapping("ST_B","Mapping4");
				subject.addTableToMapping("ST_D","Mapping4");
				subject.addMappingToTable("Mapping4","CR_A");
				//Mapping 5
				subject.addTableToMapping("ST_A","Mapping5");
				subject.addTableToMapping("ST_E","Mapping4");
				subject.addMappingToTable("Mapping5","ST_D");
				Set<IDependency<String,String>> dpds=subject.getMappingDependenciesAndCheckCyclicDependencies();
		then:	"The correct set is retuned"
				notThrown BitaCyclicDependencyException;
		then:	"The returned set contains some dependencies"
				dpds!=null;
				dpds.size()>0;
	}
	
	@Ignore	
	def "1 Cyclic depedency between mappings"(){
		given:	"The correct set is retuned"
		when:	"dependencies that do not add to mapping dependencies are added"
				//Mapping 1
				subject.addTableToMapping("LC_A","Mapping1");
				subject.addTableToMapping("LC_B","Mapping1");
				subject.addMappingToTable("Mapping1","ST_A");
				//Mapping 2
				subject.addTableToMapping("LC_C","Mapping2");
				subject.addTableToMapping("LC_A","Mapping2");
				subject.addMappingToTable("Mapping2","ST_B");
				//Mapping 3
				subject.addTableToMapping("LC_A","Mapping3");
				subject.addTableToMapping("LC_Y","Mapping3");
				subject.addTableToMapping("LC_D","Mapping3");
				subject.addTableToMapping("ST_A","Mapping3");
				subject.addTableToMapping("CR_A","Mapping3");
				subject.addMappingToTable("Mapping3","ST_C");
				subject.addMappingToTable("Mapping3","ST_B");
				//Mapping 4
				subject.addTableToMapping("ST_B","Mapping4");
				subject.addTableToMapping("ST_D","Mapping4");
				subject.addMappingToTable("Mapping4","CR_A");
				//Mapping 5
				subject.addTableToMapping("ST_A","Mapping5");
				subject.addTableToMapping("ST_E","Mapping4");
				subject.addMappingToTable("Mapping5","ST_D");
				Set<IDependency<String,String>> dpds=subject.getMappingDependenciesAndCheckCyclicDependencies();
		then:	"The correct set is retuned"
				BitaCyclicDependencyException ex=thrown();
				ex.getCyclicDependencies().size()==1;
				(ex.getCyclicDependencies()[0].who().equals("Mapping4") || (ex.getCyclicDependencies()[0].on().equals("Mapping3")))			
	}
	
	@Ignore
	def "2 Cyclic depedencies between mappings"(){
		given:	"The correct set is retuned"
		when:	"dependencies that do not add to mapping dependencies are added"
				//Mapping 1
				subject.addTableToMapping("LC_A","Mapping1");
				subject.addTableToMapping("LC_B","Mapping1");
				subject.addTableToMapping("ST_D","Mapping1");
				subject.addMappingToTable("Mapping1","ST_A");
				//Mapping 2
				subject.addTableToMapping("LC_C","Mapping2");
				subject.addTableToMapping("LC_A","Mapping2");
				subject.addMappingToTable("Mapping2","ST_B");
				//Mapping 3
				subject.addTableToMapping("LC_A","Mapping3");
				subject.addTableToMapping("LC_Y","Mapping3");
				subject.addTableToMapping("LC_D","Mapping3");
				subject.addTableToMapping("ST_A","Mapping3");
				subject.addTableToMapping("CR_A","Mapping3");
				subject.addMappingToTable("Mapping3","ST_C");
				subject.addMappingToTable("Mapping3","ST_B");
				//Mapping 4
				subject.addTableToMapping("ST_B","Mapping4");
				subject.addTableToMapping("ST_D","Mapping4");
				subject.addMappingToTable("Mapping4","CR_A");
				//Mapping 5
				subject.addTableToMapping("ST_A","Mapping5");
				subject.addTableToMapping("ST_E","Mapping4");
				subject.addMappingToTable("Mapping5","ST_D");
				Set<IDependency<String,String>> dpds=subject.getMappingDependenciesAndCheckCyclicDependencies();
		then:	"The correct set is retuned"
				BitaCyclicDependencyException ex=thrown();
				ex.getCyclicDependencies().size()==2;
				List faulty=ex.getCyclicDependencies();
				faulty.contains(new MappingDependency("Mapping4","Mapping3")) || faulty.contains(new MappingDependency("Mapping3","Mapping4"));
				faulty.contains(new MappingDependency("Mapping1","Mapping5")) || faulty.contains(new MappingDependency("Mapping5","Mapping1"));
	}
	

}
