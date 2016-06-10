package ro.bcr.bita.model;

import java.util.Set;

public interface IMappingDependencyRepositoryCyclicAware extends IMappingDependencyRepository {

	public Set<IDependency<String, String>> getMappingDependenciesAndCheckCyclicDependencies() throws BitaModelException;
	
}
