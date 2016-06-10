package ro.bcr.bita.odi.scenario.testing;

import oracle.odi.core.OdiInstance
import oracle.odi.runtime.agent.RuntimeAgent
import oracle.odi.runtime.agent.invocation.ExecutionInfo
import oracle.odi.runtime.agent.invocation.StartupParams

import org.dbunit.IDatabaseTester
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ro.bcr.bita.odi.scenario.testing.IDataSetProvider;
import ro.bcr.bita.odi.scenario.testing.IOdiTestingEnvironment;
import ro.bcr.bita.odi.scenario.testing.OdiScenarioUnderTest;
import ro.bcr.bita.odi.scenario.testing.OdiTestingException;
import ro.bcr.bita.utils.IOdiCommand
import ro.bcr.bita.utils.OdiTemplate;

public abstract class Database2LandingZoneOldTemplate {
	
	
	
	private OdiScenarioUnderTest testSubject;
	private OdiTemplate odiTemplate;
	private IDatabaseTester dbTester;
	private IDataSetProvider dbSetProvider;
	
	
	protected abstract IOdiTestingEnvironment getEnvironment();
	protected abstract String[] getMappingIdentifiers();
	protected int returnExpectedExecutionCode() {
		return 0;
	}
	
	
	/**
	 * Set up prerequisits
	 * Objects used in the test
	 * Populate the Database 
	 */
	public void prepareFortest() throws OdiTestingException {
		
		//TODO sa vad daca nu pot sa folosesc fail aici
		if (getEnvironment()==null) new OdiTestingException("The OdiTestingEnvironment was not provided for this test");
		
		//objects used in the test
		odiTemplate=getEnvironment().getOdiTemplate();
		dbTester=getEnvironment().getDatabaseTester();
		dbSetProvider=getEnvironment().getDataSetProvider();
		//retrieve (if necessary create) scenario for the object to test
		testSubject=getEnvironment().getOdiScenarioLocator().locateScenario(getMappingIdentifiers());
		//set-up the database
		dbTester.setDataSet(dbSetProvider.getDataSet("startUp",getMappingIdentifTiers()));
		dbTester.onSetup();
		
	}
	
	/**
	 * The logic of the test.
	 * Executes the mapping, checks the creturn code
	 * 
	 */
	//TODO Sa adaug in TestingEnvironment parametrii
	//TODO Sa adaug in TestingEnvironment Conetxtul
	//TODO sa verific ceilalti parametrii care trebuie adaugati pt ca acest test sa functioneze
	//TODO cum pt sa fac sa extind conditia de expected result code
	@Test
	public void executeMap() throws OdiTestingException{
		
		final String odiUsername=getEnvironment().getOdiUsername();
		final char[] odiUserPassword=getEnvironment().getOdiUserPassword();
		final OdiScenarioUnderTest scenarioUnderTest=testSubject;
		final expectedCode=returnExpectedExecutionCode();
		
		odiTemplate.executeWithoutTransaction(
			new IOdiCommand() {
				public void execute(OdiInstance odiInstance) {
					RuntimeAgent runtimeAgent=new RuntimeAgent(odiInstance,odiUsername,odiUserPassword);
					println "TS: "+System.currentTimeMillis();
					ExecutionInfo execInfo=runtimeAgent.startScenario(scenarioUnderTest.getName(),scenarioUnderTest.getVersion(),new StartupParams(),"","GLOBAL",5,"",true);
					println "ES: "+System.currentTimeMillis();
					println "Execution Code:"+execInfo.getReturnCode();
					assert expectedCode==execInfo.getReturnCode();
				}
			}
		);
		
	}
	
	public void cleanUpAfterTest() throws OdiTestingException {
		//cleanup after prerequsits
		dbTester.setTearDownOperation(DatabaseOperation.TRUNCATE_TABLE);
		dbTester.onTearDown();
	}
	
	
}
