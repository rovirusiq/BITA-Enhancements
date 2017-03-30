package ro.bcr.bita.mapping.analyze

import ro.bcr.bita.mapping.analyze.IJcGroupIdentificationStrategy
import ro.bcr.bita.model.IMappingRepository
import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.model.MappingDependency
import ro.bcr.bita.sql.IBitaSqlExecutor

import groovy.transform.CompileStatic

@CompileStatic
public class JcDalGroupStrategy implements IJcGroupIdentificationStrategy{
	
	protected final JcSqlCommandsGenerator sqlGenerator;
	
	public JcDalGroupStrategy(JcSqlCommandsGenerator sqlGen) {
		this.sqlGenerator=sqlGen;
	}
	
	protected String generateBaseStandardGroupName(JcRequestContext params,String leadingSourceName) {
		return leadingSourceName;
	}
	
	protected String generateBaseRecoGroupName(JcRequestContext param,String targetName) {
		return "RECO_"+targetName;
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
			 
			 IOdiMapping sourceMap=(IOdiMapping)mapMappings.get(sourceMapName);
			 IOdiMapping lzMap=(IOdiMapping)mapMappings.get(lzMapName);
			 
			 String srcJobId=params.generateJobId(sourceMapName);
			 String lzJobId=params.generateJobId(lzMapName);
			 
			 String jobGroupName=generateBaseStandardGroupName(params,sourceMap.getLeadingSource());
			 String jobGroupNameReco=generateBaseRecoGroupName(params,lzMap.identifyTarget());
			 
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
