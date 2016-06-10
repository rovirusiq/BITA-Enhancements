package ro.bcr.bita.odi.scenario.testing;

public interface IOdiScenarioLocator {
	
	public OdiScenarioUnderTest locateScenario(String... identifiers) throws OdiTestingException;

}
