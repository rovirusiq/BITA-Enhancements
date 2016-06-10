package ro.bcr.bita.proxy.odi;

import oracle.odi.core.persistence.transaction.ITransactionDefinition;
import oracle.odi.core.persistence.transaction.ITransactionManager;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.domain.mapping.finder.IMappingFinder;
import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFolderFinder;

public interface IOdiEntityFactory {
	
	public abstract ITransactionDefinition createDefaultTransactionDefinition() throws BitaOdiException;

	public abstract ITransactionStatus createTransactionStatus(ITransactionManager tm, ITransactionDefinition defn)  throws BitaOdiException;

	public abstract ITransactionManager getTransactionManager()  throws BitaOdiException;

	public abstract IOdiScenarioFolderFinder createScenarioFolderFinder()  throws BitaOdiException;

	public abstract IOdiScenarioFinder createScenarioFinder()  throws BitaOdiException;

	public abstract IOdiProjectFinder createProjectFinder()  throws BitaOdiException;
	
	public abstract IOdiFolderFinder createProjectFolderFinder()  throws BitaOdiException;

	public abstract IMappingFinder createMappingFinder()  throws BitaOdiException;
	
	public abstract void cleanuUp()  throws BitaOdiException;

	

}
