package ro.bcr.bita.odi.template;

import ro.bcr.bita.odi.proxy.IOdiBasicPersistenceService;
import ro.bcr.bita.odi.proxy.IOdiOperationsService;

import oracle.odi.core.persistence.transaction.ITransactionStatus;


public interface IOdiCommandContext extends IOdiBasicPersistenceService,IOdiOperationsService{
	
	public abstract void setTransactionStatus(ITransactionStatus st);
	/**
	 * @return Returns the ODI Transaction status asociated with the Thread. Can return null. Be sure to check against null.
	 */
	public abstract ITransactionStatus getTransactionStatus();
	
	public abstract Boolean inTransaction();
	
	public abstract void setInTransaction(Boolean inTransaction);
	
}