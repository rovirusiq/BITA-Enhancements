package ro.bcr.bita.model;

import java.util.Set;

public interface IMappingRepository extends IMappingDependencyRepositoryCyclicAware<String, String> {
	
	public Set<String> getAllMappingNames();
//	public Set<String> getDependenciesFor(String mapping);
//	public Set<String> getDependenciesReffering(String mapping);
//	public Set<String> getDependenciesReffering(String mapping,DependencyRole role);
	

}
