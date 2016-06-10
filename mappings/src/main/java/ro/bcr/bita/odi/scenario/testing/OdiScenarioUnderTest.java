package ro.bcr.bita.odi.scenario.testing;

public class OdiScenarioUnderTest {
	
	private String name;
	private String version;
	
	public OdiScenarioUnderTest(String name,String version) {
		this.name=name;
		this.version=version;
	}

	public OdiScenarioUnderTest() {
		
	}
	
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	
	

}
