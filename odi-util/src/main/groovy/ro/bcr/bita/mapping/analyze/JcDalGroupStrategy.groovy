package ro.bcr.bita.mapping.analyze

import ro.bcr.bita.mapping.analyze.IJcGroupIdentificationStrategy
import ro.bcr.bita.model.IMappingRepository
import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.model.MappingDependency
import ro.bcr.bita.sql.IBitaSqlExecutor

public class JcDalGroupStrategy implements IJcGroupIdentificationStrategy{
	
	private final JcSqlCommandsGenerator sqlGenerator;
	
	public JcDalGroupStrategy(JcSqlCommandsGenerator sqlGen) {
		this.sqlGenerator=sqlGen;
	}

	@Override
	public void createGroups(JcRequestContext params,MappingDependencyAnalyzerProcessor analyzer, IBitaSqlExecutor bitaSqlExecutor) {
		
		HashMap<String,IOdiMapping> mapMappings=[:];
		
		analyzer.getAllMappings().each{IOdiMapping mp->
		  mapMappings.put(mp.getName(),mp);
		}
		
		
		analyzer.getDependencies().each{MappingDependency md->
			
			 String sourceMapName=md.on();
			 String lzMapName=md.who();
			 
			 IOdiMapping sourceMap=mapMappings.get(sourceMapName);
			 IOdiMapping lzMap=mapMappings.get(lzMapName);
			 
			 String srcJobId=params.generateJobId(sourceMapName);
			 String lzJobId=params.generateJobId(lzMapName);
			 
			 String jobGroupName=sourceMap.getLeadingSource();
			 String jobGroupNameReco="RECO_"+lzMap.identifyTarget();
			 
			 /*
			  * Normal Dal Group
			  */			 
			 
			 List<String> sqlCmds=sqlGenerator.generateSqlGroupDefinition(params,jobGroupName);
			 bitaSqlExecutor.executeInCurrentTransaction(sqlCmds);
			 
			 sqlCmds=sqlGenerator.generateSqlGroupJobs(params,jobGroupName,srcJobId);
			 bitaSqlExecutor.executeInCurrentTransaction(sqlCmds);
			 
			 sqlCmds=sqlGenerator.generateSqlGroupJobs(params,jobGroupName,lzJobId);
			 bitaSqlExecutor.executeInCurrentTransaction(sqlCmds);
			 
			 /*
			  * Reco Dal Group
			  */
			 
			 sqlCmds=sqlGenerator.generateSqlGroupDefinition(params,jobGroupNameReco);
			 bitaSqlExecutor.executeInCurrentTransaction(sqlCmds);
			 
			 sqlCmds=sqlGenerator.generateSqlGroupJobs(params,jobGroupNameReco,lzJobId);
			 bitaSqlExecutor.executeInCurrentTransaction(sqlCmds);
			 
		}
		
	}
	
}
