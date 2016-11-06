package ro.bcr.bita.odi.proxy

import ro.bcr.bita.model.IBitaModelFactory;
import ro.bcr.bita.odi.template.IOdiCommandContext;
import ro.bcr.bita.odi.template.OdiBasicTemplate;
import ro.bcr.bita.odi.template.OdiCommandContext;

import bsh.This;
import groovy.sql.Sql;
import groovy.transform.TypeChecked;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import oracle.odi.core.OdiInstance
import oracle.odi.core.config.MasterRepositoryDbInfo
import oracle.odi.core.config.OdiInstanceConfig
import oracle.odi.core.config.PoolingAttributes
import oracle.odi.core.config.WorkRepositoryDbInfo
import oracle.odi.core.persistence.transaction.ITransactionDefinition
import oracle.odi.core.persistence.transaction.ITransactionManager
import oracle.odi.core.persistence.transaction.ITransactionStatus
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition
import oracle.odi.core.security.Authentication
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.project.OdiFolder
import oracle.odi.domain.project.OdiProject
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.domain.runtime.scenario.OdiScenario
import oracle.odi.domain.runtime.scenario.OdiScenarioFolder
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFolderFinder
import oracle.odi.domain.topology.OdiContext;
import oracle.odi.domain.topology.OdiDataServer;
import oracle.odi.domain.topology.OdiLogicalSchema;
import oracle.odi.domain.topology.OdiPhysicalSchema;
import oracle.odi.domain.topology.finder.IOdiContextFinder;
import oracle.odi.domain.topology.finder.IOdiDataServerFinder;
import oracle.odi.domain.topology.finder.IOdiLogicalSchemaFinder;
import oracle.odi.domain.topology.finder.IOdiPhysicalSchemaFinder;
import oracle.odi.runtime.agent.invocation.RemoteRuntimeAgentInvoker;
import oracle.odi.runtime.agent.invocation.StartupParams;

//TODO treat errors
class OdiEntityFactory implements IOdiEntityFactory{
	/********************************************************************************************
	 *
	 *Internal Bean for keeping connexion details
	 *
	 ********************************************************************************************/
	
	public static class OdiInstanceProperties{
		
		private String workRepositoryName;
		private String masterRepositoryUsername;
		private String masterRepositoryPassword;
		private String odiUsername;
		private String odiPassword;
		private String jdbcDriverClassName;
		private String jdbcUrl;
		
		/**
		 * @param workRepositoryName the workRepositoryName to set
		 */
		public setWorkRepositoryName(String workRepositoryName) {
			this.workRepositoryName = workRepositoryName;
			
		}
		/**
		 * @param masterRepositoryUsername the masterRepositoryUsername to set
		 */
		public setMasterRepositoryUsername(String masterRepositoryUsername) {
			this.masterRepositoryUsername = masterRepositoryUsername;
		}
		/**
		 * @param masterRepositoryPassword the masterRepositoryPassword to set
		 */
		public setMasterRepositoryPassword(String masterRepositoryPassword) {
			this.masterRepositoryPassword = masterRepositoryPassword;
		}
		
		public setMasterRepositoryPassword(char[] masterRepositoryPassword) {
			this.masterRepositoryPassword = String.valueOf(masterRepositoryPassword);
		}
		
		/**
		 * @param odiUsername the odiUsername to set
		 */
		public setOdiUsername(String odiUsername) {
			this.odiUsername = odiUsername;
		}
		/**
		 * @param odiPassword the odiPassword to set
		 */
		public setOdiPassword(String odiPassword) {
			this.odiPassword = odiPassword;
		}
		
		public setOdiPassword(char[] odiPassword) {
			this.odiPassword = String.valueOf(odiPassword);
		}
		
		/**
		 * @param jdbcDriverClassName the jdbcDriverClassName to set
		 */
		public void setJdbcDriverClassName(String jdbcDriverClassName) {
			this.jdbcDriverClassName = jdbcDriverClassName;
		}

		/**
		 * @param jdbcUrl the jdbcUrl to set
		 */
		public void setJdbcUrl(String jdbcUrl) {
			this.jdbcUrl = jdbcUrl;
		}
		
	}
	
	private OdiInstance odiInstance;
	private IBitaModelFactory bitaModelFactory;
	private IOdiOperationsService odiOpSrv;
	private IOdiBasicPersistenceService odiPersistSrv;
	
	/********************************************************************************************
	 *
	 *Constructor
	 *
	 ********************************************************************************************/
	private OdiEntityFactory(OdiInstance odiInstance) {
		
		if (odiInstance==null) throw new BitaOdiException("The parameter odiInstance for the factory method of OdiEntityFactory[OdiFactoryEntity.createInstance(odiInstance,bitaModelFactory)] cannot be null.");		
		this.odiInstance=odiInstance;
		this.odiPersistSrv=new OdiBasicPersistenceService(odiInstance);
	}
	/********************************************************************************************
	 *
	 *IOdiEntityFactory
	 *
	 ********************************************************************************************/	
	@Override
	public IMappingFinder createMappingFinder() throws BitaOdiException{
		try {
			return (IMappingFinder)this.odiInstance.getTransactionalEntityManager().getFinder(Mapping.class);
		} catch (Exception ex) {
			giftWrap(ex);
		}
	}
	@Override
	public IOdiProjectFinder createProjectFinder() throws BitaOdiException{
		try {
			return (IOdiProjectFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiProject.class);
		} catch (Exception ex) {
			giftWrap(ex);
		}
	}
	@Override
	public IOdiScenarioFinder createScenarioFinder() throws BitaOdiException{
		try {
			return (IOdiScenarioFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiScenario.class);
		} catch (Exception ex) {
			giftWrap(ex);
		}
	}
	@Override
	public IOdiScenarioFolderFinder createScenarioFolderFinder() throws BitaOdiException {
		try {
			return (IOdiScenarioFolderFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiScenarioFolder.class);
		} catch (Exception ex) {
			giftWrap(ex);
		}
	}
	
	@Override
	public IOdiFolderFinder createProjectFolderFinder() throws BitaOdiException {
		try {
			return (IOdiFolderFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiFolder.class);
		} catch (Exception ex) {
			giftWrap(ex);
		}
	}
	
	@Override
	public IOdiContextFinder createContextFinder() throws BitaOdiException {
		try {
			return (IOdiContextFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiContext.class);
		} catch (Exception ex) {
			giftWrap(ex);
		}
	}
	@Override
	public IOdiLogicalSchemaFinder createLogicalSchemaFinder() throws BitaOdiException {
		try {
			return (IOdiLogicalSchemaFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiLogicalSchema.class);
		} catch (Exception ex) {
			giftWrap(ex);
		}
	}
	@Override
	public IOdiPhysicalSchemaFinder createPhysicalSchemaFinder() throws BitaOdiException {
		try {
			return (IOdiPhysicalSchemaFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiPhysicalSchema.class);
		} catch (Exception ex) {
			giftWrap(ex);
		}
	}
	
	@Override
	public IOdiDataServerFinder createDataServerFinder() throws BitaOdiException {
		try {
			return (IOdiDataServerFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiDataServer.class);
		} catch (Exception ex) {
			giftWrap(ex);
		}
	}
	@Override
	public ITransactionStatus createTransaction() throws BitaOdiException {
		try {
			return this.odiInstance.getTransactionManager().getTransaction(new DefaultTransactionDefinition());
		} catch (Exception ex) {
			giftWrap(ex);
		}
	}
	/********************************************************************************************
	 *Custom Odi Utils related
	 ********************************************************************************************/
	@Override
	@TypeChecked
	public OdiPathUtil newOdiPathUtil() {
		return new OdiPathUtil(this);
	}
	
	@Override
	@TypeChecked
	public OdiProjectPaths newOdiProjectPaths(Map<String,Set<String>> includePaths) {
		return new OdiProjectPaths(includePaths);
	}
	
	@Override
	@TypeChecked
	public IOdiFullMappingPath newOdiMappingFullPath(String projectCode,String folderName,String mappingName) {
		return new OdiFullMappingPath(projectCode, folderName, mappingName);
	}
	
	
	@Override
	@TypeChecked
	public IOdiBasicPersistenceService newOdiBasicPersistenceService() {
		return this.odiPersistSrv;
	}
	
	@Override
	@TypeChecked
	public void cleanup() {
		try {
			this.odiInstance.close();	
		} catch (Exception ex) {
			//silently fail. If it runs in odi log in odilog file
			org.apache.commons.logging.LogFactory.getLog("oracle.odi").error("Class OdiEntityFactory method cleanup encountered and exception when trying to close the OdiInstance",ex);
		}
	}
	/********************************************************************************************
	 *
	 *Own Instance methods
	 *
	 ********************************************************************************************/
	public OdiInstance getOdiInstance() {
		return this.odiInstance;
	}
	/********************************************************************************************
	 *
	 *Static utility methods
	 *
	 ********************************************************************************************/
	private static final giftWrap(Exception ex) throws BitaOdiException{
		throw new BitaOdiException("Unexpected exception in persistence service:"+ex);
	}
	
	/********************************************************************************************
	 * 
	 *Factory methods
	 *
	 ********************************************************************************************/
	private static void throwCheckException(String property,String parameter) throws BitaOdiException {
		GString msg="Because an OdiInstance was not provided the instantiation is done using properties.Property[$property] has an invalid value[$parameter]";
		throw new BitaOdiException(msg);
	}
	
	private static boolean checkParameter(String smth) {
		return ((smth!=null) && (!"".equals(smth)));
	}
	
	public static IOdiEntityFactory createInstance(OdiInstance odiInstance) throws BitaOdiException{
		return new OdiEntityFactory(odiInstance);
	}
	
	public static OdiInstance createOdiInstanceFromProperties(OdiInstanceProperties odiInstanceProperties) throws BitaOdiException{
		
		if (odiInstanceProperties==null) throw new BitaOdiException("The parameter odiInstanceProperties for the factory method of OdiEntityFactory[OdiFactoryEntity.createInstance(odiInstanceProperties)] cannot be null.");
		
		if (!checkParameter(odiInstanceProperties.jdbcUrl)) throwCheckException("jdbcUrl",odiInstanceProperties.jdbcUrl);
		if (!checkParameter(odiInstanceProperties.jdbcDriverClassName)) throwCheckException("jdbcDriverClassName",odiInstanceProperties.jdbcDriverClassName);
		if (!checkParameter(odiInstanceProperties.masterRepositoryUsername)) throwCheckException("masterRepositoryUsername",odiInstanceProperties.masterRepositoryUsername);
		if (!checkParameter(odiInstanceProperties.masterRepositoryPassword)) throwCheckException("masterRepositoryPassword",odiInstanceProperties.masterRepositoryPassword);
		if (!checkParameter(odiInstanceProperties.workRepositoryName)) throwCheckException("workRepositoryName",odiInstanceProperties.workRepositoryName);
		if (!checkParameter(odiInstanceProperties.odiUsername)) throwCheckException("odiUsername",odiInstanceProperties.odiUsername);
		if (!checkParameter(odiInstanceProperties.odiPassword)) throwCheckException("odiUsername",odiInstanceProperties.odiPassword);
		
		
		MasterRepositoryDbInfo masterInfo = new MasterRepositoryDbInfo(odiInstanceProperties.jdbcUrl, odiInstanceProperties.jdbcDriverClassName,odiInstanceProperties.masterRepositoryUsername, odiInstanceProperties.masterRepositoryPassword.toCharArray(), new PoolingAttributes());
		WorkRepositoryDbInfo workInfo = null;
		if (odiInstanceProperties.workRepositoryName != null){
		  workInfo = new WorkRepositoryDbInfo(odiInstanceProperties.workRepositoryName, new PoolingAttributes());
		}
		OdiInstance inst = OdiInstance.createInstance(new OdiInstanceConfig(masterInfo, workInfo));
		try {
				Authentication auth = inst.getSecurityManager().createAuthentication(odiInstanceProperties.odiUsername, odiInstanceProperties.odiPassword.toCharArray());
				inst.getSecurityManager().setCurrentThreadAuthentication(auth);
		} catch (RuntimeException e) {
				inst.close();
				throw new BitaOdiException("Cannot create OdiInstance.",e);
		}
		return inst;
	}
	
	public static IOdiEntityFactory createInstanceFromProperties(OdiInstanceProperties odiInstanceProperties) throws BitaOdiException{
		return new OdiEntityFactory(OdiEntityFactory.createOdiInstanceFromProperties(odiInstanceProperties));
	}	
}

