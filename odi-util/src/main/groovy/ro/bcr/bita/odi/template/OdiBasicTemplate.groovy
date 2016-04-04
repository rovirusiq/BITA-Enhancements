package ro.bcr.bita.odi.template

import ro.bcr.bita.model.JdbcDriverList;
import groovy.lang.Closure;
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
import oracle.odi.domain.mapping.Mapping;
import oracle.odi.domain.mapping.finder.IMappingFinder;
import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.runtime.scenario.OdiScenario;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder;

import ro.bcr.bita.model.JdbcDriverList
import ro.bcr.bita.odi.template.IOdiBasicCommand;
import ro.bcr.bita.odi.template.IOdiBasicTemplate;
import ro.bcr.bita.odi.template.IOdiCommandContext;
import ro.bcr.bita.odi.template.OdiBasicTemplate;
import ro.bcr.bita.odi.template.OdiTemplateException;

import java.util.List;
import java.util.Map;

import bsh.This;


class OdiBasicTemplate implements IOdiBasicTemplate{
	
	public static class Builder {
		private String workRepositoryName;
		private String masterRepositoryUsername;
		private String masterRepositoryPassword;
		private String odiUsername;
		private String odiPassword;
		private String jdbcDriverClassName;
		private String jdbcUrl;
		private OdiInstance odiInst;
		
		
		
		/**********
		 * Odi BASIC Template BUILDER
		 **********/
		public Builder(OdiInstance odiInstance) {
			this.setOdiInstance(odiInstance);
		}
		
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
		
		public void setJdbcDriverClassName(JdbcDriverList driver) {
			this.jdbcDriverClassName = driver.value();
		}
		/**
		 * @param jdbcUrl the jdbcUrl to set
		 */
		public void setJdbcUrl(String jdbcUrl) {
			this.jdbcUrl = jdbcUrl;
		}
		
		
		/**
		 * @param odiI an OdiInstance ca be passed directly if it is available. T
		 * This is a convenience method for using the OdiTemplate form Groovy scripts written directly in ODI IDE.
		 */
		public void setOdiInstance(OdiInstance odiI) {
			this.odiInst = odiI;
		}
		
		
		
		private Boolean checkParameter(Object parameter) throws OdiTemplateException{
			Boolean rsp=true;
			if ((null==parameter) || ("".equals(parameter.toString()))){
				rsp=false;
			}
			return rsp;
		}
		
		private void throwCheckException(String property,String parameter) throws OdiTemplateException {
			GString msg="Because an OdiInstance was not provided the instantiation is done using properties.Property[$property] has an invalid value[$parameter]";
			throw new OdiTemplateException(msg);
		}
		
		public IOdiBasicTemplate build() throws RuntimeException{
			
			OdiBasicTemplate instance=null;
			
			if (this.odiInst==null) {
								
				if (!checkParameter(this.jdbcUrl)) throwCheckException("jdbcUrl",this.jdbcUrl);
				if (!checkParameter(this.jdbcDriverClassName)) throwCheckException("jdbcDriverClassName",this.jdbcDriverClassName);
				if (!checkParameter(this.masterRepositoryUsername)) throwCheckException("masterRepositoryUsername",this.masterRepositoryUsername);
				if (!checkParameter(this.masterRepositoryPassword)) throwCheckException("masterRepositoryPassword",this.masterRepositoryPassword);
				if (!checkParameter(this.workRepositoryName)) throwCheckException("workRepositoryName",this.workRepositoryName);
				if (!checkParameter(this.workRepositoryName)) throwCheckException("workRepositoryName",this.workRepositoryName);
				if (!checkParameter(this.odiUsername)) throwCheckException("odiUsername",this.odiUsername);
				if (!checkParameter(this.odiPassword)) throwCheckException("odiUsername",this.odiPassword);
				
					
				MasterRepositoryDbInfo masterInfo = new MasterRepositoryDbInfo(jdbcUrl, jdbcDriverClassName, masterRepositoryUsername, masterRepositoryPassword.toCharArray(), this.odiEntityFactory.createDefaltPoolingAttributes());
				WorkRepositoryDbInfo workInfo = null;
				if (workRepositoryName != null){
				  workInfo = new WorkRepositoryDbInfo(workRepositoryName, this.odiEntityFactory.createDefaltPoolingAttributes());
				}
				OdiInstance inst = OdiInstance.createInstance(new OdiInstanceConfig(masterInfo, workInfo));
				try {
						Authentication auth = inst.getSecurityManager().createAuthentication(odiUsername, odiPassword.toCharArray());
						inst.getSecurityManager().setCurrentThreadAuthentication(auth);
				} catch (RuntimeException e) {
						inst.close();
						throw e;
				}
				this.odiInst=inst;
				
			}
			
			if (this.odiInst==null) throw new OdiTemplateException("Odi Template cannot be instantiated. The OdiInstance is null after the build logic. Something is wrong with the code.");
			
			instance=new OdiBasicTemplate(this.odiInst);
			
			return instance;
			
		}
	}
	/********************************************************************************************
	 *
	 *Odi BASIC Template
	 *
	 ********************************************************************************************/
	private OdiInstance odiInstance;
	private OdiCommandContext cmdContext;
	private IOdiEntityFactory odiEntityFactory;
	
	private OdiBasicTemplate(OdiInstance odiInst) {
		this.odiInstance=odiInst;
		this.odiEntityFactory=new OdiEntityFactory(this.odiInstance);		
	}
	
	private IOdiCommandContext createNewContext() {
		return new OdiCommandContext(this.odiEntityFactory);
	}

	private void executeCommand(IOdiBasicCommand cmd,Boolean inTransaction) throws RuntimeException{
		ITransactionDefinition txnDef;
		ITransactionManager tm;
		ITransactionStatus txnStatus;
		try {
			if (inTransaction) {
				txnDef = this.odiEntityFactory.createDefaultTransactionDefinition();
				tm =  this.odiEntityFactory.getTransactionManager();
				txnStatus = this.odiEntityFactory.createTransactionStatus(tm,txnDef);//tm.getTransaction(txnDef);
			}
			
			cmd.execute(createNewContext());
			if (inTransaction) {
				tm.commit(txnStatus)
			}
		} catch (Exception ex) {
			throw new OdiTemplateException("An exception has occured while executing the ODI command",ex);
		} finally {
		}
	}
	

	@Override
	public void executeInTransaction(IOdiBasicCommand cmd) {
		this.executeCommand(cmd,true);
	}
	
	@Override
	public void executeWithoutTransaction(IOdiBasicCommand cmd) {
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
