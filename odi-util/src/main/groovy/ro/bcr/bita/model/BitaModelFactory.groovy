package ro.bcr.bita.model;

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

//TODO optimize not to return new objects when it si possible to do that
//TODO to see what must stay in the interface and what it is exposed in the test interface
@CompileStatic
public class BitaModelFactory implements IBitaModelFactory {
	
	
	protected BitaModelFactory() {
		
	}
	
	@Override
	public  IDependency<String,String> newMappingDependency(String who,String on){
		return new MappingDependency(who,on);
	}
	
	@Override
	public IMappingDependencyRepositoryCyclicAware<String,String> newMappingDeppendencyRepository() {
		return new MappingDependencyRepository();
	}
	
	
	@Override
	public IMessageCollection newMessageCollection(String idCollection) {
		return new MessageCollection(idCollection);
	}
	
	public static IBitaModelFactory newInstance() {
		return new BitaModelFactory();
	}

}
