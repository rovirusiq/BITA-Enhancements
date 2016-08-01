package ro.bcr.bita.odi.template

import ro.bcr.bita.model.BitaModelFactory
import ro.bcr.bita.model.IBitaModelFactory
import ro.bcr.bita.odi.proxy.IOdiBasicPersistenceService;
import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.proxy.OdiEntityFactory

import oracle.odi.core.persistence.transaction.ITransactionDefinition
import oracle.odi.core.persistence.transaction.ITransactionManager
import oracle.odi.core.persistence.transaction.ITransactionStatus


class OdiBasicTemplate implements IOdiBasicTemplate{
	
	private IOdiEntityFactory odiEntityFactory;
	
	protected OdiBasicTemplate(IOdiEntityFactory odiEntityFactory) throws OdiTemplateException{
		if (odiEntityFactory==null) throw new OdiTemplateException("The constructor argument odiEntityfactory for the OdiBasicTemplate cannot be null");
		this.odiEntityFactory=odiEntityFactory;
	}

	private void executeCommand(IOdiBasicCommand cmd,Boolean inTransaction) throws OdiTemplateException{
		ITransactionDefinition txnDef;
		ITransactionManager tm;
		ITransactionStatus txnStatus;
		try {
			/*
			 * If you do not start a transaction you cannot manage the persistence context
			 * This creates memory consumption if you do very long operations
			 * That is way everything has a transaction, just that the one 
			 * that are supposed NOT to execute in transaction get a clear() before 
			 */
			IOdiCommandContext ctx=this.odiEntityFactory.newOdiTemplateCommandContext();
			txnStatus = this.odiEntityFactory.createTransaction();
			ctx.setTransactionStatus(txnStatus);
			ctx.setInTransaction(inTransaction);
			
			cmd.execute(ctx);
			
			if (ctx!=null) {
			
				if (!inTransaction){
					ctx.clearPersistenceContext();
				}
				ctx.commitTransaction(txnStatus);
			}
		} catch (Exception ex) {
			if (ex.class==OdiTemplateException) throw ex;//not to wrap also an OdiTemplateException
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
