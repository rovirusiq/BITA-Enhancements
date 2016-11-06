package ro.bcr.bita.mapping.dependency.jc

import ro.bcr.bita.mapping.analyze.IMappingAnalyzeProcessor;
import ro.bcr.bita.mapping.dependency.MappingDependencyAnalyzerProcessor;
import ro.bcr.bita.model.IMessageCollection;
import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.model.MappingDependency;
import ro.bcr.bita.model.MessageCollection;
import ro.bcr.bita.model.OdiMapping

import groovy.text.Template;
import groovy.transform.CompileStatic;
import groovy.transform.TypeChecked;
import groovy.transform.TypeCheckingMode;

@CompileStatic
class JcSqlGenerator implements IMappingAnalyzeProcessor{
	
	
	private final JcSqlCommandsHelper sqlGenerator=new JcSqlCommandsHelper();
		
	@Delegate
	private MappingDependencyAnalyzerProcessor prc;
	
	public JcSqlGenerator (MappingDependencyAnalyzerProcessor processor) {
		this.prc=processor;
	}
	
	/********************************************************************************************
	 *
	 *Generate ALL SQLs
	 *
	 ********************************************************************************************/
	/**
	 * @param msgColl The message collection that will hold the queries
	 */
	@TypeChecked(TypeCheckingMode.SKIP)
	public void generateSql(JcParameters params,IMessageCollection sqlGroupDefinition,IMessageCollection sqlJobDefinition,IMessageCollection sqlJobDependencies) {
		Set<IOdiMapping> mps=this.prc.getAllMappings();
				
		//if no mappings we stop
		if (mps.size()<=0) return;
		
		sqlGroupDefinition.add(params.jobGroupName,sqlGenerator.generateSqlGroupDefinition(params));
		
		
		for (IOdiMapping mapping:mps) {
			
			String jobId=params.generateJobId(mapping.getName());
			
			//delete the dependencies
			sqlJobDependencies << [mapping.getName(),sqlGenerator.generateSqlDependencyInit(params,jobId)].flatten();
			//create the job definistion
			sqlJobDefinition << [mapping.getName(),sqlGenerator.generateSqlJobDefinition(params,jobId,mapping.getName(),mapping.getLeadingSource(),mapping.getParameterListOfScenario(params.getScenarioVersion()),false)].flatten();
			//add to the group Definition
			sqlGroupDefinition << [mapping.getName(),sqlGenerator.generateSqlGroupJobs(params,jobId)].flatten();
		}
		
		Set<MappingDependency> dpds=this.prc.getDependencies();
		
	 	for (MappingDependency mp:dpds) {
			 
			String whoJobId=params.generateJobId(mp.who());
			String onJobId=params.generateJobId(mp.on());
			 
			sqlJobDependencies << [mp.who(),this.sqlGenerator.generateSqlDependency(params,whoJobId,onJobId)].flatten()
		}
	}
}
