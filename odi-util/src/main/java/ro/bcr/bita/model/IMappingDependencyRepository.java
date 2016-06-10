package ro.bcr.bita.model;

import java.util.Set;

public interface IMappingDependencyRepository {
	
	public void addTableToMapping(String tableName,String mappingName) throws BitaModelException;
	public void addMappingToTable(String mappingName,String tableName) throws BitaModelException;
	public Set<IDependency<String,String>> getMappingDependencies() throws BitaModelException;
	
	
}
