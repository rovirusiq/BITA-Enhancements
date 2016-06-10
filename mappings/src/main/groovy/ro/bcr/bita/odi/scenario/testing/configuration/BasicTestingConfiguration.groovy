package ro.bcr.bita.odi.scenario.testing.configuration;

import ro.bcr.bita.odi.scenario.testing.IDataSetProvider;
import ro.bcr.bita.utils.OdiTemplate;

import org.dbunit.IDatabaseTester;

import oracle.odi.runtime.agent.RuntimeAgent;

public class BasicTestingConfiguration {
	
	protected BasicTestingConfigurationJ() {
		
	}
	
	public OdiTemplate getOdiTemplate() {
		return null;
	}
	
	public RuntimeAgent getOdiRuntimeAgent() {
		return null;
	}
	
	public IDatabaseTester getDatabaseTester() {
		return null;
	}
	
	public IDataSetProvider getDataSetProvider() {
		return null;
	}

}
