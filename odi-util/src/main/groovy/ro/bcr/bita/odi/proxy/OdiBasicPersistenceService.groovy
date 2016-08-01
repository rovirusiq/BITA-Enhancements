package ro.bcr.bita.odi.proxy

import oracle.odi.core.OdiInstance
import oracle.odi.core.persistence.IOdiEntityManager;
import oracle.odi.core.persistence.transaction.ITransactionManager;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.domain.IOdiEntity;

class OdiBasicPersistenceService implements IOdiBasicPersistenceService{
	
	private final OdiInstance odiInstance;
	private final ITransactionManager tm;
	
	public OdiBasicPersistenceService(OdiInstance odiInstance) {
		this.odiInstance=odiInstance;
		this.tm=this.odiInstance.getTransactionManager();
	}
	
	
	private static final giftWrap(Exception ex) throws BitaOdiException{
		throw new BitaOdiException("Unexpected exception in persistence service:"+ex);
	}
	

	@Override
	public void clearPersistenceContext() throws BitaOdiException {
		try {
			this.odiInstance.getTransactionalEntityManager().clear();
		} catch (Exception ex) {
			giftWrap(ex);
		}
		
	}

	@Override
	public void removeFromPersistenceContext(IOdiEntity odiObject)
			throws BitaOdiException {
		try {
			this.odiInstance.getTransactionalEntityManager().remove(odiObject);
		} catch (Exception ex) {
			giftWrap(ex);
		}
	}
			
	@Override
	public void detachFromPersistenceContext(IOdiEntity odiObject)
			throws BitaOdiException {
		try {
			this.odiInstance.getTransactionalEntityManager().detach(odiObject);
		} catch (Exception ex) {
			giftWrap(ex);
		}
		
	}

	@Override
	public void persistInRepository(IOdiEntity odiObject)
			throws BitaOdiException {
				
		try {
			this.odiInstance.getTransactionalEntityManager().persist(odiObject);
		} catch (Exception ex) {
			giftWrap(ex);
		}
		
	}

	@Override
	public void flushPersistenceContext() {
		try {
			this.odiInstance.getTransactionalEntityManager().flush();
		} catch (Exception ex) {
			giftWrap(ex);
		}
	}

	@Override
	public void commitTransaction(ITransactionStatus txnStatus)
			throws BitaOdiException {	
		try {
			tm.commit(txnStatus);
		} catch (Exception ex) {
			giftWrap(ex);
		}
		
	}

	@Override
	public void rollbackTransaction(ITransactionStatus txnStatus)
			throws BitaOdiException {
		try {
			tm.rollback(txnStatus);
		} catch (Exception ex) {
			giftWrap(ex);
		}
		
	}


	

}
