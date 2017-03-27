package ro.bcr.bita.app;
import ro.bcr.bita.mapping.analyze.IJcGroupIdentificationStrategy;
import ro.bcr.bita.mapping.analyze.IJcJobIdGenerator;

public interface IJcJobGroupsCreator {
	
	//TODO sa vad daca imi trebuie aceasta conexiune
	public void setJdbcProvider(IBitaJdbcConnectionProvider bitaJdbcConnectionProvider);
	public void setDwhVersionCode(String dwhVersionCd);
	public void setJcMetadatdaSchemaName(String schemaName);
	public void setScenarioVersion(String scenarioVersion);
	//public void setDatabaseUpdatePolicy(DatabaseUpdatePolicyEnum policyIdentifier);
	public void setJobIdPolicy(IJcJobIdGenerator namePolicy);
	public void setJcGroupIdentificationStrategy(IJcGroupIdentificationStrategy strategy);
	public void createJcGroups(String... odiPaths);

}
