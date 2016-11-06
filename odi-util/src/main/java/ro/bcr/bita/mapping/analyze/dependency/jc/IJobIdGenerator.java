package ro.bcr.bita.mapping.analyze.dependency.jc;

public interface IJobIdGenerator {
	
	public abstract String generateJobId(String jobGroupId,String mappingName,String scenarioVersionNumber);

}
