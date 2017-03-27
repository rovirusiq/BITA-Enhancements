package ro.bcr.bita.mapping.analyze

import groovy.text.Template
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode

@CompileStatic
public class JcSqlCommandsGenerator {
	
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
	public JcSqlCommandsGenerator() {
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
	public List<String> generateSqlDependencyInit(JcRequestContext params,String jobId){
		List<String> rsp=[];
		
		rsp << this.getTemplateForSqlDependencyInit().make([
			jobId:jobId
			,dwhVersion:params.dwhVersion
			,dwhRelease:params.dwhRelease
			,jcSchemaName:params.jcSchemaName
			]).toString();
		
		return rsp;
	}
	
	
	@TypeChecked
	public List<String> generateSqlDependency(JcRequestContext params,String whoJobId,String onJobId) {
		
		List<String> rsp=[];
		
		rsp << this.getTemplateForSqlDependency().make([
				whoJobId:whoJobId
				,onJobId:onJobId
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				,jcSchemaName:params.jcSchemaName
				]).toString()
		return rsp;
	
	}
	@TypeChecked
	public List<String> generateSqlGroupDefinition(JcRequestContext params,String jobGroupName) {
		List<String> rsp=[];
		
		rsp << this.getTemplateForSqlGroupJobInit().make([
				jobGroupName:jobGroupName
				,jcSchemaName:params.jcSchemaName
				]).toString();
		
		rsp << this.getTemplateForSqlGroupDefinitionInit().make([
				jobGroupName:jobGroupName
				,jcSchemaName:params.jcSchemaName
				]).toString();
		
		rsp << this.getTemplateForSqlGroupDefinition().make([
				jobGroupName:jobGroupName
				,jcSchemaName:params.jcSchemaName
				]).toString();
		return rsp;
	}
	
	@TypeChecked
	public List<String> generateSqlGroupJobs(JcRequestContext params,String jobGroupName,String jobId) {
		List<String> rsp=[];
		rsp << this.getTemplateForSqlGroup().make([
				jobGroupName:jobGroupName
				,jobId:jobId
				,dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				,jcSchemaName:params.jcSchemaName
				]).toString();
		return rsp;
	}
	
	@TypeChecked
	public List<String> generateSqlJobDefinition(JcRequestContext params,String jobId,String mappingName,String leadingSource,List<String> allowedParameters) {
		
		List<String> rsp=[];
		
		if (params.isDependencyDeleteRequired) {
			rsp << this.getTemplateForSqlDependencyInit().make([
					jobId:jobId
					,dwhVersion:params.dwhVersion
					,dwhRelease:params.dwhRelease
					,jcSchemaName:params.jcSchemaName
					]).toString()
		}
		if (params.isParameterDeleteRequired) {
			rsp << this.getTemplateForSqlJobParametersInit().make([
					jobId:jobId
					,dwhVersion:params.dwhVersion
					,dwhRelease:params.dwhRelease
					,jcSchemaName:params.jcSchemaName
					]).toString();
		}
		
		rsp << this.getTemplateForSqlJobDefinition().make([
				dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				,mappingName:mappingName
				,jobId:jobId
				,mappingScenarioVersion:params.scenarioVersion
				,jcSchemaName:params.jcSchemaName
				]).toString();
				
		rsp << this.getTemplateForSqlParametersWithCheck().make([
				dwhVersion:params.dwhVersion
				,dwhRelease:params.dwhRelease
				,jobId:jobId
				,leadingSource:leadingSource
				,scenarioParams:allowedParameters
				,jcSchemaName:params.jcSchemaName
				,jobGroupName:"NA"
				]).toString();
			
		return rsp;
	}

}
