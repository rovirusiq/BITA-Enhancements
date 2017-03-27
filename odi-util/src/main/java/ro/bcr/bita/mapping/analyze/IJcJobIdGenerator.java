package ro.bcr.bita.mapping.analyze;


public interface IJcJobIdGenerator {
	
	public abstract String generateJobId(JcRequestContext params,String mappingName);

}
