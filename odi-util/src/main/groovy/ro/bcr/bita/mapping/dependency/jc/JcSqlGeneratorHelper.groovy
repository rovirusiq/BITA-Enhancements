package ro.bcr.bita.mapping.dependency.jc

import groovy.text.Template
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode

@CompileStatic
class JcSqlGeneratorHelper {
	
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
	/********************************************************************************************
	 *
	 *Constructors
	 *
	 ********************************************************************************************/
	protected JcSqlGeneratorHelper() {
		super();
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
	
	private String generateJobId(JcParameters params,String mappingName) {
		if (params.isScenarioVersionDefault()) {
			return mappingName;
		} else {
			return mappingName+"_"+params.scenarioVersion;
		}
	}
	
	/********************************************************************************************
	 *
	 *Query generation
	 *
	 ********************************************************************************************/
	@TypeChecked(TypeCheckingMode.SKIP)
	public String generateSqlDepCleanup(JcParameters params,String mappingName) {
		String jobId=this.generateJobId(params,mappingName);
		return this.getTemplateForSqlDependencyInit().make([
				jobId:jobId
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				]).toString()
	}
	
	@TypeChecked(TypeCheckingMode.SKIP)
	public String generateSqlDependency(JcParameters params,String whoMappingName,String onMappingName) {
		
		String whoJobId=this.generateJobId(params,whoMappingName);
		String onJobId=this.generateJobId(params,onMappingName);
		
			return this.getTemplateForSqlDependency().make([
				whoJobId:whoMappingName
				,onJobId:onJobId
				,onMappingName:onMappingName
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				]).toString()
	
	}
	@TypeChecked(TypeCheckingMode.SKIP)
	public String[] generateSqlGroupDefinition(JcParameters params) {
		List<String> rsp=[];
		rsp << this.getTemplateForSqlGroupJobInit().make([
				jobGroupName:params.jobGroupName
				]).toString();
		
		rsp << this.getTemplateForSqlGroupDefinitionInit().make([
				jobGroupName:params.jobGroupName
				]).toString();
		
		rsp << this.getTemplateForSqlGroupDefinition().make([
				jobGroupName:params.jobGroupName
				]).toString();
		return rsp.toArray();
	}
	
	@TypeChecked(TypeCheckingMode.SKIP)
	public String generateSqlGroupJobs(JcParameters params,String mappingName) {
		String jobId=this.generateJobId(params,mappingName);
		return this.getTemplateForSqlGroup().make([
				jobGroupName:params.jobGroupName
				,jobId:jobId
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				]).toString();
	}
	
	@TypeChecked(TypeCheckingMode.SKIP)
	public String generateSqlJobParameters(JcParameters params,String mappingName,String leadingSource) {
		
		String jobId=this.generateJobId(params,mappingName);
		
		return this.getTemplateForSqlParameters().make([
				jobGroupName:params.jobGroupName
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				,jobId:jobId
				,leadingSource:leadingSource
				]).toString();
	}
	
	@TypeChecked(TypeCheckingMode.SKIP)
	public String generateSqlJobDefinition(JcParameters params,String mappingName) {
		
		String jobId=this.generateJobId(params,mappingName);
		
		return this.getTemplateForSqlJobDefinition().make([
				jobGroupName:params.jobGroupName
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				,mappingName:mappingName
				,jobId:jobId
				,mappingScenarioVersion:params.scenarioVersion
				]).toString();
	}

}
