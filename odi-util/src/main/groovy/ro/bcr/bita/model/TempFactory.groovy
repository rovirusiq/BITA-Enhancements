package ro.bcr.bita.model

import ro.bcr.bita.odi.proxy.OdiEntityFactory
import ro.bcr.bita.odi.proxy.SqlUtil
import ro.bcr.bita.odi.template.OdiBasicTemplate
import ro.bcr.bita.service.IBitaSql
import ro.bcr.bita.service.IBitaSqlSimpleOperations
import ro.bcr.bita.service.IMappingAnalyzeProcessor
import ro.bcr.bita.service.IMappingAnalyzer
import ro.bcr.bita.service.mapping.BitaMappingAnalyzerService
import ro.bcr.bita.service.mapping.JcAnalyzeProcessor
import ro.bcr.bita.service.mapping.JcParameters
import ro.bcr.bita.service.mapping.JcSqlGeneratorHelper
import ro.bcr.bita.service.mapping.JcSqlGenerator
import ro.bcr.bita.service.mapping.JcStreamSqlGenerator
import ro.bcr.bita.service.sql.BitaSql
import ro.bcr.bita.service.sql.BitaSqlSimpleOperations

import oracle.odi.core.OdiInstance

class TempFactory {

	
	/********************************************************************************************
	 *
	 *Temporary method
	 *
	 ********************************************************************************************/
	public IMappingAnalyzer newAnalyzerSerivce(OdiInstance odiInstance,IMappingAnalyzeProcessor gen) {
		OdiEntityFactory odiEntityFactory=new OdiEntityFactory(odiInstance);
		BitaMappingAnalyzerService srv=new BitaMappingAnalyzerService(new OdiBasicTemplate(odiEntityFactory,new BitaModelFactory()));
		srv.addAnalyzeProcessor(gen);
		return srv;
	}
	
	public JcSqlGenerator newJcSqlGenerator() {
		return new JcSqlGenerator(new JcAnalyzeProcessor(new MappingDependencyRepository()));
	}
	
	public JcStreamSqlGenerator newJcSqlStreamGenerator(IBitaSqlSimpleOperations sqlExecutor,JcParameters params) {
		return new JcStreamSqlGenerator(new MappingDependencyRepository(),sqlExecutor,params);
	}
	
	public JcStreamSqlGenerator newJcSqlStreamGeneratorV2(IBitaSqlSimpleOperations sqlExecutor,JcParameters params) {
		return new JcStreamSqlGenerator(new PotemkinMappingDependencyRepository(),sqlExecutor,params);
	}
	
	public JcStreamSqlGenerator newJcSqlStreamGeneratorV3(IBitaSqlSimpleOperations sqlExecutor,JcParameters params) {
		return new JcStreamSqlGenerator(new OptimizedMappingDependencyRepository(),sqlExecutor,params);
	}
	
	
	public  JcSqlGenerator.Parameters newSqlGeneratorParameters(String dwhRelease,String dwhVersion,String jobGroupName) {
		JcSqlGenerator.Parameters param=new JcSqlGenerator.Parameters();
		param.dwhRelease=dwhRelease;
		param.dwhVersion=dwhVersion;
		param.jobGroupName=jobGroupName;
		param.sqlJobDependencyStatements=new MessageCollection("jcSqlDep");
		param.sqlJobGroupStatements=new MessageCollection("jcSqlGroup");
		param.sqlJobParameterStatements=new MessageCollection("jcSqlParams");
		
		return param;
	}
	
	public  JcParameters newJcParameters(String dwhRelease,String dwhVersion,String jobGroupName) {
		JcParameters params=JcParameters.newParameters(dwhRelease,dwhVersion,jobGroupName);	
		return params;
	}
	
	public IBitaSql newBitaSqlService(OdiInstance odiInstance,String oracleServerName) {
		groovy.sql.Sql db=(new SqlUtil(new OdiEntityFactory(odiInstance))).newSqlInstanceFromServer(oracleServerName,"O_LDS_META","O_LDS_META");
		return new BitaSql(db);
	} 
	
	public IBitaSqlSimpleOperations newBitaSqlSimpleService(OdiInstance odiInstance,String oracleServerName) {
		groovy.sql.Sql db=(new SqlUtil(new OdiEntityFactory(odiInstance))).newSqlInstanceFromServer(oracleServerName,"O_LDS_META","O_LDS_META");
		return new BitaSqlSimpleOperations(db);
	}
	
}
