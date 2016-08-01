package ro.bcr.bita.mapping.dependency.jc

import ro.bcr.bita.mapping.analyze.IMappingAnalyzeProcessor;
import ro.bcr.bita.mapping.dependency.MappingAnalyzeDependencyProcessor;
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
	
	
	private final JcSqlGeneratorHelper sqlGenerator=new JcSqlGeneratorHelper();
		
	@Delegate
	private MappingAnalyzeDependencyProcessor prc;
	
	public JcSqlGenerator (MappingAnalyzeDependencyProcessor processor) {
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
			//add job to the job definitions
			sqlJobDependencies << [mapping.getName(),sqlGenerator.generateSqlDepCleanup(params,mapping.getName())]
			sqlJobDefinition << [mapping.getName(),sqlGenerator.generateSqlJobDefinition(params,mapping.getName())]
			sqlJobDefinition << [mapping.getName(),sqlGenerator.generateSqlJobParameters(params,mapping.getName(),mapping.getLeadingSource())]
			sqlGroupDefinition << [mapping.getName(),sqlGenerator.generateSqlGroupJobs(params,mapping.getName())]
		}
		
		Set<MappingDependency> dpds=this.prc.getDependencies();
		
	 	for (MappingDependency mp:dpds) {
			sqlJobDependencies << [mp.who(),this.sqlGenerator.generateSqlDependency(params,mp.who(),mp.on())]
		}
	}
}
