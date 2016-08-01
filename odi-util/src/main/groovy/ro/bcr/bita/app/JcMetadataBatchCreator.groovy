package ro.bcr.bita.app

import ro.bcr.bita.mapping.analyze.IMappingAnalyzerService;
import ro.bcr.bita.mapping.dependency.jc.JcParameters;
import ro.bcr.bita.mapping.dependency.jc.JcSqlGenerator;
import ro.bcr.bita.model.IMessageCollection;
import ro.bcr.bita.sql.IBitaBatchSql;

class JcMetadataBatchCreator implements IJcMetadataCreator {
	
	private final JcParameters params;
	private final JcSqlGenerator sqlGen;
	private final IMappingAnalyzerService srv;
	private ro.bcr.bita.sql.IBitaBatchSql sqlExecutor;
	private final IMessageCollection sqlG;
	private final IMessageCollection sqlJ;
	private final IMessageCollection sqlD;
	
	
	

	/**
	 * @param params
	 * @param sqlGen
	 * @param srv
	 * @param sqlExecutor
	 * @param sqlG
	 * @param sqlJ
	 * @param sqlD
	 */
	public JcMetadataBatchCreator(JcParameters params, JcSqlGenerator sqlGen,
			IMappingAnalyzerService srv, IBitaBatchSql sqlExecutor,
			IMessageCollection sqlG, IMessageCollection sqlJ,
			IMessageCollection sqlD) {
		super();
		this.params = params;
		this.sqlGen = sqlGen;
		this.srv = srv;
		this.sqlExecutor = sqlExecutor;
		this.sqlG = sqlG;
		this.sqlJ = sqlJ;
		this.sqlD = sqlD;
	}




	@Override
	public void createMetadata(String... odiPaths) {
		srv.addAnalyzeProcessor(sqlGen);
		srv.analyzeMappingsFrom(odiPaths);
		sqlGen.generateSql(params,sqlG,sqlJ,sqlD);
		sqlExecutor.executeInTransaction(sqlJ,sqlG,sqlD);
	}

}
