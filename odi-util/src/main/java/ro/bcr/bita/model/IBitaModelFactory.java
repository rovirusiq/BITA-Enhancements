package ro.bcr.bita.model;

import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.proxy.OdiPathUtil;
import ro.bcr.bita.odi.proxy.OdiProjectPaths;
import ro.bcr.bita.odi.template.IOdiCommandContext;
import ro.bcr.bita.service.IMappingAnalyzeProcessor;

import java.util.Map;
import java.util.Set;

import oracle.odi.domain.mapping.Mapping;

public interface IBitaModelFactory {

	public abstract IOdiMapping newOdiMapping(Mapping odiObject) throws BitaModelException;

	public abstract IDependency<String,String> newMappingDependency(String who, String on);

	public abstract IOdiCommandContext newOdiTemplateCommandContext(IOdiEntityFactory odiEntityFactory);

	public abstract OdiPathUtil newOdiPathUtil(IOdiEntityFactory odiEntityFactory);

	public abstract OdiProjectPaths newOdiProjectPaths(Map<String,Set<String>> includePaths);

	public abstract IMappingAnalyzeProcessor newDependencyAnalyzeProcessor();

	public abstract IMappingDependencyRepositoryCyclicAware newMappingDeppendencyRepository();

	public abstract IMessageCollection newMessageCollection(String idCollection);
	
}