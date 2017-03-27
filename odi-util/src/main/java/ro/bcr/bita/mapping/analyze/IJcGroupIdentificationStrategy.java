package ro.bcr.bita.mapping.analyze;

import ro.bcr.bita.sql.IBitaSqlExecutor;
import ro.bcr.bita.mapping.analyze.JcRequestContext;

public interface IJcGroupIdentificationStrategy {
	
	public void createGroups(JcRequestContext params,MappingDependencyAnalyzerProcessor analyzer,IBitaSqlExecutor bitaSqlExecutor);

}
