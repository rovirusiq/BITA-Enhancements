package ro.bcr.bita.mapping.dependency.jc

import groovy.text.Template
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode

@CompileStatic
class JcSqlCommandsHelper {
	
	static final String SQL_DEPENDENCY_KEY="JC_DEPENDECY";
	static final String SQL_GROUP_KEY="JC_GROUP";
	static final String  SQL_PARAMETERS_KEY="JC_PARAMETERS";
	private static final String TEMPLATES_PATH="templates/sql/jc/"
	private static final String SQL_DEPENDENCY_CLEANUP='dependency_initial';
	private static final String SQL_DEPENDENCY_ADD='dependency_add';
	private static final String SQL_JOB_DEFINITION="job_definition";
	private static final String SQL_PARMETERS_INIT='job_parameters_init';
	private static final String SQL_PARMETERS_WITH_CHECK='job_parameters_with_check';
	private static final String SQL_GROUP_DEFINITION='group_definition';
	private static final String SQL_GROUP_DEFINITION_INIT='group_definition_init';
	private static final String SQL_GROUP_JOBS='group_jobs';
	private static final String SQL_GROUP_JOBS_INIT='group_jobs_init';
	
	
//	private Template tmplSqlDependecyInit=null;
//	private Template tmplSqlDependency=null;
//	private Template tmplSqlParametersInit=null;
//	private Template tmplSqlParametersWithCheck=null;
//	private Template tmplSqlGroupDefinition=null;
//	private Template tmplSqlJobDefinition=null;
//	private Template tmplSqlGroupDefinitionInit=null;
//	private Template tmplSqlGroupJobInit=null;
//	private Template tmplSqlGroup=null;
	/********************************************************************************************
	 *
	 *Constructors
	 *
	 ********************************************************************************************/
	protected JcSqlCommandsHelper() {
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
		return this.getTemplate(SQL_DEPENDENCY_CLEANUP);
	}
	
	private Template getTemplateForSqlDependency() {
		return this.getTemplate(SQL_DEPENDENCY_ADD);
	}
	
	private Template getTemplateForSqlJobParametersInit() {
		return this.getTemplate(SQL_PARMETERS_INIT);
	}
	
	
	private Template getTemplateForSqlParametersWithCheck() {
		return this.getTemplate(SQL_PARMETERS_WITH_CHECK);
	}
	
	
	private Template getTemplateForSqlGroupDefinitionInit() {
		return this.getTemplate(SQL_GROUP_DEFINITION_INIT);
	}
	
	private Template getTemplateForSqlGroupJobInit() {
		return this.getTemplate(SQL_GROUP_JOBS_INIT);
	}
	
	private Template getTemplateForSqlGroupDefinition() {
		return this.getTemplate(SQL_GROUP_DEFINITION);
	}
	
	private Template getTemplateForSqlJobDefinition() {
		return this.getTemplate(SQL_JOB_DEFINITION);
	}
	
	private Template getTemplateForSqlGroup() {
		return this.getTemplate(SQL_GROUP_JOBS);
	}
	
	/********************************************************************************************
	 *
	 *Query generation
	 *
	 ********************************************************************************************/
	@TypeChecked
	public List<String> generateSqlDependencyInit(JcParameters params,String jobId){
		List<String> rsp=[];
		
		rsp << this.getTemplateForSqlDependencyInit().make([
			jobId:jobId
			,dwhVersion:params.dwhVersion
			,dwhRelease:params.dwhRelease
			]).toString();
		
		return rsp;
	}
	
	
	@TypeChecked
	public List<String> generateSqlDependency(JcParameters params,String whoJobId,String onJobId) {
		
		List<String> rsp=[];
		
		rsp << this.getTemplateForSqlDependency().make([
				whoJobId:whoJobId
				,onJobId:onJobId
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				]).toString()
		return rsp;
	
	}
	@TypeChecked
	public List<String> generateSqlGroupDefinition(JcParameters params) {
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
		return rsp;
	}
	
	@TypeChecked
	public List<String> generateSqlGroupJobs(JcParameters params,String jobId) {
		List<String> rsp=[];
		rsp << this.getTemplateForSqlGroup().make([
				jobGroupName:params.jobGroupName
				,jobId:jobId
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				]).toString();
		return rsp;
	}
	
	@TypeChecked
	public List<String> generateSqlJobDefinition(JcParameters params,String jobId,String mappingName,String leadingSource,List<String> allowedParameters,boolean addCommandToDeleteDependency=true) {
		
		List<String> rsp=[];
		
		if (addCommandToDeleteDependency) {
			rsp << this.getTemplateForSqlDependencyInit().make([
					jobId:jobId
					,dwhVersion:params.dwhVersion
					,dwhRelease:params.dwhRelease
					]).toString()
		}
		if (params.isParameterDeleteRequired) {
			rsp << this.getTemplateForSqlJobParametersInit().make([
					jobId:jobId
					,dwhVersion:params.dwhVersion
					,dwhRelease:params.dwhRelease
					]).toString();
		}
		
		rsp << this.getTemplateForSqlJobDefinition().make([
				jobGroupName:params.jobGroupName
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				,mappingName:mappingName
				,jobId:jobId
				,mappingScenarioVersion:params.scenarioVersion
				]).toString();
				
		rsp << this.getTemplateForSqlParametersWithCheck().make([
				jobGroupName:params.jobGroupName
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				,jobId:jobId
				,leadingSource:leadingSource
				,scenarioParams:allowedParameters
				]).toString();
			
		return rsp;
	}

}
