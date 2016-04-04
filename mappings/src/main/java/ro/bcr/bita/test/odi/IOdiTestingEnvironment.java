package ro.bcr.bita.test.odi;

import ro.bcr.bita.utils.OdiTemplate;

import org.dbunit.IDatabaseTester;

public interface IOdiTestingEnvironment {
	
	public OdiTemplate getOdiTemplate();
	public IDatabaseTester getDatabaseTester();
	public IOdiScenarioLocator getOdiScenarioLocator();
	public IDataSetProvider getDataSetProvider();
	public char[] getOdiUserPassword();
	public String getOdiUsername();

}
