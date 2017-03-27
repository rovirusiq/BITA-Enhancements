package ro.bcr.bita.model;


public interface IBitaModelFactory {


	public IDependency<String,String> newMappingDependency(String who, String on);

	public IMappingDependencyRepositoryCyclicAware<String,String> newMappingDeppendencyRepository();

	public IMessageCollection newMessageCollection(String idCollection);
	
	
	
}