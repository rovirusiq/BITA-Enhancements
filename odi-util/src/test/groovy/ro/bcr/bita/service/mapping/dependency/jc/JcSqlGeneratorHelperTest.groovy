package ro.bcr.bita.service.mapping.dependency.jc

import ro.bcr.bita.mapping.dependency.MappingAnalyzeDependencyProcessor;
import ro.bcr.bita.mapping.dependency.jc.JcParameters;
import ro.bcr.bita.mapping.dependency.jc.JcSqlGenerator;
import ro.bcr.bita.mapping.dependency.jc.JcSqlGeneratorHelper;
import ro.bcr.bita.model.IMessageCollection;

import spock.lang.Specification;

class JcSqlGeneratorHelperTest extends Specification{
	
	
	private JcSqlGeneratorHelper subject;
	private final String DWH_RELEASE="RELEASE_CD";
	private final String DWH_VERSION="VERSION_CD";
	private final String JOB_GROUP="JOB_GROUP";
	
	
	def setup() {
		subject=new JcSqlGeneratorHelper();
	}
	
	
	def "Job Definition - no scenario version provided as parameter"(){
		given:	"The subject defined in setup"
		when:	"The generateSqlJobDefinition is called with no parameter for scenarioVersion"
				String generatedCode=subject.generateSqlJobDefinition(
					JcParameters.newParameters(JOB_GROUP,DWH_RELEASE,DWH_VERSION)
					,"MP_A"
					);
		then:	"The code generated is for scenarioVersion 001"
				JcSqlQueriesTestSupport.getJobDefinition('MP_A','MP_A','001',DWH_RELEASE,DWH_VERSION).equals(generatedCode);
	}
	
	
	def "Job Definition - different scenario version as parameter"(){
		given:	"The subject defined in setup"		
		when:	"The generateSqlJobDefinition is called with no parameter for scenarioVersion"
				String generatedCode=subject.generateSqlJobDefinition(
					JcParameters.newParameters("JOB_GROUP","RELEASE_CD","VERSION_CD","V11")
					,"MP_A"
					);
		then:	"The code generated is for scenarioVersion 001"
				JcSqlQueriesTestSupport.getJobDefinition('MP_A_V11','MP_A','V11',DWH_RELEASE,DWH_VERSION).equals(generatedCode);
	}
	
	
	def "Job Parameters - definition"(){
		given:	"The subject and the expected result"
				
		when:	"The generateSqlJobParameters method is called"
				String generatedCode=subject.generateSqlJobParameters(
					JcParameters.newParameters(JOB_GROUP,DWH_RELEASE,DWH_VERSION)
					,"MP_A"
					,"LC_A"
					);
		then:	"The code generated matches the expected result"
				JcSqlQueriesTestSupport.getJobParametersDefinition(JOB_GROUP,'MP_A','LC_A',DWH_RELEASE,DWH_VERSION).equals(generatedCode);
	}
	
	
	def "Job Dependency - cleanup"(){
		given:	"The subject"
		when:	"The generateSqlDepCleanup mehtod is called"
				String generatedCode=subject.generateSqlDepCleanup(
						JcParameters.newParameters(JOB_GROUP,DWH_RELEASE,DWH_VERSION)
						,'MP_A'
					);
		then:	"The code generated matches the expected result"
				JcSqlQueriesTestSupport.getJobDependencyCleanup('MP_A',DWH_RELEASE,DWH_VERSION).equals(generatedCode);
	}
	
	def "Job Dependency - definition"(){
		given:	"The subject"
		when:	"The generateSqlDependency mehtod is called"
				String generatedCode=subject.generateSqlDependency(
						JcParameters.newParameters(JOB_GROUP,DWH_RELEASE,DWH_VERSION)
						,'MP_A'
						,'MP_B'
					);
		then:	"The code generated matches the expected result"
				JcSqlQueriesTestSupport.getJobDependencyDefinition('MP_B','MP_A',DWH_RELEASE,DWH_VERSION).equals(generatedCode);
	}
	
	def "Group2Job - association"(){
		given:	"The subject"
		when:	"The generateSqlGroupJobs mehtod is called"
				String generatedCode=subject.generateSqlGroupJobs(
					JcParameters.newParameters(JOB_GROUP,DWH_RELEASE,DWH_VERSION)
					,"MP_A"
					);
		then:	"The code generated matches the expected result"
				JcSqlQueriesTestSupport.getJobGroupJobAssociation(JOB_GROUP,'MP_A',DWH_RELEASE,DWH_VERSION).equals(generatedCode);
	}			
	
	def "Group - definition"(){
		given:	"The subject"
		when:	"The generateSqlGroupDefinition mehtod is called"
				String[] generatedCode=subject.generateSqlGroupDefinition(
					JcParameters.newParameters(JOB_GROUP,DWH_RELEASE,DWH_VERSION)
					);
				def rsp=generatedCode as List;
		then:	"The code generated matches the expected result"
				rsp.containsAll(
						JcSqlQueriesTestSupport.getJobGroupDefinition(JOB_GROUP)
						,JcSqlQueriesTestSupport.getJobGroupCleanup(JOB_GROUP)
						,JcSqlQueriesTestSupport.getJobGroupJobAssociationCleanup(JOB_GROUP)
				);
				rsp.size()==3;
		
	}

}
