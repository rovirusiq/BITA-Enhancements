package ro.bcr.bita.model;

import ro.bcr.bita.model.IDependency.DependencyRole;

import java.util.Set;

//TODO - the implementation of this
public interface IMappingDependencyRepositoryExtended {
	
	

	public Set<String> getAllMappings();
	public Set<String> getDependenciesFor(String mapping);
	public Set<String> getDependenciesReffering(String mapping);
	public Set<String> getDependenciesReffering(String mapping,DependencyRole role);
	
	
}
