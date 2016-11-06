package ro.bcr.bita.model;

import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.proxy.IOdiOperationsService;
import ro.bcr.bita.odi.template.IOdiCommandContext;
import ro.bcr.bita.odi.template.OdiBasicTemplate;

import oracle.odi.domain.mapping.Mapping;

public interface IBitaDomainFactory extends IBitaModelFactory,IOdiEntityFactory{
	
	
	public abstract IOdiMapping newOdiMapping(Mapping odiObject) throws BitaModelException;
	public abstract IOdiScenario newOdiScenario(oracle.odi.domain.runtime.scenario.OdiScenario  odiObject);
	public abstract IOdiOperationsService newOdiOperationsService();
	public abstract IOdiCommandContext newOdiTemplateCommandContext();
	public abstract OdiBasicTemplate newOdiTemplate();

}
