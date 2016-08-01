package ro.bcr.bita.app

import ro.bcr.bita.mapping.analyze.BitaMappingAnalyzerService;
import ro.bcr.bita.mapping.analyze.IMappingAnalyzerService;
import ro.bcr.bita.mapping.dependency.MappingAnalyzeDependencyProcessor
import ro.bcr.bita.mapping.dependency.jc.JcParameters;
import ro.bcr.bita.mapping.dependency.jc.JcSqlGenerator;
import ro.bcr.bita.mapping.dependency.jc.JcStreamSqlExecutor;
import ro.bcr.bita.model.BitaModelFactory;
import ro.bcr.bita.model.IBitaModelFactory;
import ro.bcr.bita.model.IMappingDependencyRepositoryCyclicAware;
import ro.bcr.bita.model.IMessageCollection;
import ro.bcr.bita.model.MappingDependencyRepository;
import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.proxy.OdiEntityFactory;
import ro.bcr.bita.odi.proxy.SqlUtil;
import ro.bcr.bita.sql.BitaBatchSql
import ro.bcr.bita.sql.BitaSqlSimpleOperations;
import ro.bcr.bita.sql.IBitaBatchSql;
import ro.bcr.bita.sql.IBitaSqlSimpleOperations;

import oracle.odi.core.OdiInstance;

class BitaAppFactory {
	
	private final IBitaModelFactory bitaModelFactory=BitaModelFactory.newInstance();
	private final IOdiEntityFactory odiEntityFactory;
	
	private BitaAppFactory(OdiInstance odiInstance) {
		this.odiEntityFactory=OdiEntityFactory.newInstance(odiInstance,bitaModelFactory);
	}
	
	public static BitaAppFactory newInstance(OdiInstance odiInstance) {
		return new BitaAppFactory(odiInstance);
	}
	
	
	private groovy.sql.Sql fetchDbConnection(String oracleServerName){
		return (new SqlUtil(this.odiEntityFactory)).newSqlInstanceFromServer(oracleServerName,"O_LDS_META","O_LDS_META");
	}
	
	
	
	public JcParameters newJcParameters(String dwhRelease,String dwhVersion,String jobGroupName) {
		return JcParameters.newParameters(jobGroupName,dwhRelease,dwhVersion);
	}
	
	
	
	public IMappingDependencyRepositoryCyclicAware createMappingDependencyRepository() {
		return new MappingDependencyRepository();
	}
	
	/********************************************************************************************
	 *
	 *Full Functional Medium level components
	 *
	 ********************************************************************************************/
		
	public JcStreamSqlExecutor newJcStreamSqlExecutor(String oracleServerName,Boolean applyCommits,String dwhRelease,String dwhVersion,String jobGroupName) {
		return new JcStreamSqlExecutor(createMappingDependencyRepository()
				,newSqlExecutor(oracleServerName)
				,newJcParameters(dwhRelease,dwhVersion,jobGroupName)
				,applyCommits
				);
	}
	
	public JcSqlGenerator newJcSqlGenerator() {
		return new JcSqlGenerator(new MappingAnalyzeDependencyProcessor(createMappingDependencyRepository()));
	}
	
	public IMappingAnalyzerService newMappingAnalyzerService() {
		IMappingAnalyzerService rsp=new BitaMappingAnalyzerService(this.odiEntityFactory.newOdiTemplate());
		return rsp;
	}
	
	public IBitaSqlSimpleOperations newSqlExecutor(String oracleServerName) {
		return new BitaSqlSimpleOperations(fetchDbConnection(oracleServerName));
	}
	
	public IBitaBatchSql newBatchSqlExecutor(String oracleServerName) {
		return new BitaBatchSql(fetchDbConnection(oracleServerName));
	}
	
	public IMessageCollection newMessageCollection(String id) {
		return bitaModelFactory.newMessageCollection(id);
	}
	
	/********************************************************************************************
	 *
	 *High Level Functions
	 *
	 ********************************************************************************************/
	
	public IJcMetadataCreator newJcMetadataBatchCreator(String oracleServerName,String dwhRelease,String dwhVersion,String jobGroupName) {
		return new JcMetadataBatchCreator(
			newJcParameters(dwhRelease,dwhVersion,jobGroupName)
			,newJcSqlGenerator()
			,newMappingAnalyzerService()
			,newBatchSqlExecutor(oracleServerName)
			,newMessageCollection("sqlGroup"),
			,newMessageCollection("sqlJob")
			,newMessageCollection("sqlDependencies")
			);
	}

	public IJcMetadataCreator newJcMetadataStreamCreator(String oracleServerName, int frequencyOfCommits,String dwhRelease,String dwhVersion,String jobGroupName) {
		return new JcMetadataStreamCreator(
			newJcStreamSqlExecutor(oracleServerName,frequencyOfCommits>0,dwhRelease,dwhVersion,jobGroupName)
			,newMappingAnalyzerService()
			);
		
	}
	
}
