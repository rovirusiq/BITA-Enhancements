package ro.bcr.bita.odi.template;

import oracle.odi.core.OdiInstance;
import oracle.odi.core.config.PoolingAttributes;
import oracle.odi.core.persistence.transaction.ITransactionDefinition;
import oracle.odi.core.persistence.transaction.ITransactionManager;
import oracle.odi.core.persistence.transaction.ITransactionStatus;

public interface IOdiEntityFactory {
	
	public abstract ITransactionDefinition createDefaultTransactionDefinition();

	public abstract PoolingAttributes createDefaltPoolingAttributes();

	public abstract ITransactionStatus createTransactionStatus(ITransactionManager tm, ITransactionDefinition defn);

	public abstract ITransactionManager getTransactionManager(); 

}
