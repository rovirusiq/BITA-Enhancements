package ro.bcr.bita.odi.template;

import java.util.List;
import java.util.Map;

import oracle.odi.domain.mapping.Mapping;
import oracle.odi.domain.mapping.finder.IMappingFinder;
import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.runtime.scenario.OdiScenario;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder;


public interface IOdiCommandContext {
	
	public final String SCENARIO_VERSION_BITA="001";
	public final String SCENARIO_VERSION_TEST="999";
	
	public IMappingFinder getMappingFinder();
	public IOdiProjectFinder getProjectFinder();
	public IOdiFolderFinder getFolderFinder();
	public IOdiScenarioFinder getScenarioFinder();

	public List<Mapping> findMappings(String projectCode,String folderName);
	public List<Mapping> findMappings(Map<String,String> param);
	public List<Mapping> findMappings(String projectCode);
	
	public OdiScenario findScenarioForMapping(Mapping map,String version);
	public OdiScenario findBitaScenarioForMapping(Mapping map);
	public OdiScenario findTestScenarioForMapping(Mapping map);
	
}