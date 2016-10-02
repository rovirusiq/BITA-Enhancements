package ro.bcr.bita.odi.proxy

import ro.bcr.bita.model.BitaTestSpecification;

import oracle.odi.runtime.agent.invocation.ExecutionInfo;
import oracle.odi.runtime.agent.invocation.RemoteRuntimeAgentInvoker;
import oracle.odi.runtime.agent.invocation.StartupParams;



class OdiRemoteOperationsServiceTest extends BitaTestSpecification {
	
	private OdiRemoteOperationsService subject;
	
	def setup() {
		subject=new OdiRemoteOperationsService(this.stbOdiRemoteEntityFactory);
	}
	
	
	def "Execute Scenario - interactions with the Remote Agent"(){
		given:	"The setup"
		
				String agentUrl="dummUrl";
				String odiUsername="odiUsername";
				char[] odiPassword="odiPassword".toCharArray();
				String odiContext="GLOBAL";
				Integer odiLogLevel=5;
				String odiWorkRepName="DMYWRKREP";
				
				String scenName="SCENARION-NAME";
				String scenVersion="001";
				String opDescription="Description";
				HashMap<String, String> scenParams=[:];
				String keyWords="keyWords";
				
				Long sessionNumber=123;
				String statusCode="DONE";
				String statusMsg="Message";
				
				OdiScenExecutionEnv env=stbOdiRemoteEntityFactory.newOdiScenExecutionEnv(agentUrl,odiUsername,odiPassword,odiContext,odiLogLevel,odiWorkRepName);
				
		
				RemoteRuntimeAgentInvoker mckAgent=Mock();
				ExecutionInfo mckInfo=Mock();
				StartupParams mckParams=Mock();
				//ExecutionInfo.SessionStatus mckExecStatus=Mock();
				
				stbOdiRemoteEntityFactory.newOdiScenParams(scenParams)>>{
					return mckParams;
				}
				
				stbOdiRemoteEntityFactory.newOdiRuntimeInvoker(env)>>{
					return mckAgent;
				}
				
		when:	"The command to execute sceanrio is issued"
				
				OdiScenExecutionStatus rsp=subject.executeScenario(env,scenName,scenVersion,opDescription,scenParams,keyWords);
				
		then:	"The interactions are as expected"
				
				1*mckAgent.invokeStartScenario(scenName,scenVersion,mckParams,keyWords,env.odiContext,env.odiLogLevel,opDescription,true,env.odiWorkRepName)>>{
					return mckInfo;
				}
				
				1*mckInfo.getSessionId()>>{
					return sessionNumber;
				}
				
				1*mckInfo.getSessionStatus()>>{
					return statusCode;//it works only because of Grovy. It does not have the correct type
				}
				
				1*mckInfo.getStatusMessage()>>{
					return statusMsg;
				}
				
				0 * _;//not othe rinteraction
		then:	"The results are as expected"
				statusCode.equals(rsp.getCode());
				rsp.getSessionNumber().equals(""+sessionNumber);
				rsp.getMessage().equals(statusMsg);
	}
	
	def "Execute Scenario - Exception handling"(){
		given:	"The setup"
				String agentUrl="dummUrl";
				String odiUsername="odiUsername";
				char[] odiPassword="odiPassword".toCharArray();
				String odiContext="GLOBAL";
				Integer odiLogLevel=5;
				String odiWorkRepName="DMYWRKREP";
				
				String scenName="SCENARION-NAME";
				String scenVersion="001";
				String opDescription="Description";
				HashMap<String, String> scenParams=[:];
				String keyWords="keyWords";
				
				Long sessionNumber=123;
				String statusCode="DONE";
				String statusMsg="Message";
				
				OdiScenExecutionEnv env=stbOdiRemoteEntityFactory.newOdiScenExecutionEnv(agentUrl,odiUsername,odiPassword,odiContext,odiLogLevel,odiWorkRepName);
		
				ExecutionInfo mckInfo=Mock();
				StartupParams mckParams=Mock();
				
				
				
				stbOdiRemoteEntityFactory.newOdiScenParams(scenParams)>>{
					return mckParams;
				}
				
				stbOdiRemoteEntityFactory.newOdiRuntimeInvoker(env)>>{
					throw new RuntimeException("Exception for testing purpose");
				}
				
		when:	"The command to execute sceanrio is issued"
				
				OdiScenExecutionStatus rsp=subject.executeScenario(env,scenName,scenVersion,opDescription,scenParams,keyWords);
				
		then:	"Exception is thrown"
				
				thrown BitaOdiException;
		
	}
	
}
