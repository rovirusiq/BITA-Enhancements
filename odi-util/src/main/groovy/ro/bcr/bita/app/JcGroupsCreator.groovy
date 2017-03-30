package ro.bcr.bita.app

import ro.bcr.bita.core.IBitaGlobals
import ro.bcr.bita.mapping.analyze.IJcGroupIdentificationStrategy
import ro.bcr.bita.mapping.analyze.IJcJobIdGenerator
import ro.bcr.bita.mapping.analyze.IMappingAnalyzerService
import ro.bcr.bita.mapping.analyze.JcRequestContext
import ro.bcr.bita.mapping.analyze.JcSqlCommandsGenerator
import ro.bcr.bita.mapping.analyze.JcSqlExecutor
import ro.bcr.bita.model.IBitaDomainFactory
import ro.bcr.bita.odi.proxy.IOdiEntityFactory
import ro.bcr.bita.sql.IBitaSqlStatementExecutor

import groovy.transform.CompileStatic

@CompileStatic
public class JcGroupsCreator implements IJcJobGroupsCreator,IBitaGlobals{
	
	
	private final IOdiEntityFactory odiEntityFactory;
	private final IBitaDomainFactory bitaDomainFactory;
	
	private IBitaJdbcConnectionProvider bitaJdbcConnectionProvider;
	private IJcJobIdGenerator jobIdPolicy;
	private IJcGroupIdentificationStrategy groupIdentificationStrategy;
	private String dwhVersionCode;
	private String jcMetadataSchemaName=IBitaGlobals.BITA_DEFAULT_META_SCHEMA;
	private String scenarioVersion=IBitaGlobals.BITA_ODI_SCENARIO_VERSION;
	
	
	protected JcGroupsCreator(IBitaDomainFactory bitaDomainFactory,IOdiEntityFactory odiEntityFactory) {
		this.odiEntityFactory=odiEntityFactory;
		this.bitaDomainFactory=bitaDomainFactory;
	}
	

	@Override
	public void setJdbcProvider( IBitaJdbcConnectionProvider bitaJdbcConnectionProvider) {
			this.bitaJdbcConnectionProvider=bitaJdbcConnectionProvider;
	}

	@Override
	public void setDwhVersionCode(String dwhVersionCd) {
		this.dwhVersionCode=dwhVersionCd;
	}

	@Override
	public void setJcMetadatdaSchemaName(String schemaName) {
		this.jcMetadataSchemaName=schemaName;
	}

	@Override
	public void setScenarioVersion(String scenarioVersion) {
		this.scenarioVersion=scenarioVersion;
	}

	@Override
	public void setJobIdPolicy(IJcJobIdGenerator namePolicy) {
		this.jobIdPolicy=namePolicy;
	}

	@Override
	public void setJcGroupIdentificationStrategy(IJcGroupIdentificationStrategy strategy) {
		this.groupIdentificationStrategy=strategy;
	}

	@Override
	public void createJcGroups(String... odiPaths) throws RuntimeException{
		
		if (jobIdPolicy==null) throw new RuntimeException ("For Jc Groups to be created a Name Policy for JOB_ID must be defined")
		if (bitaJdbcConnectionProvider==null) throw new RuntimeException ("For Jc Groups to be created a BitaJdbcConnectionProvider must be defined")
		if (groupIdentificationStrategy==null) throw new RuntimeException ("For Jc Groups to be created a GroupIdentificationStrategy must be defined")
		if ( (dwhVersionCode==null) || ("".equals(dwhVersionCode)) ) throw new RuntimeException ("For Jc Groups to be created a DWH Version must be defined")
		
		JcRequestContext jcContext=new JcRequestContext(dwhVersionCode,groupIdentificationStrategy);
		jcContext=jcContext.newJobIdGenerator(jobIdPolicy);
		jcContext=jcContext.newJcSchemaName(jcMetadataSchemaName);
		jcContext=jcContext.newScenarioVersion(scenarioVersion);
		
		
		IMappingAnalyzerService srvMappingAnalyzer=this.bitaDomainFactory.newMappingAnalyzerService();
		
		IBitaSqlStatementExecutor sqlBita=this.bitaDomainFactory.newBitaSqlStatementExecutor(bitaJdbcConnectionProvider);
		JcSqlCommandsGenerator sqlCommandsHelper=this.bitaDomainFactory.newJcSqlCommandGenerator();
		
		JcSqlExecutor srvMappingProcessor=this.bitaDomainFactory.newJcSqlExecutor(jcContext,sqlBita);
		
		try {
			srvMappingAnalyzer.addAnalyzeProcessor(srvMappingProcessor);
			srvMappingAnalyzer.analyzeMappingsFrom(odiPaths);
			sqlBita.commit();
		} catch (Exception ex) {
			sqlBita.rollback();
			throw new RuntimeException("Exception occured during execution of script:",ex)
		} finally {
			sqlBita.close();
		}
		
		
	}

}
