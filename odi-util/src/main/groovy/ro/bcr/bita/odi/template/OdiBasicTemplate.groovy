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
import ro.bcr.bita.proxy.odi.IOdiEntityFactory;
import ro.bcr.bita.proxy.odi.OdiEntityFactory;

import java.util.List;
import java.util.Map;

import bsh.This;


class OdiBasicTemplate implements IOdiBasicTemplate{
	
	private IOdiEntityFactory odiEntityFactory;
	
	public OdiBasicTemplate(IOdiEntityFactory odiEntityFactory) throws OdiTemplateException{
		if (odiEntityFactory==null) throw new OdiTemplateException("The constructor argument odiEntityfactory for the OdiBasicTemplate cannot be null");
		this.odiEntityFactory=odiEntityFactory;		
	}
	
	private IOdiCommandContext createNewContext() {
		return new OdiCommandContext(this.odiEntityFactory);
	}

	private void executeCommand(IOdiBasicCommand cmd,Boolean inTransaction) throws OdiTemplateException{
		ITransactionDefinition txnDef;
		ITransactionManager tm;
		ITransactionStatus txnStatus;
		try {
			if (inTransaction) {
				txnDef = this.odiEntityFactory.createDefaultTransactionDefinition();
				tm =  this.odiEntityFactory.getTransactionManager();
				txnStatus = this.odiEntityFactory.createTransactionStatus(tm,txnDef);
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

}
