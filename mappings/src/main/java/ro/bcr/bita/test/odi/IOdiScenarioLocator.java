package ro.bcr.bita.test.odi;

public interface IOdiScenarioLocator {
	
	public OdiScenarioUnderTest locateScenario(String... identifiers) throws OdiTestingException;

}
