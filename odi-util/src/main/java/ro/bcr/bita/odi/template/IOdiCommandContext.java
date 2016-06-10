package ro.bcr.bita.odi.template;

import ro.bcr.bita.model.IOdiMapping;
import ro.bcr.bita.model.IOdiScenario;

import java.util.List;

import oracle.odi.domain.mapping.finder.IMappingFinder;
import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFolderFinder;


public interface IOdiCommandContext {
	
	public IMappingFinder getMappingFinder() throws OdiTemplateException;
	public IOdiProjectFinder getProjectFinder() throws OdiTemplateException;
	public abstract IOdiFolderFinder getProjectFolderFinder() throws OdiTemplateException;
	public IOdiScenarioFinder getScenarioFinder() throws OdiTemplateException;
	public IOdiScenarioFolderFinder getScenarioFolderFinder() throws OdiTemplateException;

	public List<IOdiMapping> findMappings(String... odiPaths) throws OdiTemplateException;
	
	public IOdiScenario findScenarioForMapping(IOdiMapping map,String version) throws OdiTemplateException;
	public IOdiScenario findBitaScenarioForMapping(IOdiMapping map) throws OdiTemplateException;
	public IOdiScenario findTestScenarioForMapping(IOdiMapping map) throws OdiTemplateException;

	
	
}