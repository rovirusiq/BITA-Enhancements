package ro.bcr.bita.service.mapping.dependency.jc

class JcSqlQueriesTestSupport {
	
	public static final String getJobGroupCleanup(String jobGroupName) {
		return """DELETE FROM O_LDS_META.CMT_JC_JOBGROUP where JOBGROUP_ID='$jobGroupName'""".toString();
	}
	
	public static final String getJobGroupDefinition(String jobGroupName) {
		return """INSERT INTO O_LDS_META.CMT_JC_JOBGROUP(JOBGROUP_ID,JOBGROUP_DESC,JOBGROUP_ACTIVE_IND)
VALUES('$jobGroupName','$jobGroupName','Y')""".toString();
	}
	
	
	public static final String getJobGroupJobAssociationCleanup(String jobGroupName) {
		return """DELETE FROM O_LDS_META.CMT_JC_JOBGROUP_X_JOB where JOBGROUP_ID='$jobGroupName'""".toString();
	}
	
	public static final String getJobGroupJobAssociation(String jobGroupName,String jobId,String dwhRelease,String dwhVersion) {
		return """INSERT INTO O_LDS_META.CMT_JC_JOBGROUP_X_JOB(JOBGROUP_ID,JOB_ID,DWH_VERSION_CD,RELEASE_CD,LINK_ACTIVE_FLAG)
VALUES('$jobGroupName','$jobId','$dwhVersion','$dwhRelease','Y')""".toString();
	}
	
	public static final String getJobDependencyCleanup(String jobId,String dwhRelease,String dwhVersion) {
		return """DELETE FROM O_LDS_META.CMT_JC_JOB_DEPENDENCY where DEPENDENT_JOB_ID='$jobId' and CMT_DWH_VERSION_CD='$dwhVersion' and CMT_RELEASE_CD='$dwhRelease'""".toString();
	}
	
	public static final String getJobDependencyDefinition(String onJobId,String whoJobId,String dwhRelease, String dwhVersion) {
		return """INSERT INTO O_LDS_META.CMT_JC_JOB_DEPENDENCY (JOB_ID,DWH_VERSION_CD,RELEASE_CD,DEPENDENT_JOB_ID,CMT_DWH_VERSION_CD,CMT_RELEASE_CD,DEPENDENCY_ACTIVE_IND)
VALUES('$onJobId','${dwhVersion}','${dwhRelease}','$whoJobId','${dwhVersion}','${dwhRelease}','Y')""".toString();
	}
	
	public static final String getJobDefinition(String jobId, String scenarioName,String scenarioVersion,String dwhRelease, String dwhVersion) {
		return """MERGE INTO O_LDS_META.CMT_JC_JOB j
using (
select '$jobId' as JOB_ID,'$dwhVersion' as DWH_VERSION_CD,'$dwhRelease' as RELEASE_CD,'$scenarioName' as ODI_SCENARIO_NAME,'$scenarioVersion' as ODI_SCENARIO_VERSION
, '0' as JOB_WEIGHT,'0'as JOB_PRIORITY,'Y' as JOB_ACTIVE_IND
, 'N' as AUTO_RESTART_IND
, 0 as AUTO_RESTART_RETRIES_NO
, 0 as AUTO_RESTART_WAIT_SECS
, '' as AUTO_RESTART_ERROR_CONDITION
, '' as JOB_START_CONDITION 
from dual
) n
on (j.job_id=n.job_id and j.dwh_version_cd=n.dwh_version_cd and j.release_cd=n.release_cd)
when not matched then
	insert (job_id,dwh_version_cd,release_cd,ODI_SCENARIO_NAME,ODI_SCENARIO_VERSION,JOB_WEIGHT,JOB_PRIORITY,JOB_ACTIVE_IND,AUTO_RESTART_IND,AUTO_RESTART_RETRIES_NO,AUTO_RESTART_WAIT_SECS,AUTO_RESTART_ERROR_CONDITION,JOB_START_CONDITION)  
		values (n.job_id,n.dwh_version_cd,n.release_cd,n.ODI_SCENARIO_NAME,n.ODI_SCENARIO_VERSION,n.JOB_WEIGHT,n.JOB_PRIORITY,n.JOB_ACTIVE_IND,n.AUTO_RESTART_IND,n.AUTO_RESTART_RETRIES_NO,n.AUTO_RESTART_WAIT_SECS,n.AUTO_RESTART_ERROR_CONDITION,n.JOB_START_CONDITION)""".toString()
	}
	
	public static final String getJobParametersDefinition(String jobGroupName,String jobId,String leadingSource,String dwhRelease,String dwhVersion) {
		return """MERGE INTO O_LDS_META.CMT_JC_PARAMETER e
using (
select '$jobId' as job_id,'$dwhVersion' as dwh_version_cd,'$dwhRelease' as release_cd,'$jobGroupName' as jobgroup_id,'GLOBAL.GVGRAN_ENTITY_CD' as parameter_name,'BCR' as parameter_value,null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_APPLICATION_CD','BCRAPP',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_SOURCE_DT','@cob_dt@',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_VERSION_NUMBER','1.0.0',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_DEBUG_MODE_IND','Y',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_SOURCE_STREAM_CD','$leadingSource',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRNN_LOAD_BATCH_ID','@batch_id@',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_COB_DT','@cob_dt@',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_FILE_NAME','@auto_file_name@',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_SOURCE_SYSTEM_CD','SOURCE',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_CORRECTION_ROW_IND','N',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_TECHNICAL_DT','@cob_dt@',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_EXTRACT_TS','@cob_dt@ 10:00:00',null from dual
) n
on (e.job_id=n.job_id and e.dwh_version_cd=n.dwh_version_cd and e.release_cd=n.release_cd and e.jobgroup_id=n.jobgroup_id and e.parameter_name=n.parameter_name)
when not matched then
	insert (job_id,dwh_version_cd,release_cd,jobgroup_id,parameter_name,parameter_value)  
		values (n.job_id,n.dwh_version_cd,n.release_cd,n.jobgroup_id,n.parameter_name,n.parameter_value)""".toString()
	}

}
