package ro.bcr.bita.mapping.analyze

import ro.bcr.bita.sql.IBitaSqlExecutor

public class JcLdsGroupStrategy implements IJcGroupIdentificationStrategy{
	
	private final JcSqlCommandsGenerator sqlGenerator;
	private final String jobGroupName;
	
	public JcLdsGroupStrategy(String jobGroupName,JcSqlCommandsGenerator sqlGen) {
		this.sqlGenerator=sqlGen;
		this.jobGroupName=jobGroupName;
	}

	@Override
	public void createGroups(JcRequestContext params,MappingDependencyAnalyzerProcessor analyzer, IBitaSqlExecutor bitaSqlExecutor) {
		
		String[] queries=sqlGenerator.generateSqlGroupDefinition(params,jobGroupName);
		bitaSqlExecutor.executeInCurrentTransaction(queries);
		
		analyzer.getAllMappingNames().each{String mappingName->
			String jobId=params.generateJobId(mappingName);
			List<String> sqlAddToGroup=sqlGenerator.generateSqlGroupJobs(params,jobGroupName,jobId);
			bitaSqlExecutor.executeInCurrentTransaction(sqlAddToGroup);
		}
		
	}

}
