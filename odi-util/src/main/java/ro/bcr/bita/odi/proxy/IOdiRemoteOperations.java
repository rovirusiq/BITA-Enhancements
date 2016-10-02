package ro.bcr.bita.odi.proxy;

import java.util.HashMap;

public interface IOdiRemoteOperations {
	
	public OdiScenExecutionStatus executeScenario(
			OdiScenExecutionEnv env,
			String scenName,
			String scenVersion,
			String opDescription, 
			HashMap<String,String> scenParams,
			String keyWords) throws BitaOdiException;

}
