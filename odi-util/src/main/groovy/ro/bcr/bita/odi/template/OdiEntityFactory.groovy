package ro.bcr.bita.odi.template

import oracle.odi.core.OdiInstance
import oracle.odi.core.config.PoolingAttributes
import oracle.odi.core.persistence.transaction.ITransactionDefinition;
import oracle.odi.core.persistence.transaction.ITransactionManager
import oracle.odi.core.persistence.transaction.ITransactionStatus
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition

class OdiEntityFactory implements IOdiEntityFactory{
	
	private OdiInstance odiInstance;
	
	/********************************************************************************************
	 *
	 *Constructor
	 *
	 ********************************************************************************************/
	protected OdiEntityFactory(OdiInstance odiInstance) {
		this.odiInstance=odiInstance;
	}
	/********************************************************************************************
	 *
	 *IOdiEntityFactory
	 *
	 ********************************************************************************************/
	@Override
	public ITransactionDefinition createDefaultTransactionDefinition() {
		return new DefaultTransactionDefinition();
	}
	
	@Override
	public PoolingAttributes createDefaltPoolingAttributes() {
		return new PoolingAttributes();
	}
	
	@Override
	public ITransactionManager getTransactionManager() {
		return this.odiInstance.getTransactionManager();
	}
	
	@Override
	public ITransactionStatus createTransactionStatus(ITransactionManager tm, ITransactionDefinition defn) {
		return tm.getTransaction(defn);
	}
	/********************************************************************************************
	 *
	 *Own Instance methods
	 *
	 ********************************************************************************************/
	public OdiInstance getOdiInstance() {
		return this.odiInstance;
	}

}
