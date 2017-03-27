package ro.bcr.bita.odi.proxy

import java.util.HashMap;

import oracle.odi.runtime.agent.invocation.RemoteRuntimeAgentInvoker;
import oracle.odi.runtime.agent.invocation.StartupParams;

class OdiRemoteEntityFactory implements IOdiRemoteEntityFactory{
	
	private OdiRemoteEntityFactory() {
		
	}
	

	/********************************************************************************************
	 *IOdiRemoteEntityFactory
	 ********************************************************************************************/
	@Override
	public StartupParams newOdiScenParams(HashMap<String,String> params) {
		return new StartupParams(params);
	}
	
	@Override
	public RemoteRuntimeAgentInvoker newOdiRuntimeInvoker(OdiScenExecutionEnv env) {
		return new RemoteRuntimeAgentInvoker(env.agentUrl, env.odiUsername, env.odiPassword);
	}
	
	//@Override
	public OdiScenExecutionStatus newOdiScenExecutionStatus(String sessionNumber="",String code="",String message="") {
		return new OdiScenExecutionStatus(sessionNumber, code, message);
	}
	
	@Override
	public OdiScenExecutionEnv newOdiScenExecutionEnv(String agentUrl,String odiUserName,char[] odiPassword,String odiContext,Integer odiAgentLogLevel,String workRepName) {
		return new OdiScenExecutionEnv(agentUrl,odiUserName,odiPassword,odiContext,odiAgentLogLevel,workRepName);
	}
	/********************************************************************************************
	 *Static methods
	 ********************************************************************************************/
	public static IOdiRemoteEntityFactory newInstance() {
		return new OdiRemoteEntityFactory();
	}
}
