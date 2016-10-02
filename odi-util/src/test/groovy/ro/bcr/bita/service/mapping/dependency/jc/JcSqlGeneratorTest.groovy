package ro.bcr.bita.service.mapping.dependency.jc

import ro.bcr.bita.mapping.dependency.MappingAnalyzeDependencyProcessor;
import ro.bcr.bita.mapping.dependency.jc.JcParameters
import ro.bcr.bita.mapping.dependency.jc.JcSqlGenerator;
import ro.bcr.bita.model.IMessageCollection;
import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.model.MappingDependency;
import ro.bcr.bita.model.MessageCollection;
import ro.bcr.bita.model.MessageCollectionUtil

import spock.lang.Specification

class JcSqlGeneratorTest extends Specification{
	
	JcSqlGenerator subject;
	MappingAnalyzeDependencyProcessor processor;
	JcParameters params;
	IMessageCollection sqlDependency;
	IMessageCollection sqlGroup;
	IMessageCollection sqlJob;
	
	def setup() {
		processor=Mock();
		subject=new JcSqlGenerator(processor);
		params=JcParameters.newParameters("JC_BMF","R1","V1");
		sqlDependency=new MessageCollection("test1");
		sqlGroup=new MessageCollection("test2");
		sqlJob=new MessageCollection("test3");
	}
	
	def "Dependency SQL - No mapings, empty set of dependencies"(){
		given:	"The objects from setup and an empty set of dependencies"
				Set<MappingDependency> stDeps=[];
				Set<IOdiMapping> myMappings=[];
		when:	"The generateSql method is called"
				subject.generateSql(params,sqlGroup,sqlJob,sqlDependency);
		then:	"the interactions are as below"
				1 * processor.getAllMappings() >> myMappings;
				0 * processor.getDependencies() >> stDeps;
		then:	"we will get empty IMessageCollection(s) with sqls"
				IMessageCollection sqlDep=sqlDependency;
				IMessageCollection sqlGroup=sqlGroup;
				IMessageCollection sqlParameter=sqlJob;
				sqlDep.getNoOfKeys()==0;
				sqlGroup.getNoOfKeys()==0;
				sqlParameter.getNoOfKeys()==0;
	}
	
	def "Dependency SQL - 1 mapping and empty set of dependencies"(){
		given:	"The objects from setup a Set of maps and  a Set of dependencies"
				Set<MappingDependency> stDeps=[];
				IOdiMapping myMap1=Mock();
				Set<IOdiMapping> myMappings=[myMap1];
		when:	"The generateSql method is called"
				subject.generateSql(params,sqlGroup,sqlJob,sqlDependency);
		then:	"the interactions are as below"
				1 * processor.getAllMappings() >> myMappings;
				myMap1.getName() >> "MP_A";
				myMap1.getLeadingSource() >> "LC_A";
				1 * processor.getDependencies() >> stDeps;
		then:	"we get sql defintions in for group definition and group parameters " 
				IMessageCollection sqlDep=sqlDependency;
				IMessageCollection sqlGroup=sqlGroup;
				IMessageCollection sqlParameter=sqlJob;
				sqlDep.getNoOfKeys()==1;
				sqlDep.getNoOfMessagesForKey("MP_A")==1;//delete dependencies
				sqlGroup.getNoOfKeys()==2;//one group, one mapping
				sqlGroup.getNoOfMessagesForKey(params.jobGroupName)==3;//1 delete associations, 1 delete,1 create
				sqlGroup.getNoOfMessagesForKey("MP_A")==1;//1 association group job
				sqlParameter.getNoOfKeys()==1;//one mapping
				sqlParameter.getNoOfMessagesForKey("MP_A")==2;//1 merge for job definition, 1 merge for parameters
	}

	
	def "Dependency SQL - 2 mapping and 1 dependencies, same IMessageCollection"(){
		given:	"The objects from setup a Set of maps and  a Set of dependencies"
				Set<MappingDependency> stDeps=[
					new MappingDependency("MP_A","MP_B")
				];
				
				IOdiMapping myMap1=Mock();
				IOdiMapping myMap2=Mock();
				Set<IOdiMapping> myMappings=[myMap1,myMap2];
				sqlDependency=new MessageCollection("newTest");
				sqlGroup=sqlDependency;
				sqlJob=sqlDependency;
		when:	"The generateSql method is called"
				subject.generateSql(params,sqlGroup,sqlJob,sqlDependency);
		then:	"the interactions are as below"
				processor.getAllMappings() >> myMappings;
				processor.getDependencies() >> stDeps;
				myMap1.getName() >> "MP_A";
				myMap1.getLeadingSource() >> "LC_A";
				myMap2.getName() >> "MP_B";
				myMap2.getLeadingSource() >> "LC_B";
		then:	"we see check to see the sqls were generated in the same collection"
				IMessageCollection sqlDep=sqlDependency;
				IMessageCollection sqlGroup=sqlGroup;
				IMessageCollection sqlParameter=sqlJob;
				sqlDep==sqlGroup;
				sqlDep==sqlParameter;
				sqlDep.getNoOfKeys()==3;//1 group, 2 mappings
				sqlDep.getKeys().containsAll("$params.jobGroupName".toString(),"MP_A","MP_B");
	}
	
	def "Dependency SQL - 3 mapping and 2 dependencies"(){
		given:	"The objects from setup a Set of maps and  a Set of dependencies"
				Set<MappingDependency> stDeps=[
					new MappingDependency("MP_A","MP_B")
					,new MappingDependency("MP_A","MP_C")
				];
				IOdiMapping myMap1=Mock();
				IOdiMapping myMap2=Mock();
				IOdiMapping myMap3=Mock();
				Set<IOdiMapping> myMappings=[myMap1,myMap2,myMap3];
		when:	"The generateSql method is called"
				subject.generateSql(params,sqlGroup,sqlJob,sqlDependency);
		then:	"the interactions are as below"
				1 * processor.getAllMappings() >> myMappings;
				myMap1.getName() >> "MP_A";
				myMap1.getLeadingSource() >> "LC_A";
				myMap2.getName() >> "MP_B";
				myMap2.getLeadingSource() >> "LC_B";
				myMap3.getName() >> "MP_C";
				myMap3.getLeadingSource() >> "LC_C";
				1 * processor.getDependencies() >> stDeps;
		then:	"we get sql defintions in for group definition and group parameters "
				IMessageCollection sqlDep=sqlDependency;
				IMessageCollection sqlGroup=sqlGroup;
				IMessageCollection sqlParameter=sqlJob;
				sqlDep.getNoOfKeys()==3;
				sqlDep.getNoOfMessagesForKey("MP_A")==3;//1 delete dependencies, add 2 dependency
				sqlDep.getNoOfMessagesForKey("MP_B")==1;//1 delete dependencies
				sqlDep.getNoOfMessagesForKey("MP_C")==1;//1 delete dependencies
				sqlGroup.getNoOfKeys()==4;//one group, 3 mappings
				sqlGroup.getNoOfMessagesForKey(params.jobGroupName)==3;//1 delete associations, 1 delete, 1 create
				sqlGroup.getNoOfMessagesForKey("MP_A")==1;//1 association
				sqlGroup.getNoOfMessagesForKey("MP_B")==1;//1 association
				sqlGroup.getNoOfMessagesForKey("MP_C")==1;//1 association
				sqlParameter.getNoOfKeys()==3;//3 mapping
				sqlParameter.getNoOfMessagesForKey("MP_A")==2;//1 merge, 1 merge parameters
				sqlParameter.getNoOfMessagesForKey("MP_B")==2;//1 merge, 1 merge parameters
				sqlParameter.getNoOfMessagesForKey("MP_C")==2;//1 merge, 1 merge parameters
		then:	"we test the queries for the group definition"
				
				sqlGroup.getMessagesForKey(params.jobGroupName).contains(JcSqlQueriesTestSupport.getJobGroupJobAssociationCleanup(params.jobGroupName));
				sqlGroup.getMessagesForKey(params.jobGroupName).contains(JcSqlQueriesTestSupport.getJobGroupCleanup(params.jobGroupName));
				sqlGroup.getMessagesForKey(params.jobGroupName).contains(JcSqlQueriesTestSupport.getJobGroupDefinition(params.jobGroupName));
				
		then:	"we test the queries for the group to job association"
		
				sqlGroup.getMessagesForKey('MP_A').contains(JcSqlQueriesTestSupport.getJobGroupJobAssociation(params.jobGroupName,'MP_A',params.dwhRelease,params.dwhVersion));
				sqlGroup.getMessagesForKey('MP_B').contains(JcSqlQueriesTestSupport.getJobGroupJobAssociation(params.jobGroupName,'MP_B',params.dwhRelease,params.dwhVersion));
				sqlGroup.getMessagesForKey('MP_C').contains(JcSqlQueriesTestSupport.getJobGroupJobAssociation(params.jobGroupName,'MP_C',params.dwhRelease,params.dwhVersion));
				
		then:	"we test queries for job definition"
		
				sqlParameter.getMessagesForKey("MP_A").contains(JcSqlQueriesTestSupport.getJobDefinition('MP_A','MP_A','001',params.dwhRelease,params.dwhVersion));
		
		then:	"we test the queries for the paramters of MP_B"
		
				sqlParameter.getMessagesForKey("MP_B").contains(JcSqlQueriesTestSupport.getJobParametersDefinition(params.jobGroupName,'MP_B','LC_B',params.dwhRelease,params.dwhVersion));
		
		then:	"we test the queries for dependenices for MP_A"
				
				sqlDep.getMessagesForKey("MP_A").contains(JcSqlQueriesTestSupport.getJobDependencyCleanup('MP_A',params.dwhRelease,params.dwhVersion));
				sqlDep.getMessagesForKey("MP_A").contains(JcSqlQueriesTestSupport.getJobDependencyDefinition('MP_B','MP_A',params.dwhRelease,params.dwhVersion));
	}	
	
}
