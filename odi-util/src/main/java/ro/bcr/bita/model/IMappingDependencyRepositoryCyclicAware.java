package ro.bcr.bita.model;

import java.util.Set;

public interface IMappingDependencyRepositoryCyclicAware<H,O> extends IMappingDependencyRepository<H,O> {

	public Set<IDependency<H, O>> getMappingDependenciesAndCheckCyclicDependencies() throws BitaModelException;
	
}
