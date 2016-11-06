package ro.bcr.bita.model;


public interface IBitaModelFactory {


	public abstract IDependency<String,String> newMappingDependency(String who, String on);

	public abstract IMappingDependencyRepositoryCyclicAware<String,String> newMappingDeppendencyRepository();

	public abstract IMessageCollection newMessageCollection(String idCollection);
	
	
	
}