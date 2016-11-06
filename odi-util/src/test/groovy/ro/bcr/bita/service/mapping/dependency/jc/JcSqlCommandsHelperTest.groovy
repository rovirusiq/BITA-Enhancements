package ro.bcr.bita.service.mapping.dependency.jc

import ro.bcr.bita.mapping.dependency.MappingDependencyAnalyzerProcessor;
import ro.bcr.bita.mapping.dependency.jc.JcJobIdGenerator;
import ro.bcr.bita.mapping.dependency.jc.JcParameters;
import ro.bcr.bita.mapping.dependency.jc.JcSqlGenerator;
import ro.bcr.bita.mapping.dependency.jc.JcSqlCommandsHelper;
import ro.bcr.bita.model.IMessageCollection;

import spock.lang.Ignore;
import spock.lang.Specification;

class JcSqlCommandsHelperTest extends Specification{
	
	
	private JcSqlCommandsHelper subject;
	private final String DWH_RELEASE="RELEASE_CD";
	private final String DWH_VERSION="VERSION_CD";
	private final String JOB_GROUP="JOB_GROUP";
	
	private static final List<String> FIXED_LIST_OF_PARAMS=["GLOBAL.GVGRAN_SOURCE_SYSTEM_CD","GLOBAL.GVGRAN_AUDIT_EXECUTION_ID","GLOBAL.GVGRAN_FILE_NAME","GLOBAL.GVGRAN_LOAD_BATCH_ID"];
	
	
	def setup() {
		subject=new JcSqlCommandsHelper();
	}
	
	
	def "Job Definition - no scenario version provided as parameter"(){
		given:	"The subject defined in setup"
		when:	"The generateSqlJobDefinition is called with no parameter for scenarioVersion"
				List<String> generatedCode=subject.generateSqlJobDefinition(
					JcParameters.newParameters(JOB_GROUP,DWH_RELEASE,DWH_VERSION)
					,"MP_A"//job id
					,"MP_A"
					,"LC_X"
					,["GLOBAL.GVGRAN_SOURCE_SYSTEM_CD","GLOBAL.GVGRAN_AUDIT_EXECUTION_ID","GLOBAL.GVGRAN_FILE_NAME","GLOBAL.GVGRAN_LOAD_BATCH_ID"]
					,true
					);
		then:	"The code generated is for scenarioVersion 001"
				List<String> expectedResult=JcSqlQueriesTestSupport.getJobDefinition(JOB_GROUP,'MP_A','MP_A','001',DWH_RELEASE,DWH_VERSION,"LC_X",true,true);
				int max=Math.max(generatedCode.size(),expectedResult.size());
				//generatedCode==expectedResult;
				//FOR DEBUG
				generatedCode[0]==expectedResult[0];
				generatedCode[1]==expectedResult[1];
				generatedCode[2]==expectedResult[2];
				generatedCode[3]==expectedResult[3];
	}
	
	
	def "Job Definition - non-default scenario version and delete parameters option"(){
		given:	"The subject defined in setup"		
		when:	"The generateSqlJobDefinition is called with no parameter for scenarioVersion"
				List<String> generatedCode=subject.generateSqlJobDefinition(
					JcParameters.newParameters("JOB_GROUP","RELEASE_CD","VERSION_CD","V11",new JcJobIdGenerator(),false)
					,"MP_A_V11" //job id
					,"MP_A"
					,"LC_X"
					,FIXED_LIST_OF_PARAMS
					,false
					);
		then:	"The code generated is for scenarioVersion 001"
				List<String> expectedResult=JcSqlQueriesTestSupport.getJobDefinition(JOB_GROUP,'MP_A_V11','MP_A','V11',DWH_RELEASE,DWH_VERSION,"LC_X",false,false);
				int max=Math.max(generatedCode.size(),expectedResult.size());
				generatedCode==expectedResult;
				//FOR DEBUG
				//generatedCode[0]==expectedResult[0];
				//generatedCode[1]==expectedResult[1];
				//generatedCode[2]==expectedResult[2];
	}
	
	def "Job Dependency - init"(){
		given:	"The subject"
		when:	"The generateSqlDependencyInit mehtod is called"
				List<String> generatedCode=subject.generateSqlDependencyInit(
						JcParameters.newParameters(JOB_GROUP,DWH_RELEASE,DWH_VERSION)
						,'MP_A'
					);
		then:	"The code generated matches the expected result"
				List<String> expectedResult=JcSqlQueriesTestSupport.getJobDependencyInit('MP_A',DWH_RELEASE,DWH_VERSION);
				generatedCode==expectedResult;
	}
	
	def "Job Dependency - definition"(){
		given:	"The subject"
		when:	"The generateSqlDependency mehtod is called"
				List<String> generatedCode=subject.generateSqlDependency(
						JcParameters.newParameters(JOB_GROUP,DWH_RELEASE,DWH_VERSION)
						,'MP_A'
						,'MP_B'
					);
		then:	"The code generated matches the expected result"
				List<String> expectedResult=JcSqlQueriesTestSupport.getJobDependencyDefinition('MP_B','MP_A',DWH_RELEASE,DWH_VERSION);
				generatedCode==expectedResult;
	}
	
	def "Group2Job - association"(){
		given:	"The subject"
		when:	"The generateSqlGroupJobs mehtod is called"
				List<String> generatedCode=subject.generateSqlGroupJobs(
					JcParameters.newParameters(JOB_GROUP,DWH_RELEASE,DWH_VERSION)
					,"MP_A"
					);
		then:	"The code generated matches the expected result"
				List<String> expectedResult=JcSqlQueriesTestSupport.getJobGroupJobAssociation(JOB_GROUP,'MP_A',DWH_RELEASE,DWH_VERSION);
				generatedCode==expectedResult;
	}			
	
	def "Group - definition"(){
		given:	"The subject"
		when:	"The generateSqlGroupDefinition mehtod is called"
				List<String> generatedCode=subject.generateSqlGroupDefinition(
					JcParameters.newParameters(JOB_GROUP,DWH_RELEASE,DWH_VERSION)
					);
				def rsp=generatedCode as List;
		then:	"The code generated matches the expected result"
				List<String> expectedResult=JcSqlQueriesTestSupport.getJobGroupDefinition(JOB_GROUP);
				generatedCode==expectedResult;
				//FOR DEBUG
				//generatedCode[0]==expectedResult[0];
				//generatedCode[1]==expectedResult[1];
				//generatedCode[2]==expectedResult[2];
	}

}
