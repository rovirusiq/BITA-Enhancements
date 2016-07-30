package ro.bcr.bita.service.mapping

import ro.bcr.bita.model.IMessageCollection;
import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.model.MappingDependency;
import ro.bcr.bita.model.MessageCollection;
import ro.bcr.bita.model.OdiMapping
import ro.bcr.bita.service.IMappingAnalyzeProcessor;

import groovy.text.Template;
import groovy.transform.CompileStatic;
import groovy.transform.TypeChecked;
import groovy.transform.TypeCheckingMode;

@CompileStatic
class JcSqlGenerator implements IMappingAnalyzeProcessor{
	
	static final String SQL_DEPENDENCY_KEY="JC_DEPENDECY";
	static final String SQL_GROUP_KEY="JC_GROUP";
	static final String  SQL_PARAMETERS_KEY="JC_PARAMETERS";
	private static final String TEMPLATES_PATH="templates/sql/jc/"
	private static final String SQL_DEPENDENCY_CLEANUP='dependency_initial';
	private static final String SQL_DEPENDENCY_ADD='dependency_add';
	private static final String SQL_JOB_DEFINITION="job_definition";
	private static final String SQL_PARMETERS='job_parameters';
	private static final String SQL_GROUP_DEFINITION='group_definition';
	private static final String SQL_GROUP_DEFINITION_INIT='group_definition_init';
	private static final String SQL_GROUP_JOBS='group_jobs';
	private static final String SQL_GROUP_JOBS_INIT='group_jobs_init';
	
	
	private Template tmplSqlDependecyInit=null;
	private Template tmplSqlDependency=null;
	private Template tmplSqlParameters=null;
	private Template tmplSqlGroupDefinition=null;
	private Template tmplSqlJobDefinition=null;
	private Template tmplSqlGroupDefinitionInit=null;
	private Template tmplSqlGroupJobInit=null;
	private Template tmplSqlGroup=null;
	
	
	@Delegate
	private JcAnalyzeProcessor prc;
	
	public JcSqlGenerator (JcAnalyzeProcessor processor) {
		this.prc=processor;
	}
	
	/********************************************************************************************
	 *
	 *Parameter Class
	 *
	 ********************************************************************************************/
	public static class Parameters {
		
		private Parameters() {
			
		}
		
		public String dwhRelease="JC_SQL_RELEASE";
		public String dwhVersion="JC_SQL_VERSION";
		public String jobGroupName="JC_BMF";
		public IMessageCollection sqlJobDependencyStatements=new MessageCollection("jcSqls");
		public IMessageCollection sqlJobGroupStatements=sqlJobDependencyStatements;
		public IMessageCollection sqlJobParameterStatements=sqlJobDependencyStatements;
		
		
		
		public static Parameters newSqlParameters() {
			return new Parameters();
		}
		
	}
	
	/********************************************************************************************
	 *
	 *Templates
	 *
	 ********************************************************************************************/
	private Template getTemplate(String templateName) {
		InputStream is=this.getClass().getClassLoader().getResourceAsStream(TEMPLATES_PATH+templateName);
		Template template=new groovy.text.SimpleTemplateEngine().createTemplate(new java.io.InputStreamReader(is));
		return template;
	}
	
	private Template getTemplateForSqlDependencyInit() {
		if (this.tmplSqlDependecyInit==null) {
			this.tmplSqlDependecyInit=this.getTemplate(SQL_DEPENDENCY_CLEANUP);
		}
		return this.tmplSqlDependecyInit;
	}
	
	private Template getTemplateForSqlDependency() {
		if (this.tmplSqlDependency==null) {
			this.tmplSqlDependency=this.getTemplate(SQL_DEPENDENCY_ADD);
		}
		return this.tmplSqlDependency;
	}
	
	private Template getTemplateForSqlParameters() {
		if (this.tmplSqlParameters==null) {
			this.tmplSqlParameters=this.getTemplate(SQL_PARMETERS);
		}
		return this.tmplSqlParameters;
	}
	
	
	private Template getTemplateForSqlGroupDefinitionInit() {
		if (this.tmplSqlGroupDefinitionInit==null) {
			this.tmplSqlGroupDefinitionInit=this.getTemplate(SQL_GROUP_DEFINITION_INIT);
		}
		return this.tmplSqlGroupDefinitionInit;
	}
	
	private Template getTemplateForSqlGroupJobInit() {
		if (this.tmplSqlGroupJobInit==null) {
			this.tmplSqlGroupJobInit=this.getTemplate(SQL_GROUP_JOBS_INIT);
		}
		return this.tmplSqlGroupJobInit;
	}
	
	private Template getTemplateForSqlGroupDefinition() {
		if (this.tmplSqlGroupDefinition==null) {
			this.tmplSqlGroupDefinition=this.getTemplate(SQL_GROUP_DEFINITION);
		}
		return this.tmplSqlGroupDefinition;
	}
	
	private Template getTemplateForSqlJobDefinition() {
		if (this.tmplSqlJobDefinition==null) {
			this.tmplSqlJobDefinition=this.getTemplate(SQL_JOB_DEFINITION);
		}
		return this.tmplSqlJobDefinition;
	}
	
	private Template getTemplateForSqlGroup() {
		if (this.tmplSqlGroup==null) {
			this.tmplSqlGroup=this.getTemplate(SQL_GROUP_JOBS);
		}
		return this.tmplSqlGroup;
	}
	
	/********************************************************************************************
	 *
	 *Query generation
	 *
	 ********************************************************************************************/
	@TypeChecked(TypeCheckingMode.SKIP)
	private void addSqlDepCleanup(Parameters params,String mappingName) {
		params.sqlJobDependencyStatements << [mappingName
			,this.getTemplateForSqlDependencyInit().make([mappingName:mappingName]).toString()
			];
	}
	@TypeChecked(TypeCheckingMode.SKIP)
	private void addSqlDependency(Parameters params,String whoMappingName,String onMappingName) {
		params.sqlJobDependencyStatements << [whoMappingName
			,this.getTemplateForSqlDependency().make([
				whoMappingName:whoMappingName
				,onMappingName:onMappingName
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				]).toString()
			];
	}
	@TypeChecked(TypeCheckingMode.SKIP)
	private void addSqlGroupDefinition(Parameters params) {
		
		
		params.sqlJobGroupStatements << [params.jobGroupName
			,this.getTemplateForSqlGroupJobInit().make([
				jobGroupName:params.jobGroupName
				]).toString()
			];
		
		params.sqlJobGroupStatements << [params.jobGroupName
			,this.getTemplateForSqlGroupDefinitionInit().make([
				jobGroupName:params.jobGroupName
				]).toString()
			];
		
		params.sqlJobGroupStatements << [params.jobGroupName
			,this.getTemplateForSqlGroupDefinition().make([
				jobGroupName:params.jobGroupName
				]).toString()
			];
	}
	
	@TypeChecked(TypeCheckingMode.SKIP)
	private void addSqlGroupJobs(Parameters params,String mappingName) {
		params.sqlJobGroupStatements << [params.jobGroupName
			,this.getTemplateForSqlGroup().make([
				jobGroupName:params.jobGroupName
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				,mappingName:mappingName
				]).toString()
			];
	}
	
	@TypeChecked(TypeCheckingMode.SKIP)
	private void addSqlJobParameters(Parameters params,String mappingName,String leadingSource) {
		params.sqlJobParameterStatements << [mappingName
			,this.getTemplateForSqlParameters().make([
				jobGroupName:params.jobGroupName
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				,mappingName:mappingName
				,leadingSource:leadingSource
				]).toString()
			
			];
	}
	
	@TypeChecked(TypeCheckingMode.SKIP)
	private void addSqlJobDefinition(Parameters params,String mappingName) {
		params.sqlJobGroupStatements << [mappingName
			,this.getTemplateForSqlJobDefinition().make([
				jobGroupName:params.jobGroupName
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				,mappingName:mappingName
				]).toString()
			
			];
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
	public void generateSql(Parameters params) {
		Set<IOdiMapping> mps=this.prc.getAllMappings();
				
		//if no mappings we stop
		if (mps.size()<=0) return;
		
		//create job group
		this.addSqlGroupDefinition(params);
		
		for (IOdiMapping x:mps) {
			//add job to the job definitions
			this.addSqlJobDefinition(params,x.getName());
			//add mapping to job group
			this.addSqlGroupJobs(params,x.getName());
			//add parameters for each mapping
			this.addSqlJobParameters(params,x.getName(),x.getLeadingSource());
			//delete dependencies for each mapping
			this.addSqlDepCleanup(params,x.getName());
		}
		
		Set<MappingDependency> dpds=this.prc.getDependencies();
		
	 	for (MappingDependency mp:dpds) {
			this.addSqlDependency(params,mp.who(),mp.on());
		}
		
	}

}
