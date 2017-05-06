package ro.bcr.bita.mapping.analyze

import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.sql.IBitaSqlExecutor

class JcInitialLoadStrategy implements IJcGroupIdentificationStrategy {
	
	private static final String JOB_GROUP_IL_PREFIX="IL_";
	private static final String JOB_GROUP_ALL=JOB_GROUP_IL_PREFIX+"BIG_GROUP";
	
	
	private final JcSqlCommandsGenerator sqlGenerator;
	
	public JcInitialLoadStrategy(JcSqlCommandsGenerator sqlGen) {
		this.sqlGenerator=sqlGen;
	}
	

	@Override
	public void createGroups(JcRequestContext params,MappingDependencyAnalyzerProcessor analyzer,IBitaSqlExecutor bitaSqlExecutor) {
		HashMap<String,IOdiMapping> mapMappings=[:];
		
		analyzer.getAllMappings().each{IOdiMapping mp->
		  mapMappings.put(mp.getName(),mp);
		}
		
		List<String> sqlCmds=sqlGenerator.generateSqlGroupDefinition(params,JOB_GROUP_ALL);
		bitaSqlExecutor.executeInCurrentTransaction(sqlCmds);
		
		mapMappings.each{String name,IOdiMapping mapping->
			if (name.startsWith("L2B_")) {
				String jobId=params.generateJobId(name);
				//add to the ALL GROUP
				sqlCmds=sqlGenerator.generateSqlGroupJobs(params,JOB_GROUP_ALL,jobId);
				bitaSqlExecutor.executeInCurrentTransaction(sqlCmds);
				//create the group for that one mapping
				String jobGroupName=JOB_GROUP_IL_PREFIX+name;
				sqlCmds=sqlGenerator.generateSqlGroupDefinition(params,jobGroupName);
				bitaSqlExecutor.executeInCurrentTransaction(sqlCmds);
				
				sqlCmds=sqlGenerator.generateSqlGroupJobs(params,jobGroupName,jobId);
				bitaSqlExecutor.executeInCurrentTransaction(sqlCmds);
				
				
			}
		}
		
		

	}

}
