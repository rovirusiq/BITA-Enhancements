package ro.bcr.bita.odi.proxy;

import ro.bcr.bita.model.IOdiMapping;
import ro.bcr.bita.model.IOdiScenario;

import java.util.List;

import oracle.odi.domain.mapping.finder.IMappingFinder;
import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFolderFinder;

public interface IOdiOperationsService {
	
	public IMappingFinder getMappingFinder() throws BitaOdiException;
	public IOdiProjectFinder getProjectFinder() throws BitaOdiException;
	public abstract IOdiFolderFinder getProjectFolderFinder() throws BitaOdiException;
	public IOdiScenarioFinder getScenarioFinder() throws BitaOdiException;
	public IOdiScenarioFolderFinder getScenarioFolderFinder() throws BitaOdiException;

	public List<IOdiMapping> findMappings(String... odiPaths) throws BitaOdiException;
	
	public IOdiScenario findScenarioForMapping(IOdiMapping map,String version) throws BitaOdiException;
	public IOdiScenario findBitaScenarioForMapping(IOdiMapping map) throws BitaOdiException;
	public IOdiScenario findTestScenarioForMapping(IOdiMapping map) throws BitaOdiException;
	

}
