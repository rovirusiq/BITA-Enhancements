package ro.bcr.bita.model

import ro.bcr.bita.app.IBitaJdbcConnectionProvider
import ro.bcr.bita.mapping.analyze.BitaMappingAnalyzerService
import ro.bcr.bita.mapping.analyze.IMappingAnalyzerService
import ro.bcr.bita.mapping.analyze.JcRequestContext
import ro.bcr.bita.mapping.analyze.JcSqlCommandsGenerator
import ro.bcr.bita.mapping.analyze.JcSqlExecutor
import ro.bcr.bita.mapping.analyze.MappingDependencyAnalyzerProcessor
import ro.bcr.bita.odi.proxy.BitaOdiException
import ro.bcr.bita.odi.proxy.IOdiBasicPersistenceService
import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.proxy.IOdiFullMappingPath
import ro.bcr.bita.odi.proxy.IOdiOperationsService;
import ro.bcr.bita.odi.proxy.OdiPathUtil
import ro.bcr.bita.odi.proxy.OdiProjectPaths
import ro.bcr.bita.odi.template.IOdiCommandContext;
import ro.bcr.bita.odi.template.OdiBasicTemplate;
import ro.bcr.bita.odi.template.OdiCommandContext;
import ro.bcr.bita.odi.template.OdiOperationsService;
import ro.bcr.bita.sql.BitaSqlStatementExecutor
import ro.bcr.bita.sql.IBitaSqlExecutor
import ro.bcr.bita.sql.IBitaSqlStatementExecutor

import java.util.Map
import java.util.Set

import com.sun.org.apache.bcel.internal.generic.RETURN

import groovy.lang.Delegate;
import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked;
import oracle.odi.core.persistence.transaction.ITransactionStatus
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.domain.runtime.scenario.OdiScenario as RealOdiScenario;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFolderFinder
import oracle.odi.domain.topology.finder.IOdiContextFinder
import oracle.odi.domain.topology.finder.IOdiDataServerFinder
import oracle.odi.domain.topology.finder.IOdiLogicalSchemaFinder
import oracle.odi.domain.topology.finder.IOdiPhysicalSchemaFinder

class BitaDomainFactory implements IBitaDomainFactory {
	
	private final IBitaModelFactory bitaFactory;
	private final IOdiEntityFactory odiFactory;
	private final IOdiOperationsService odiOpSrv;
	
	
	protected BitaDomainFactory(IBitaModelFactory bitaFactory,IOdiEntityFactory odiFactory) {
		this.bitaFactory=bitaFactory;
		this.odiFactory=odiFactory;
		this.odiOpSrv=new OdiOperationsService(this);
	}

	
	@Override
	@TypeChecked
	public  IOdiCommandContext newOdiTemplateCommandContext(){
		return new OdiCommandContext(this.odiFactory,this.odiOpSrv,this.odiFactory.newOdiBasicPersistenceService());
	}
	
	@Override
	@TypeChecked
	public OdiBasicTemplate newOdiTemplate() {
		return new OdiBasicTemplate(this);
	}
	
	@Override
	public IOdiOperationsService newOdiOperationsService() {
		return this.odiOpSrv;
	}
	
	@Override
	public IOdiMapping newOdiMapping(Mapping odiObject) {
		return new OdiMapping(odiObject,this);
	}
	

	@Override
	public IOdiScenario newOdiScenario(RealOdiScenario odiObject) {
		return new OdiScenario(odiObject);
	}
	
	@Override
	public IMappingAnalyzerService newMappingAnalyzerService() {
		IMappingAnalyzerService rsp=new BitaMappingAnalyzerService(this.newOdiTemplate());
		return rsp;
	}
	
	@Override
	public  MappingDependencyAnalyzerProcessor newMappingDependencyAnalyzerProcessor() {
		return new MappingDependencyAnalyzerProcessor(this.createMappingDependencyRepository());
	}
	
	@Override
	public IMappingRepository createMappingDependencyRepository() {
		return new MappingDependencyRepository();
	}
	
	@Override
	public JcSqlCommandsGenerator newJcSqlCommandGenerator() {
		return new JcSqlCommandsGenerator();
	}
	
	@Override
	public IBitaSqlStatementExecutor newBitaSqlStatementExecutor(IBitaJdbcConnectionProvider jdbcProvider) {
		return new BitaSqlStatementExecutor(jdbcProvider.getGroovySql());
	}
	
	@Override
	public JcSqlExecutor newJcSqlExecutor(JcRequestContext params,IBitaSqlExecutor bitaSqlExecutor) {
		return new JcSqlExecutor(this,params,bitaSqlExecutor);
	}
	
	
	/********************************************************************************************
	 *
	 * Static methods
	 *
	 ********************************************************************************************/
	public static final newInstance(IBitaModelFactory bitaFactory,IOdiEntityFactory odiFactory) {
		return new BitaDomainFactory(bitaFactory,odiFactory);
	}


	@Override
	public IDependency<String, String> newMappingDependency(String who,
			String on) {
		return this.bitaFactory.newMappingDependency(who,on);
	}


	@Override
	public IMappingDependencyRepositoryCyclicAware<String, String> newMappingDeppendencyRepository() {
		return this.bitaFactory.newMappingDeppendencyRepository();
	}


	@Override
	public IMessageCollection newMessageCollection(String idCollection) {
		this.bitaFactory.newMessageCollection(idCollection);
	}


	@Override
	public IOdiScenarioFolderFinder createScenarioFolderFinder()
			throws BitaOdiException {
		return this.odiFactory.createScenarioFolderFinder()
	}


	@Override
	public IOdiScenarioFinder createScenarioFinder() throws BitaOdiException {
		return this.odiFactory.createScenarioFinder();
	}


	@Override
	public IOdiProjectFinder createProjectFinder() throws BitaOdiException {
		return this.odiFactory.createProjectFinder();
	}


	@Override
	public IOdiFolderFinder createProjectFolderFinder() throws BitaOdiException {
		return this.odiFactory.createProjectFolderFinder();
	}


	@Override
	public IMappingFinder createMappingFinder() throws BitaOdiException {
		return this.odiFactory.createMappingFinder();
	}


	@Override
	public IOdiPhysicalSchemaFinder createPhysicalSchemaFinder()
			throws BitaOdiException {
		return this.odiFactory.createPhysicalSchemaFinder();
	}


	@Override
	public IOdiLogicalSchemaFinder createLogicalSchemaFinder()
			throws BitaOdiException {
		return this.odiFactory.createLogicalSchemaFinder();
	}


	@Override
	public IOdiContextFinder createContextFinder() throws BitaOdiException {
		return this.odiFactory.createContextFinder();
	}


	@Override
	public IOdiDataServerFinder createDataServerFinder()
			throws BitaOdiException {
		return this.odiFactory.createDataServerFinder();
	}


	@Override
	public ITransactionStatus createTransaction() throws BitaOdiException {
		return this.odiFactory.createTransaction();
	}


	@Override
	public OdiPathUtil newOdiPathUtil() {
		return this.odiFactory.newOdiPathUtil();
	}


	@Override
	public OdiProjectPaths newOdiProjectPaths(Map<String, Set<String>> includePaths) {
		return this.odiFactory.newOdiProjectPaths(includePaths);
	}


	@Override
	public IOdiFullMappingPath newOdiMappingFullPath(String projectCode,
			String folderName, String mappingName) {
		return this.odiFactory.newOdiMappingFullPath(projectCode,folderName,mappingName);
	}


	@Override
	public IOdiBasicPersistenceService newOdiBasicPersistenceService() {
		return this.odiFactory.newOdiBasicPersistenceService();
	}


	@Override
	public Sql createSqlConnection(String physicalTopologyServer) {
		return this.odiFactory.createSqlConnection(physicalTopologyServer)
	}


	@Override
	public void cleanup() {
		this.odiFactory.cleanup();
		
	}
	
	

}
