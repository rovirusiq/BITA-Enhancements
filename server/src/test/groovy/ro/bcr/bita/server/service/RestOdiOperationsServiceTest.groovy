package ro.bcr.bita.server.service

import ro.bcr.bita.odi.proxy.IOdiRemoteOperations
import ro.bcr.bita.odi.proxy.OdiScenExecutionEnv
import ro.bcr.bita.odi.proxy.OdiScenExecutionStatus

import spock.lang.Specification

class RestOdiOperationsServiceTest extends Specification{
	
	private IOdiRemoteOperations mckOdiService=Mock();
	private OdiScenExecutionEnv mckOdiEnv=Mock();
	private RestOdiOperationsService subject=new RestOdiOperationsService(mckOdiEnv,mckOdiService);
	
	
	def setup() {
	}
	
	def "Rest Execute Scenario - test interaction with external dependency"(){
		given:	"The current setup"
				String scenName="TEST_SCENARIO";
				String scenVersion="001";
				String execDescription="Test call scenario";
				HashMap<String,String> scenParams=[:];
				
				String scenExecCode="DONE";
				String odiSessionNumber="123";
				String scenExecMessage="TEST MESSAGE";
				
				OdiScenExecutionStatus mckOdiExecStatus=Mock();
				
		when:	"The execute scenatio method is executed"
				OdiScenarioExecutionResult rsp=subject.executeScenario(scenName,scenVersion,execDescription,scenParams);
				
		then:	"The interactions happen as described below"
				
				1 * mckOdiService.executeScenario(mckOdiEnv,scenName,scenVersion,execDescription,scenParams,"") >> {
					return mckOdiExecStatus;
				}
				
				(1.._) * mckOdiExecStatus.getCode() >> scenExecCode;
				
				(1.._) * mckOdiExecStatus.getSessionNumber() >> odiSessionNumber;
				
				(1.._) * mckOdiExecStatus.getMessage() >> scenExecMessage;
				
				0 * _;//no other interaction
				
		then:	"The result are as exepcted, as set in the mock objects"
				rsp.code==scenExecCode;
				rsp.odiSessionNumber==odiSessionNumber;
				rsp.message==scenExecMessage;
				
	}

}
