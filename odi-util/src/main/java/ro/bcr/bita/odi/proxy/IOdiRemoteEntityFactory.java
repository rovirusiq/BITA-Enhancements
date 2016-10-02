package ro.bcr.bita.odi.proxy;

import java.util.HashMap;

import oracle.odi.runtime.agent.invocation.RemoteRuntimeAgentInvoker;
import oracle.odi.runtime.agent.invocation.StartupParams;

public interface IOdiRemoteEntityFactory {
	
	/************************************************************************************************************
	 *ODI Standard Objects
	 ************************************************************************************************************/
	public abstract StartupParams newOdiScenParams(HashMap<String,String> params);

	public abstract RemoteRuntimeAgentInvoker newOdiRuntimeInvoker(OdiScenExecutionEnv env);
	/************************************************************************************************************
	 *Bita helper objects
	 ************************************************************************************************************/
	
	public abstract OdiScenExecutionStatus newOdiScenExecutionStatus(String sessionNumber,String code,String message);
	
	public abstract OdiScenExecutionEnv newOdiScenExecutionEnv(String agentUrl,String odiUserName,char[] odiPassword,String odiContext,Integer odiAgentLogLevel,String workRepName);
	
}
