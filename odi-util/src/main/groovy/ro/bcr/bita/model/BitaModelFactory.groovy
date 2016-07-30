package ro.bcr.bita.model;

import ro.bcr.bita.odi.proxy.IOdiEntityFactory
import ro.bcr.bita.odi.proxy.OdiPathUtil
import ro.bcr.bita.odi.proxy.OdiProjectPaths
import ro.bcr.bita.odi.template.IOdiCommandContext
import ro.bcr.bita.odi.template.OdiCommandContext
import ro.bcr.bita.service.IMappingAnalyzeProcessor
import ro.bcr.bita.service.mapping.JcAnalyzeProcessor

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import oracle.odi.domain.mapping.Mapping

//TODO optimize not to return new objects when it si possible to do that
//TODO to see what must stay in the interface and what it is exposed in the test interface
@CompileStatic
public class BitaModelFactory implements IBitaModelFactory {
	
	
	/* (non-Javadoc)
	 * @see ro.bcr.bita.model.IBitaModelFactory#createOdiMapping(oracle.odi.domain.mapping.Mapping)
	 */
	@Override
	public IOdiMapping newOdiMapping(Mapping odiObject) throws BitaModelException{
		return new OdiMapping(odiObject);
	}
	
	
	@Override
	public  IDependency<String,String> newMappingDependency(String who,String on){
		return new MappingDependency(who,on);
	}
	
	@Override
	@TypeChecked
	public  IOdiCommandContext newOdiTemplateCommandContext(IOdiEntityFactory odiEntityFactory){
		return new OdiCommandContext(odiEntityFactory);
	}
	
	@Override
	public OdiPathUtil newOdiPathUtil(IOdiEntityFactory odiEntityFactory) {
		return new OdiPathUtil(odiEntityFactory);
	}
	
	@Override
	public OdiProjectPaths newOdiProjectPaths(Map<String,Set<String>> includePaths) {
		return new OdiProjectPaths(includePaths);
	}
	
	@Override
	public IMappingDependencyRepositoryCyclicAware newMappingDeppendencyRepository() {
		return new MappingDependencyRepository();
	}
	
	@Override
	public IMappingAnalyzeProcessor newDependencyAnalyzeProcessor() {
		return new JcAnalyzeProcessor(this.newMappingDeppendencyRepository());
	}
	
	@Override
	public IMessageCollection newMessageCollection(String idCollection) {
		return new MessageCollection(idCollection);
	}

}
