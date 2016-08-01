package ro.bcr.bita.model;

import java.util.Set;

public interface IMappingDependencyRepository<H,O> {
	
	public void addTableToMapping(String tableName,String mappingName) throws BitaModelException;
	public void addMappingToTable(String mappingName,String tableName) throws BitaModelException;
	public Set<IDependency<H,O>> getMappingDependencies() throws BitaModelException;
	
}
