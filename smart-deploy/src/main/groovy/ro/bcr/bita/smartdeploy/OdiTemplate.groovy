package ro.bcr.bita.smartdeploy

import groovy.lang.Closure;
import oracle.odi.core.OdiInstance
import oracle.odi.core.config.MasterRepositoryDbInfo
import oracle.odi.core.config.OdiInstanceConfig
import oracle.odi.core.config.PoolingAttributes
import oracle.odi.core.config.WorkRepositoryDbInfo
import oracle.odi.core.persistence.transaction.ITransactionManager
import oracle.odi.core.persistence.transaction.ITransactionStatus
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition
import oracle.odi.core.security.Authentication

class OdiTemplate implements IOdiTemplate{
	
	
	public static class Builder {
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
		public Builder setWorkRepositoryName(String workRepositoryName) {
			this.workRepositoryName = workRepositoryName;
			return this;
		}
		/**
		 * @param masterRepositoryUsername the masterRepositoryUsername to set
		 */
		public Builder setMasterRepositoryUsername(String masterRepositoryUsername) {
			this.masterRepositoryUsername = masterRepositoryUsername;
			return this;
		}
		/**
		 * @param masterRepositoryPassword the masterRepositoryPassword to set
		 */
		public Builder setMasterRepositoryPassword(String masterRepositoryPassword) {
			this.masterRepositoryPassword = masterRepositoryPassword;
			return this;
		}
		/**
		 * @param odiUsername the odiUsername to set
		 */
		public Builder setOdiUsername(String odiUsername) {
			this.odiUsername = odiUsername;
			return this;
		}
		/**
		 * @param odiPassword the odiPassword to set
		 */
		public Builder setOdiPassword(String odiPassword) {
			this.odiPassword = odiPassword;
			return this;
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
		
		
		public OdiTemplate build() throws RuntimeException{
				
			MasterRepositoryDbInfo masterInfo = new MasterRepositoryDbInfo(jdbcUrl, jdbcDriverClassName, masterRepositoryUsername, masterRepositoryPassword.toCharArray(), new PoolingAttributes());
			WorkRepositoryDbInfo workInfo = null;
			if (workRepositoryName != null){
			  workInfo = new WorkRepositoryDbInfo(workRepositoryName, new PoolingAttributes());
			}
			OdiInstance inst = OdiInstance.createInstance(new OdiInstanceConfig(masterInfo, workInfo));
			try {
					Authentication auth = inst.getSecurityManager().createAuthentication(odiUsername, odiPassword.toCharArray());
					inst.getSecurityManager().setCurrentThreadAuthentication(auth);
			} catch (RuntimeException e) {
					inst.close();
					throw e;
			}
			return new OdiTemplate(inst);
			
		}
	}
	
	
	
	private OdiInstance odiInstance;
	
	
	private OdiTemplate(OdiInstance odiInst) {
		odiInstance=odiInst;
	}
	
	
	private void executeCommand(IOdiCommand cmd,Boolean inTransaction) throws RuntimeException{
		DefaultTransactionDefinition txnDef;
		ITransactionManager tm;
		ITransactionStatus txnStatus;
		try {
			if (inTransaction) {
				txnDef = new DefaultTransactionDefinition();
				tm = odiInstance.getTransactionManager();
				txnStatus = tm.getTransaction(txnDef);
			}
			cmd.execute(odiInstance);
			if (inTransaction) {
				tm.commit(txnStatus)
			}
		} catch (Exception ex) {
			throw new OdiTemplateException("An exception has occured why executing the ODI command",ex);
		} finally {
		}
	}
	

	@Override
	public void executeInTransaction(IOdiCommand cmd) {
		this.executeCommand(cmd,true);
	}
	
	@Override
	public void executeWithoutTransaction(IOdiCommand cmd) {
		this.executeCommand(cmd,false);
	}


	@Override
	public void cleanUp() throws OdiTemplateException {
		try {
			odiInstance?.close();
		} catch (Exception ex) {
			throw new OdiTemplateException("An exception occured when trying to close the OdiInstance");
		}
		
	}
	
	public void finalize() throws Throwable{
		try {
			this.cleanUp();
		} finally {
			super.finalize();
		}
	}
	
	

}
