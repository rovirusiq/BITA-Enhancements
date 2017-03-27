package ro.bcr.bita.mapping.analyze

import ro.bcr.bita.mapping.analyze.JcJobIdGenerator
import ro.bcr.bita.mapping.analyze.JcRequestContext
import ro.bcr.bita.mapping.analyze.JcSqlCommandsGenerator
import ro.bcr.bita.mapping.analyze.MappingDependencyAnalyzerProcessor
import ro.bcr.bita.model.IMessageCollection;

import spock.lang.Ignore;
import spock.lang.Specification;

class JcSqlCommandsGeneratorTest extends Specification{
	
	
	private JcSqlCommandsGenerator subject;
	private final String DWH_RELEASE="RELEASE_CD";
	private final String DWH_VERSION="VERSION_CD";
	private final String JOB_GROUP="JOB_GROUP";
	
	private static final List<String> FIXED_LIST_OF_PARAMS=["GLOBAL.GVGRAN_SOURCE_SYSTEM_CD","GLOBAL.GVGRAN_AUDIT_EXECUTION_ID","GLOBAL.GVGRAN_FILE_NAME","GLOBAL.GVGRAN_LOAD_BATCH_ID"];
	
	
	def setup() {
		subject=new JcSqlCommandsGenerator();
	}
	
	
	def "Job Definition - no scenario version provided as parameter"(){
		given:	"The subject defined in setup"
				List<String> expectedResult=JcSqlQueriesTestSupport.getJobDefinition("NA",'MP_A','MP_A','001',DWH_VERSION,DWH_VERSION,"LC_X",true,true);
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				JcRequestContext ctx=new JcRequestContext(DWH_VERSION,grpStrategy);
		when:	"The generateSqlJobDefinition is called with no parameter for scenarioVersion"
				List<String> generatedCode=subject.generateSqlJobDefinition(
					ctx
					,"MP_A"//job id
					,"MP_A"
					,"LC_X"
					,FIXED_LIST_OF_PARAMS
					);
				
		then:	"The code generated is for scenarioVersion 001"
				generatedCode.size()==expectedResult.size();
				generatedCode.size()==4;
		when:	"we extract the strings for easier debug"
				String exp0=expectedResult[0];
				String gen0=generatedCode[0];
				String exp1=expectedResult[1];
				String gen1=generatedCode[1];
				String exp2=expectedResult[2];
				String gen2=generatedCode[2];
				String exp3=expectedResult[3];
				String gen3=generatedCode[3];
		then:	"match each command"
				gen0==exp0;
				gen1==exp1;
				gen2==exp2;
				gen3==exp3;
				
				
	}
	
	
	def "Job Definition - non-default scenario version and delete parameters option"(){
		given:	"The subject defined in setup"		
				List<String> expectedResult=JcSqlQueriesTestSupport.getJobDefinition("NA",'MP_A_V11','MP_A','V11',DWH_VERSION,DWH_VERSION,"LC_X",false,false);
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				JcRequestContext ctx=new JcRequestContext(DWH_VERSION,grpStrategy);
				ctx=ctx.newDependencyDeleteRequired(false);
				ctx=ctx.newParameterDeleteRequired(false);
				ctx=ctx.newScenarioVersion("V11");
		when:	"The generateSqlJobDefinition is called with no parameter for scenarioVersion"
				List<String> generatedCode=subject.generateSqlJobDefinition(
					ctx
					,"MP_A_V11" //job id
					,"MP_A"
					,"LC_X"
					,FIXED_LIST_OF_PARAMS
					);
		then:	"The code generated is for scenarioVersion 001"
				generatedCode.size()==expectedResult.size();
				generatedCode.size()==2;
		when:	"we extract the strings for easier debug"
				String exp0=expectedResult[0];
				String gen0=generatedCode[0];
				String exp1=expectedResult[1];
				String gen1=generatedCode[1];
		then:	"match each command"
				gen0==exp0;
				gen1==exp1;
	}
	
	def "Job Dependency - init"(){
		given:	"The subject"
				List<String> expectedResult=JcSqlQueriesTestSupport.getJobDependencyInit('MP_A',DWH_RELEASE,DWH_VERSION);
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				JcRequestContext ctx=new JcRequestContext(DWH_VERSION,grpStrategy);
		when:	"The generateSqlDependencyInit mehtod is called"
				List<String> generatedCode=subject.generateSqlDependencyInit(
						ctx
						,'MP_A'
					);
		then:	"The code generated matches the expected result"
				
				generatedCode==expectedResult;
	}
	
	def "Job Dependency - definition"(){
		given:	"The subject"
				List<String> expectedResult=JcSqlQueriesTestSupport.getJobDependencyDefinition('MP_B','MP_A',DWH_VERSION,DWH_VERSION);
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				JcRequestContext ctx=new JcRequestContext(DWH_VERSION,grpStrategy);
		when:	"The generateSqlDependency mehtod is called"
				List<String> generatedCode=subject.generateSqlDependency(
						ctx
						,'MP_A'
						,'MP_B'
					);
		then:	"The code generated matches the expected result"
				
				generatedCode==expectedResult;
	}
	
	def "Group2Job - association"(){
		given:	"The subject"
				List<String> expectedResult=JcSqlQueriesTestSupport.getJobGroupJobAssociation(JOB_GROUP,'MP_A',DWH_VERSION,DWH_VERSION);
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				JcRequestContext ctx=new JcRequestContext(DWH_VERSION,grpStrategy);
		when:	"The generateSqlGroupJobs mehtod is called"
				List<String> generatedCode=subject.generateSqlGroupJobs(
					ctx
					,JOB_GROUP
					,"MP_A"
					);
		then:	"The code generated matches the expected result"
				
				generatedCode==expectedResult;
	}			
	
	def "Group - definition"(){
		given:	"The subject"
				List<String> expectedResult=JcSqlQueriesTestSupport.getJobGroupDefinition(JOB_GROUP);
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				JcRequestContext ctx=new JcRequestContext(DWH_VERSION,grpStrategy);
		when:	"The generateSqlGroupDefinition mehtod is called"
				List<String> generatedCode=subject.generateSqlGroupDefinition(
					ctx
					,JOB_GROUP
					);
				def rsp=generatedCode as List;
		then:	"The code generated matches the expected result"
				generatedCode==expectedResult;
	}

}
