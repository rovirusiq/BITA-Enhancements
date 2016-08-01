package ro.bcr.bita.model;

import oracle.odi.domain.mapping.Mapping;

public interface IBitaModelFactory {

	public abstract IOdiMapping newOdiMapping(Mapping odiObject) throws BitaModelException;

	public abstract IDependency<String,String> newMappingDependency(String who, String on);

	public abstract IMappingDependencyRepositoryCyclicAware<String,String> newMappingDeppendencyRepository();

	public abstract IMessageCollection newMessageCollection(String idCollection);
	
}