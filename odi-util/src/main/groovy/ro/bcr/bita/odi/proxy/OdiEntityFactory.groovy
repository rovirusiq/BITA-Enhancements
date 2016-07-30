package ro.bcr.bita.odi.proxy

import ro.bcr.bita.model.JdbcDriverList

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

class OdiEntityFactory implements IOdiEntityFactory{
	/********************************************************************************************
	 *
	 *Internal Bean for keeping connexion details
	 *
	 ********************************************************************************************/
	
	public class OdiInstanceProperties{
		
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
		
		/**
		 * @param jdbcDriverClassName the jdbcDriverClassName to set
		 */
		public void setJdbcDriverClassName(String jdbcDriverClassName) {
			this.jdbcDriverClassName = jdbcDriverClassName;
		}
		
		public void setJdbcDriverClassName(JdbcDriverList driver) {
			this.jdbcDriverClassName = driver.value();
		}
		/**
		 * @param jdbcUrl the jdbcUrl to set
		 */
		public void setJdbcUrl(String jdbcUrl) {
			this.jdbcUrl = jdbcUrl;
		}
		
	}
	
	private OdiInstance odiInstance;
	
	/********************************************************************************************
	 *
	 *Constructor
	 *
	 ********************************************************************************************/
	private OdiEntityFactory(OdiInstance odiInstance) {
		this.odiInstance=odiInstance;
	}
	/********************************************************************************************
	 *
	 *IOdiEntityFactory
	 *
	 ********************************************************************************************/

	@Override
	public ITransactionDefinition createDefaultTransactionDefinition() throws BitaOdiException{
		return new DefaultTransactionDefinition();
	}

	@Override
	public ITransactionManager getTransactionManager() throws BitaOdiException{
		return this.odiInstance.getTransactionManager();
	}
	
	@Override
	public ITransactionStatus createTransactionStatus(ITransactionManager tm, ITransactionDefinition defn) throws BitaOdiException{
		return tm.getTransaction(defn);
	}
	
	@Override
	public IMappingFinder createMappingFinder() throws BitaOdiException{
		(IMappingFinder)this.odiInstance.getTransactionalEntityManager().getFinder(Mapping.class);
	}
	@Override
	public IOdiProjectFinder createProjectFinder() throws BitaOdiException{
		(IOdiProjectFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiProject.class);
	}
	@Override
	public IOdiScenarioFinder createScenarioFinder() throws BitaOdiException{
		(IOdiScenarioFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiScenario.class);
	}
	@Override
	public IOdiScenarioFolderFinder createScenarioFolderFinder() throws BitaOdiException {
		(IOdiScenarioFolderFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiScenarioFolder.class);
	}
	
	@Override
	public IOdiFolderFinder createProjectFolderFinder() throws BitaOdiException {
		(IOdiFolderFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiFolder.class);
	}
	
	@Override
	public IOdiContextFinder createContextFinder() throws BitaOdiException {
		(IOdiContextFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiContext.class);
	}
	@Override
	public IOdiLogicalSchemaFinder createLogicalSchemaFinder() throws BitaOdiException {
		(IOdiLogicalSchemaFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiLogicalSchema.class);
	}
	@Override
	public IOdiPhysicalSchemaFinder createPhysicalSchemaFinder() throws BitaOdiException {
		(IOdiPhysicalSchemaFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiPhysicalSchema.class);
	}
	
	@Override
	public IOdiDataServerFinder createDataServerFinder() throws BitaOdiException {
		(IOdiDataServerFinder)this.odiInstance.getTransactionalEntityManager().getFinder(OdiDataServer.class);
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
		if (odiInstance==null) throw new BitaOdiException("The parameter odiInstance for the factory method of OdiEntityFactory[OdiFactoryEntity.createInstance(odiInstance)] cannot be null.");
		return new OdiEntityFactory(odiInstance);
	}
	public static IOdiEntityFactory createInstance(OdiInstanceProperties odiInstanceProperties) throws BitaOdiException{
		
		if (odiInstanceProperties==null) throw new BitaOdiException("The parameter odiInstanceProperties for the factory method of OdiEntityFactory[OdiFactoryEntity.createInstance(odiInstanceProperties)] cannot be null.");
		
		if (!checkParameter(odiInstanceProperties.jdbcUrl)) throwCheckException("jdbcUrl",odiInstanceProperties.jdbcUrl);
		if (!checkParameter(odiInstanceProperties.jdbcDriverClassName)) throwCheckException("jdbcDriverClassName",odiInstanceProperties.jdbcDriverClassName);
		if (!checkParameter(odiInstanceProperties.masterRepositoryUsername)) throwCheckException("masterRepositoryUsername",odiInstanceProperties.masterRepositoryUsername);
		if (!checkParameter(odiInstanceProperties.masterRepositoryPassword)) throwCheckException("masterRepositoryPassword",odiInstanceProperties.masterRepositoryPassword);
		if (!checkParameter(odiInstanceProperties.workRepositoryName)) throwCheckException("workRepositoryName",odiInstanceProperties.workRepositoryName);
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
		return new OdiEntityFactory(inst);
		
	}
	@Override
	public void cleanuUp() throws BitaOdiException {
		if (this.odiInstance) {
			try {
				this.odiInstance.close();
			} catch (Exception ex) {
			//ingnore
			}
		}
		
	}
}
