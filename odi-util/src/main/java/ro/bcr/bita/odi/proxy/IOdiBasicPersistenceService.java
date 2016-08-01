package ro.bcr.bita.odi.proxy;

import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.domain.IOdiEntity;

public interface IOdiBasicPersistenceService {
	
	public abstract void clearPersistenceContext() throws BitaOdiException;
	public abstract void flushPersistenceContext();
	public abstract void removeFromPersistenceContext(IOdiEntity odiObject) throws BitaOdiException;
	public abstract void persistInRepository(IOdiEntity odiObject) throws BitaOdiException;
	public abstract void detachFromPersistenceContext(IOdiEntity odiObject) throws BitaOdiException;
	
	public abstract void commitTransaction(ITransactionStatus txnStatus) throws BitaOdiException;
	public abstract void rollbackTransaction(ITransactionStatus txnStatus) throws BitaOdiException;
	
}
