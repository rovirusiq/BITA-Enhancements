package ro.bcr.bita.test.odi.configuration;

import ro.bcr.bita.test.odi.IDataSetProvider;
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
