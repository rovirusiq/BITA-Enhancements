package ro.bcr.bita.mapping.analyze

import ro.ns.spock.base.odi.IInitSubjectTestHelper
import spock.lang.Specification
import ro.bcr.bita.mapping.analyze.IJcJobIdGenerator

class JcRequestContextSpecification extends Specification implements IInitSubjectTestHelper{
	
	JcRequestContext subject;
	
	def initSubject(Map params=[:]) {
		
		String dwhVersion=checkParam(params,"dwhVersion"){
			return "JC_DWH_VERSION";
		}
		IJcGroupIdentificationStrategy groupIdStrategy=checkParam(params,"groupIdentificationStrategy"){
			IJcGroupIdentificationStrategy grpStrategy=Stub();
			return grpStrategy;
		}
		JcRequestContext rsp=new JcRequestContext(dwhVersion,groupIdStrategy);
		return rsp;
	}
	
	def "Test constructor"(){
		when:	"subject is instantiated by factory method with default values"
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				subject=initSubject(dwhVersion:"JC_TEST",groupIdentificationStrategy:grpStrategy);
		then:	"all the transmited values are applied"
				subject.dwhVersion=="JC_TEST"
				subject.groupIdentificationStrategy.is(grpStrategy)
		and:	"all default values are applied"
				subject.dwhRelease == subject.dwhVersion
				subject.scenarioVersion == "001"
				subject.isParameterDeleteRequired == true
				subject.isDependencyDeleteRequired == true
				subject.jcSchemaName == "O_LDS_META"
				subject.jobIdGenerator != null
	}
	
	def "Test newDwhRelease"(){
		given:	"our initialized subject"
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				subject=initSubject(dwhVersion:"JC_TEST",groupIdentificationStrategy:grpStrategy);
		when:	"method newDwhRelease is called"
				JcRequestContext newCtx=subject.newDwhRelease("newReleaase");
		then:	"all the values are inherited from the subject"
				newCtx.dwhVersion==subject.dwhVersion
				newCtx.groupIdentificationStrategy.is(subject.groupIdentificationStrategy)
				newCtx.scenarioVersion == subject.scenarioVersion
				newCtx.isParameterDeleteRequired == subject.isParameterDeleteRequired
				newCtx.isDependencyDeleteRequired == subject.isDependencyDeleteRequired
				newCtx.jcSchemaName == subject.jcSchemaName
				newCtx.jobIdGenerator.is(subject.jobIdGenerator)
		and:	"except the new value for release"
				newCtx.dwhRelease == "newReleaase"
	}
	
	def "Test newJcSchemaName"(){
		given:	"our initialized subject"
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				subject=initSubject(dwhVersion:"JC_TEST",groupIdentificationStrategy:grpStrategy);
		when:	"method newJcSchemaName is called"
				JcRequestContext newCtx=subject.newJcSchemaName("newSchema");
		then:	"all the values are inherited from the subject"
				newCtx.dwhVersion==subject.dwhVersion
				newCtx.dwhRelease==subject.dwhRelease
				newCtx.groupIdentificationStrategy.is(subject.groupIdentificationStrategy)
				newCtx.scenarioVersion == subject.scenarioVersion
				newCtx.isParameterDeleteRequired == subject.isParameterDeleteRequired
				newCtx.isDependencyDeleteRequired == subject.isDependencyDeleteRequired
				newCtx.jobIdGenerator.is(subject.jobIdGenerator)
		and:	"except the new value for jcSchemaName"
				newCtx.jcSchemaName == "newSchema"
	}
	
	def "Test newJobIdGenerator"(){
		given:	"our initialized subject"
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				subject=initSubject(dwhVersion:"JC_TEST",groupIdentificationStrategy:grpStrategy);
		when:	"method newJcSchemaName is called"
				IJcJobIdGenerator jobIdGen=Stub()
				JcRequestContext newCtx=subject.newJobIdGenerator(jobIdGen);
		then:	"all the values are inherited from the subject"
				newCtx.dwhVersion==subject.dwhVersion
				newCtx.dwhRelease==subject.dwhRelease
				newCtx.groupIdentificationStrategy.is(subject.groupIdentificationStrategy)
				newCtx.scenarioVersion == subject.scenarioVersion
				newCtx.isParameterDeleteRequired == subject.isParameterDeleteRequired
				newCtx.isDependencyDeleteRequired == subject.isDependencyDeleteRequired
				newCtx.jcSchemaName == subject.jcSchemaName
				
		and:	"except the new value for newJobIdGenerator"
				newCtx.jobIdGenerator.is(jobIdGen)
	}
	
	def "Test newScenarioVersion"(){
		given:	"our initialized subject"
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				subject=initSubject(dwhVersion:"JC_TEST",groupIdentificationStrategy:grpStrategy);
		when:	"method newScenarioVersion is called"
				JcRequestContext newCtx=subject.newScenarioVersion("newVersion");
		then:	"all the values are inherited from the subject"
				newCtx.dwhVersion==subject.dwhVersion
				newCtx.dwhRelease==subject.dwhRelease
				newCtx.groupIdentificationStrategy.is(subject.groupIdentificationStrategy)
				newCtx.isParameterDeleteRequired == subject.isParameterDeleteRequired
				newCtx.isDependencyDeleteRequired == subject.isDependencyDeleteRequired
				newCtx.jcSchemaName == subject.jcSchemaName
				newCtx.jobIdGenerator.is(subject.jobIdGenerator)
		and:	"except the new value for newJobIdGenerator"
				newCtx.scenarioVersion == "newVersion"
	}
	
	def "Test newParameterDeleteRequired"(){
		given:	"our initialized subject"
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				subject=initSubject(dwhVersion:"JC_TEST",groupIdentificationStrategy:grpStrategy);
		when:	"method newParameterDeleteRequired is called"
				JcRequestContext newCtx=subject.newParameterDeleteRequired(false);
		then:	"all the values are inherited from the subject"
				newCtx.dwhVersion==subject.dwhVersion
				newCtx.dwhRelease==subject.dwhRelease
				newCtx.groupIdentificationStrategy.is(subject.groupIdentificationStrategy)
				newCtx.isDependencyDeleteRequired == subject.isDependencyDeleteRequired
				newCtx.jcSchemaName == subject.jcSchemaName
				newCtx.scenarioVersion == subject.scenarioVersion
				newCtx.jobIdGenerator.is(subject.jobIdGenerator)
		and:	"except the new value for newJobIdGenerator"
				newCtx.isParameterDeleteRequired == false
	}
	
	def "Test newDependencyDeleteRequired"(){
		given:	"our initialized subject"
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				subject=initSubject(dwhVersion:"JC_TEST",groupIdentificationStrategy:grpStrategy);
		when:	"method newDependencyDeleteRequired is called"
				JcRequestContext newCtx=subject.newDependencyDeleteRequired(false);
		then:	"all the values are inherited from the subject"
				newCtx.dwhVersion==subject.dwhVersion
				newCtx.dwhRelease==subject.dwhRelease
				newCtx.groupIdentificationStrategy.is(subject.groupIdentificationStrategy)
				newCtx.isParameterDeleteRequired == subject.isParameterDeleteRequired
				newCtx.jcSchemaName == subject.jcSchemaName
				newCtx.scenarioVersion == subject.scenarioVersion
				newCtx.jobIdGenerator.is(subject.jobIdGenerator)
		and:	"except the new value for newJobIdGenerator"
				newCtx.isDependencyDeleteRequired == false
	}
	
	def "Test create job id delegation"(){
		given:	"our initialized subject"
				IJcGroupIdentificationStrategy grpStrategy=Stub();
				IJcJobIdGenerator mckJobIdGen=Mock()
				subject=initSubject(dwhVersion:"JC_TEST",groupIdentificationStrategy:grpStrategy);
				subject=subject.newJobIdGenerator(mckJobIdGen)
		when:	"the generateId is called"
				String rsp=subject.generateJobId("forAMap");
		then:	"the interactions are as expected"
				1 * mckJobIdGen.generateJobId(subject,"forAMap") >> "jobID"	
				0 * _
		and:	"response is as expected"
				rsp == "jobID"
		
	}
	
	def "Test create groups delegation"(){
		given:	"our initialized subject"
				IJcGroupIdentificationStrategy mckGrpStrategy=Mock();
				subject=initSubject(dwhVersion:"JC_TEST",groupIdentificationStrategy:mckGrpStrategy);
		when:	"the generateId is called"
				String rsp=subject.createGroups(null,null);
		then:	"the interactions are as expected"
				1 * mckGrpStrategy.createGroups(subject,null,null)
				0 * _
		
	}

}
