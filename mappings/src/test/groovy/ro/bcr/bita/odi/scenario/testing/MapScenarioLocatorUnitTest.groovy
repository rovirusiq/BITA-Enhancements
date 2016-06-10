package ro.bcr.bita.odi.scenario.testing

import groovy.mock.interceptor.MockFor
import oracle.odi.core.OdiInstance
import oracle.odi.core.persistence.IOdiEntityManager;

import org.junit.Before;
import org.junit.Test;

import static groovy.test.GroovyAssert.shouldFail

import ro.bcr.bita.odi.scenario.testing.MapScenarioLocator;
import ro.bcr.bita.odi.scenario.testing.OdiScenarioUnderTest;
import ro.bcr.bita.odi.scenario.testing.OdiTestingException;
import ro.bcr.bita.utils.IOdiCommand
import ro.bcr.bita.utils.OdiTemplate
import ro.bcr.bita.utils.OdiTemplateException

class MapScenarioLocatorUnitTest{
	
	private static final String SCENARIO_VERSION_FOR_TESTING="099";
	
	private static final String ODI_USERNAME = "SUPERVISOR"
	private static final String ODI_PASSWORD = "par0la123";
	
	private MapScenarioLocator testSubject;
	
	
	@Before
	public void beforeTest() {
		OdiTemplate.Builder builder=new OdiTemplate.Builder();
		builder.setJdbcDriverClassName("oracle.jdbc.OracleDriver");
		builder.setJdbcUrl("jdbc:oracle:thin:@vmdb:1521:XE");
		builder.setMasterRepositoryUsername("ODI_REPO");
		builder.setMasterRepositoryPassword(ODI_PASSWORD);
		builder.setWorkRepositoryName("WORKREP");
		builder.setOdiUsername(ODI_USERNAME);
		builder.setOdiPassword(ODI_PASSWORD);
		testSubject=new MapScenarioLocator(builder.build());
	}
	
	
	@Test
	public void when_MapDoesNotExist_ExceptionIsThrown() {
		shouldFail(OdiTestingException){
			testSubject.locateScenario("NO_MAP","NO_PROJECT");
		}
		
	}
	
	@Test
	public void when_MapDoesExistAndNoExceptionIsThrown_ValidObjectIsReturned() {
		OdiScenarioUnderTest scen=testSubject.locateScenario("TESTING_FRAMEWORK","Subject1");
		assert null!=scen;
		assert "Subject1"==scen.getName();
		assert SCENARIO_VERSION_FOR_TESTING==scen.getVersion();
	}

}
