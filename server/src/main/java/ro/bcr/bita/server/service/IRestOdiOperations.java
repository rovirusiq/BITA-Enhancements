package ro.bcr.bita.server.service;

import java.util.HashMap;

public interface IRestOdiOperations {
	
	public abstract OdiScenarioExecutionResult executeScenario(String scenName,String scenVersion,String opDescription, HashMap<String,String> scenParams);

}
