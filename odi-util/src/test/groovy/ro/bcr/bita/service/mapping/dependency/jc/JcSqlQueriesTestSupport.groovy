package ro.bcr.bita.service.mapping.dependency.jc

import java.util.List;

class JcSqlQueriesTestSupport {
	
	public static final List<String> getJobGroupDefinition(String jobGroupName) {
		List<String> rsp=[];
		
		rsp << """DELETE FROM O_LDS_META.CMT_JC_JOBGROUP_X_JOB where JOBGROUP_ID='$jobGroupName'""".toString();
		
		rsp << """DELETE FROM O_LDS_META.CMT_JC_JOBGROUP where JOBGROUP_ID='$jobGroupName'""".toString();
		
		rsp << """INSERT INTO O_LDS_META.CMT_JC_JOBGROUP(JOBGROUP_ID,JOBGROUP_DESC,JOBGROUP_ACTIVE_IND)
VALUES('$jobGroupName','$jobGroupName','Y')""".toString();

		return rsp;
	}
	
	public static final List<String> getJobGroupJobAssociation(String jobGroupName,String jobId,String dwhRelease,String dwhVersion) {
		List<String> rsp=[];
		rsp << """INSERT INTO O_LDS_META.CMT_JC_JOBGROUP_X_JOB(JOBGROUP_ID,JOB_ID,DWH_VERSION_CD,RELEASE_CD,LINK_ACTIVE_FLAG)
VALUES('$jobGroupName','$jobId','$dwhVersion','$dwhRelease','Y')""".toString();
		return rsp;
	}
	
	public static final List<String> getJobDependencyInit(String jobId,String dwhRelease, String dwhVersion) {
		List<String> rsp=[];
		rsp << """DELETE FROM O_LDS_META.CMT_JC_JOB_DEPENDENCY where DEPENDENT_JOB_ID='$jobId'""".toString();
		return rsp;
	}
	
	public static final List<String> getJobDependencyDefinition(String onJobId,String whoJobId,String dwhRelease, String dwhVersion) {
		List<String> rsp=[];
		rsp << """INSERT INTO O_LDS_META.CMT_JC_JOB_DEPENDENCY (JOB_ID,DWH_VERSION_CD,RELEASE_CD,DEPENDENT_JOB_ID,CMT_DWH_VERSION_CD,CMT_RELEASE_CD,DEPENDENCY_ACTIVE_IND)
VALUES('$onJobId','${dwhVersion}','${dwhRelease}','$whoJobId','${dwhVersion}','${dwhRelease}','Y')""".toString();
		return rsp;
	}
	
	public static final List<String> getJobDefinition(String jobGroupName,String jobId, String scenarioName,String scenarioVersion,String dwhRelease, String dwhVersion,String leadingSource,boolean deleteExistingParameters,boolean deleteDependencies) {
		List<String> rsp=[];
		
		if (deleteDependencies) {
			rsp << getJobDependencyInit(jobId,dwhRelease,dwhVersion);
		}
		if (deleteExistingParameters) {
			rsp << """DELETE FROM  O_LDS_META.CMT_JC_PARAMETER where job_id='$jobId'""".toString();
		}
		
		rsp << """MERGE INTO O_LDS_META.CMT_JC_JOB j
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
		values (n.job_id,n.dwh_version_cd,n.release_cd,n.ODI_SCENARIO_NAME,n.ODI_SCENARIO_VERSION,n.JOB_WEIGHT,n.JOB_PRIORITY,n.JOB_ACTIVE_IND,n.AUTO_RESTART_IND,n.AUTO_RESTART_RETRIES_NO,n.AUTO_RESTART_WAIT_SECS,n.AUTO_RESTART_ERROR_CONDITION,n.JOB_START_CONDITION)"""
		
		rsp << """MERGE INTO O_LDS_META.CMT_JC_PARAMETER e
using (
select
'$jobId' as job_id
,'$dwhVersion' as dwh_version_cd
,'$dwhRelease' as release_cd
,'$jobGroupName' as jobgroup_id
,'GLOBAL.GVGRAN_COB_DT' as parameter_name
,'@cob_dt@' as parameter_value
,null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_SOURCE_SYSTEM_CD','SOURCE',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_AUDIT_EXECUTION_ID','',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_FILE_NAME','@auto_file_name@',null from dual
union
select '$jobId','$dwhVersion','$dwhRelease','$jobGroupName','GLOBAL.GVGRAN_LOAD_BATCH_ID','@batch_id@',null from dual
) n
on (e.job_id=n.job_id and e.dwh_version_cd=n.dwh_version_cd and e.release_cd=n.release_cd and e.jobgroup_id=n.jobgroup_id)
when not matched then
	insert (job_id,dwh_version_cd,release_cd,jobgroup_id,parameter_name,parameter_value)  
		values (n.job_id,n.dwh_version_cd,n.release_cd,n.jobgroup_id,n.parameter_name,n.parameter_value)""".toString()
		
		return rsp.flatten();
	}

}
