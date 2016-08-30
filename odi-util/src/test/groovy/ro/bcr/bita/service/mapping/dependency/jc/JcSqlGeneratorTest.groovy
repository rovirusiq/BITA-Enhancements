package ro.bcr.bita.service.mapping.dependency.jc

import ro.bcr.bita.mapping.dependency.MappingAnalyzeDependencyProcessor;
import ro.bcr.bita.mapping.dependency.jc.JcParameters
import ro.bcr.bita.mapping.dependency.jc.JcSqlGenerator;
import ro.bcr.bita.model.IMessageCollection;
import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.model.MappingDependency;
import ro.bcr.bita.model.MessageCollection;
import ro.bcr.bita.model.MessageCollectionUtil

import spock.lang.Specification

class JcSqlGeneratorTest extends Specification{
	
	JcSqlGenerator subject;
	MappingAnalyzeDependencyProcessor processor;
	JcParameters params;
	IMessageCollection sqlDependency;
	IMessageCollection sqlGroup;
	IMessageCollection sqlJob;
	
	def setup() {
		processor=Mock();
		subject=new JcSqlGenerator(processor);
		params=JcParameters.newParameters("JC_BMF","R1","V1");
		sqlDependency=new MessageCollection("test1");
		sqlGroup=new MessageCollection("test2");
		sqlJob=new MessageCollection("test3");
	}
	
	def "Dependency SQL - No mapings, empty set of dependencies"(){
		given:	"The objects from setup and an empty set of dependencies"
				Set<MappingDependency> stDeps=[];
				Set<IOdiMapping> myMappings=[];
		when:	"The generateSql method is called"
				subject.generateSql(params,sqlGroup,sqlJob,sqlDependency);
		then:	"the interactions are as below"
				1 * processor.getAllMappings() >> myMappings;
				0 * processor.getDependencies() >> stDeps;
		then:	"we will get empty IMessageCollection(s) with sqls"
				IMessageCollection sqlDep=sqlDependency;
				IMessageCollection sqlGroup=sqlGroup;
				IMessageCollection sqlParameter=sqlJob;
				sqlDep.getNoOfKeys()==0;
				sqlGroup.getNoOfKeys()==0;
				sqlParameter.getNoOfKeys()==0;
	}
	
	def "Dependency SQL - 1 mapping and empty set of dependencies"(){
		given:	"The objects from setup a Set of maps and  a Set of dependencies"
				Set<MappingDependency> stDeps=[];
				IOdiMapping myMap1=Mock();
				Set<IOdiMapping> myMappings=[myMap1];
		when:	"The generateSql method is called"
				subject.generateSql(params,sqlGroup,sqlJob,sqlDependency);
		then:	"the interactions are as below"
				1 * processor.getAllMappings() >> myMappings;
				myMap1.getName() >> "MP_A";
				myMap1.getLeadingSource() >> "LC_A";
				1 * processor.getDependencies() >> stDeps;
		then:	"we get sql defintions in for group definition and group parameters " 
				IMessageCollection sqlDep=sqlDependency;
				IMessageCollection sqlGroup=sqlGroup;
				IMessageCollection sqlParameter=sqlJob;
				sqlDep.getNoOfKeys()==1;
				sqlDep.getNoOfMessagesForKey("MP_A")==1;//delete dependencies
				sqlGroup.getNoOfKeys()==2;//one group, one mapping
				sqlGroup.getNoOfMessagesForKey(params.jobGroupName)==3;//1 delete associations, 1 delete,1 create
				sqlGroup.getNoOfMessagesForKey("MP_A")==1;//1 association group job
				sqlParameter.getNoOfKeys()==1;//one mapping
				sqlParameter.getNoOfMessagesForKey("MP_A")==2;//1 merge for job definition, 1 merge for parameters
	}

	
	def "Dependency SQL - 3 mapping and 2 dependencies"(){
		given:	"The objects from setup a Set of maps and  a Set of dependencies"
				Set<MappingDependency> stDeps=[
					new MappingDependency("MP_A","MP_B")
					,new MappingDependency("MP_A","MP_C")
				];
				IOdiMapping myMap1=Mock();
				IOdiMapping myMap2=Mock();
				IOdiMapping myMap3=Mock();
				Set<IOdiMapping> myMappings=[myMap1,myMap2,myMap3];
		when:	"The generateSql method is called"
				subject.generateSql(params,sqlGroup,sqlJob,sqlDependency);
		then:	"the interactions are as below"
				1 * processor.getAllMappings() >> myMappings;
				myMap1.getName() >> "MP_A";
				myMap1.getLeadingSource() >> "LC_A";
				myMap2.getName() >> "MP_B";
				myMap2.getLeadingSource() >> "LC_B";
				myMap3.getName() >> "MP_C";
				myMap3.getLeadingSource() >> "LC_C";
				1 * processor.getDependencies() >> stDeps;
		then:	"we get sql defintions in for group definition and group parameters "
				IMessageCollection sqlDep=sqlDependency;
				IMessageCollection sqlGroup=sqlGroup;
				IMessageCollection sqlParameter=sqlJob;
				sqlDep.getNoOfKeys()==3;
				sqlDep.getNoOfMessagesForKey("MP_A")==3;//1 delete dependencies, add 2 dependency
				sqlDep.getNoOfMessagesForKey("MP_B")==1;//1 delete dependencies
				sqlDep.getNoOfMessagesForKey("MP_C")==1;//1 delete dependencies
				sqlGroup.getNoOfKeys()==4;//one group, 3 mappings
				sqlGroup.getNoOfMessagesForKey(params.jobGroupName)==3;//1 delete associations, 1 delete, 1 create
				sqlGroup.getNoOfMessagesForKey("MP_A")==1;//1 association
				sqlGroup.getNoOfMessagesForKey("MP_B")==1;//1 association
				sqlGroup.getNoOfMessagesForKey("MP_C")==1;//1 association
				sqlParameter.getNoOfKeys()==3;//3 mapping
				sqlParameter.getNoOfMessagesForKey("MP_A")==2;//1 merge, 1 merge parameters
				sqlParameter.getNoOfMessagesForKey("MP_B")==2;//1 merge, 1 merge parameters
				sqlParameter.getNoOfMessagesForKey("MP_C")==2;//1 merge, 1 merge parameters
		then:	"we test the queries for the group definition"
				
				sqlGroup.getMessagesForKey(params.jobGroupName).contains("""DELETE FROM O_LDS_META.CMT_JC_JOBGROUP where JOBGROUP_ID='$params.jobGroupName'""".toString());
				sqlGroup.getMessagesForKey(params.jobGroupName).contains("""INSERT INTO O_LDS_META.CMT_JC_JOBGROUP(JOBGROUP_ID,JOBGROUP_DESC,JOBGROUP_ACTIVE_IND)
VALUES('$params.jobGroupName','$params.jobGroupName','Y')""".toString()
				);
				sqlGroup.getMessagesForKey('MP_A').contains(
				"""INSERT INTO O_LDS_META.CMT_JC_JOBGROUP_X_JOB(JOBGROUP_ID,JOB_ID,DWH_VERSION_CD,RELEASE_CD,LINK_ACTIVE_FLAG)
VALUES('$params.jobGroupName','MP_A','$params.dwhVersion','$params.dwhRelease','Y')""".toString()
				);
				sqlGroup.getMessagesForKey('MP_B').contains(
				"""INSERT INTO O_LDS_META.CMT_JC_JOBGROUP_X_JOB(JOBGROUP_ID,JOB_ID,DWH_VERSION_CD,RELEASE_CD,LINK_ACTIVE_FLAG)
VALUES('$params.jobGroupName','MP_B','$params.dwhVersion','$params.dwhRelease','Y')""".toString()
				);
				sqlGroup.getMessagesForKey('MP_C').contains(
				"""INSERT INTO O_LDS_META.CMT_JC_JOBGROUP_X_JOB(JOBGROUP_ID,JOB_ID,DWH_VERSION_CD,RELEASE_CD,LINK_ACTIVE_FLAG)
VALUES('$params.jobGroupName','MP_C','$params.dwhVersion','$params.dwhRelease','Y')""".toString()
				);
			
		then:	"we test queries for job definition"
				sqlParameter.getMessagesForKey("MP_A").contains("""MERGE INTO O_LDS_META.CMT_JC_JOB j
using (
select 'MP_A' as JOB_ID,'$params.dwhVersion' as DWH_VERSION_CD,'$params.dwhRelease' as RELEASE_CD,'MP_A' as ODI_SCENARIO_NAME,'001' as ODI_SCENARIO_VERSION
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
				);
				String calculatedSQL="DELETE FROM O_LDS_META.CMT_JC_JOB_DEPENDENCY where DEPENDENT_JOB_ID='MP_A' and CMT_DWH_VERSION_CD='${params.dwhVersion}' and CMT_RELEASE_CD='${params.dwhRelease}'";
		then:	"we test the queries for the dependencies of MP_A"
				//sqlDep.getMessagesForKey("MP_A").contains("""DELETE FROM O_LDS_META.CMT_JC_JOB_DEPENDENCY where DEPENDENT_JOB_ID='MP_A' and CMT_DWH_VERSION_CD='${params.dwhVersion}' and CMT_RELEASE_CD='${params.dwhRelease}'""");	
				sqlDep.getMessagesForKey("MP_A").contains(calculatedSQL);
				sqlDep.getMessagesForKey("MP_A").contains("""INSERT INTO O_LDS_META.CMT_JC_JOB_DEPENDENCY (JOB_ID,DWH_VERSION_CD,RELEASE_CD,DEPENDENT_JOB_ID,CMT_DWH_VERSION_CD,CMT_RELEASE_CD,DEPENDENCY_ACTIVE_IND)
VALUES('MP_B','${params.dwhVersion}','${params.dwhRelease}','MP_A','${params.dwhVersion}','${params.dwhRelease}','Y')""".toString());
				sqlDep.getMessagesForKey("MP_A").contains("""INSERT INTO O_LDS_META.CMT_JC_JOB_DEPENDENCY (JOB_ID,DWH_VERSION_CD,RELEASE_CD,DEPENDENT_JOB_ID,CMT_DWH_VERSION_CD,CMT_RELEASE_CD,DEPENDENCY_ACTIVE_IND)
VALUES('MP_C','${params.dwhVersion}','${params.dwhRelease}','MP_A','${params.dwhVersion}','${params.dwhRelease}','Y')""".toString());
		then:	"we test the queries for the paramters of MP_B"
				sqlParameter.getMessagesForKey("MP_B").contains("""MERGE INTO O_LDS_META.CMT_JC_PARAMETER e
using (
select 'MP_B' as job_id,'$params.dwhVersion' as dwh_version_cd,'$params.dwhRelease' as release_cd,'$params.jobGroupName' as jobgroup_id,'GLOBAL.GVGRAN_ENTITY_CD' as parameter_name,'BCR' as parameter_value,null from dual
union
select 'MP_B','$params.dwhVersion','$params.dwhRelease','$params.jobGroupName','GLOBAL.GVGRAN_APPLICATION_CD','BCRAPP',null from dual
union
select 'MP_B','$params.dwhVersion','$params.dwhRelease','$params.jobGroupName','GLOBAL.GVGRAN_SOURCE_DT','@cob_dt@',null from dual
union
select 'MP_B','$params.dwhVersion','$params.dwhRelease','$params.jobGroupName','GLOBAL.GVGRAN_VERSION_NUMBER','1.0.0',null from dual
union
select 'MP_B','$params.dwhVersion','$params.dwhRelease','$params.jobGroupName','GLOBAL.GVGRAN_DEBUG_MODE_IND','Y',null from dual
union
select 'MP_B','$params.dwhVersion','$params.dwhRelease','$params.jobGroupName','GLOBAL.GVGRAN_SOURCE_STREAM_CD','LC_B',null from dual
union
select 'MP_B','$params.dwhVersion','$params.dwhRelease','$params.jobGroupName','GLOBAL.GVGRNN_LOAD_BATCH_ID','@batch_id@',null from dual
union
select 'MP_B','$params.dwhVersion','$params.dwhRelease','$params.jobGroupName','GLOBAL.GVGRAN_COB_DT','@cob_dt@',null from dual
union
select 'MP_B','$params.dwhVersion','$params.dwhRelease','$params.jobGroupName','GLOBAL.GVGRAN_FILE_NAME','@auto_file_name@',null from dual
union
select 'MP_B','$params.dwhVersion','$params.dwhRelease','$params.jobGroupName','GLOBAL.GVGRAN_SOURCE_SYSTEM_CD','SOURCE',null from dual
union
select 'MP_B','$params.dwhVersion','$params.dwhRelease','$params.jobGroupName','GLOBAL.GVGRAN_CORRECTION_ROW_IND','N',null from dual
union
select 'MP_B','$params.dwhVersion','$params.dwhRelease','$params.jobGroupName','GLOBAL.GVGRAN_TECHNICAL_DT','@cob_dt@',null from dual
union
select 'MP_B','$params.dwhVersion','$params.dwhRelease','$params.jobGroupName','GLOBAL.GVGRAN_EXTRACT_TS','@cob_dt@ 10:00:00',null from dual
) n
on (e.job_id=n.job_id and e.dwh_version_cd=n.dwh_version_cd and e.release_cd=n.release_cd and e.jobgroup_id=n.jobgroup_id and e.parameter_name=n.parameter_name)
when not matched then
	insert (job_id,dwh_version_cd,release_cd,jobgroup_id,parameter_name,parameter_value)  
		values (n.job_id,n.dwh_version_cd,n.release_cd,n.jobgroup_id,n.parameter_name,n.parameter_value)""".toString()
		);		
			
	}
	
	def "Dependency SQL - 2 mapping and 1 dependencies, same IMessageCollection"(){
		given:	"The objects from setup a Set of maps and  a Set of dependencies"
				Set<MappingDependency> stDeps=[
					new MappingDependency("MP_A","MP_B")
				];
				
				IOdiMapping myMap1=Mock();
				IOdiMapping myMap2=Mock();
				Set<IOdiMapping> myMappings=[myMap1,myMap2];
				sqlDependency=new MessageCollection("newTest");
				sqlGroup=sqlDependency;
				sqlJob=sqlDependency;
		when:	"The generateSql method is called"
				subject.generateSql(params,sqlGroup,sqlJob,sqlDependency);
		then:	"the interactions are as below"
				processor.getAllMappings() >> myMappings;
				processor.getDependencies() >> stDeps;
				myMap1.getName() >> "MP_A";
				myMap1.getLeadingSource() >> "LC_A";
				myMap2.getName() >> "MP_B";
				myMap2.getLeadingSource() >> "LC_B";
		then:	"we see check to see the sqls were generated in the same collection"
				IMessageCollection sqlDep=sqlDependency;
				IMessageCollection sqlGroup=sqlGroup;
				IMessageCollection sqlParameter=sqlJob;
				sqlDep==sqlGroup;
				sqlDep==sqlParameter;
				sqlDep.getNoOfKeys()==3;//1 group, 2 mappings
				sqlDep.getKeys().containsAll("$params.jobGroupName".toString(),"MP_A","MP_B");
	}
	
}
