package ro.bcr.bita.odi.proxy

import java.util.HashMap;

import oracle.odi.runtime.agent.invocation.ExecutionInfo;
import oracle.odi.runtime.agent.invocation.RemoteRuntimeAgentInvoker;

class OdiRemoteOperationsService implements IOdiRemoteOperations{
	
	private final IOdiRemoteEntityFactory odiEntityFactory;
	
	/**
	 * @param odiEntityFactory
	 */
	public OdiRemoteOperationsService(IOdiRemoteEntityFactory odiEntityFactory) {
		super();
		this.odiEntityFactory = odiEntityFactory;
	}

	@Override
	public OdiScenExecutionStatus executeScenario(OdiScenExecutionEnv env,
			String scenName, String scenVersion, String opDescription,
			HashMap<String, String> scenParams,
			String keyWords) throws BitaOdiException {
		
		try {
			RemoteRuntimeAgentInvoker odiAgent=odiEntityFactory.newOdiRuntimeInvoker(env);
			ExecutionInfo info=odiAgent.invokeStartScenario(scenName,scenVersion,odiEntityFactory.newOdiScenParams(scenParams),keyWords,env.odiContext,env.odiLogLevel,opDescription,true,env.odiWorkRepName);
			
			OdiScenExecutionStatus rsp=odiEntityFactory.newOdiScenExecutionStatus(""+info.getSessionId(),info.getSessionStatus().toString(),info.getStatusMessage());
			
			return rsp;
		} catch (Exception ex) {
			throw new BitaOdiException("An exception was raised when executing Odi scenario [$scenName,$scenVersion].",ex);
		}
	}

}
