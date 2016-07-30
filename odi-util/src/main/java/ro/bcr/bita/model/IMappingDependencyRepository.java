package ro.bcr.bita.model;

import java.util.Set;

//TODO sa fac mai generica interfata aceasta. Sa reyruneze IDpenedency cu diferite obiecte ca tip
public interface IMappingDependencyRepository {
	
	public void addTableToMapping(String tableName,String mappingName) throws BitaModelException;
	public void addMappingToTable(String mappingName,String tableName) throws BitaModelException;
	public Set<IDependency<String,String>> getMappingDependencies() throws BitaModelException;
	
}
