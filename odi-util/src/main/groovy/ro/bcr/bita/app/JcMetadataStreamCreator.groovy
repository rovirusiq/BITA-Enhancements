package ro.bcr.bita.app

import ro.bcr.bita.mapping.analyze.IMappingAnalyzerService;
import ro.bcr.bita.mapping.dependency.jc.JcStreamSqlExecutor;

class JcMetadataStreamCreator implements IJcMetadataCreator {
	

	private final JcStreamSqlExecutor sqlExecutor;
	private final IMappingAnalyzerService srv;
	
	

	/**
	 * @param sqlExecutor
	 * @param srv
	 */
	protected JcMetadataStreamCreator(JcStreamSqlExecutor sqlExecutor,IMappingAnalyzerService srv) {
		super();
		this.sqlExecutor = sqlExecutor;
		this.srv = srv;
	}



	@Override
	public void createMetadata(String... odiPaths) {
		srv.addAnalyzeProcessor(sqlExecutor);
		sqlExecutor.generateAndExecuteSqlBeforeAnalysis();
		srv.analyzeMappingsFrom(odiPaths);
		sqlExecutor.generateAndExecuteSqlAfterAnalysis();
	}
}
