package ro.bcr.bita.mapping.analyze

import ro.bcr.bita.sql.IBitaSqlExecutor

import groovy.transform.CompileStatic

@CompileStatic
public class JcLdsGroupStrategy implements IJcGroupIdentificationStrategy{
	
	protected final JcSqlCommandsGenerator sqlGenerator;
	protected final String jobGroupName;
	
	public JcLdsGroupStrategy(String jobGroupName,JcSqlCommandsGenerator sqlGen) {
		this.sqlGenerator=sqlGen;
		this.jobGroupName=jobGroupName;
	}
	
	protected String generateJobGroupName(JcRequestContext params,String jobGroupName) {
		return jobGroupName;
	}

	@Override
	public void createGroups(JcRequestContext params,MappingDependencyAnalyzerProcessor analyzer, IBitaSqlExecutor bitaSqlExecutor) {
		
		String realJobGroupName=generateJobGroupName(params,jobGroupName);
		
		String[] queries=sqlGenerator.generateSqlGroupDefinition(params,realJobGroupName);
		bitaSqlExecutor.executeInCurrentTransaction(queries);
		
		analyzer.getAllMappingNames().each{String mappingName->
			String jobId=params.generateJobId(mappingName);
			List<String> sqlAddToGroup=sqlGenerator.generateSqlGroupJobs(params,realJobGroupName,jobId);
			bitaSqlExecutor.executeInCurrentTransaction(sqlAddToGroup);
		}
		
	}

}
