package ro.bcr.bita.odi.template

import java.util.List;
import java.util.Map;

import oracle.odi.domain.mapping.Mapping;
import oracle.odi.domain.mapping.finder.IMappingFinder;
import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.runtime.scenario.OdiScenario;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder;

public  class OdiCommandContext implements IOdiCommandContext{
	private IOdiEntityFactory odiEntityFactory;
	
	private OdiCommandContext(IOdiEntityFactory odiEntityFactory) {
		this.odiEntityFactory=odiEntityFactory;
	}
	
	public getOdiEntityFactory() {
		return this.odiEntityFactory;
	}
	
	@Override
	public IMappingFinder getMappingFinder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IOdiProjectFinder getProjectFinder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IOdiFolderFinder getFolderFinder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IOdiScenarioFinder getScenarioFinder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mapping> findMappings(Map<String, String> param) {
		if (param.size()==1) {
			return findMappings(param.keySet()[0],param.values()[0]);
		} else if (param.size()==2) {
			return findMappings(param["PROJECT_CODE"],param["FOLDER_NAME"]);
		}
		throw new RuntimeException("Invalid parameter");
	}

	@Override
	public List<Mapping> findMappings(String projectCode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Mapping> findMappings(String projectCode,String folderName){
			return null;
	}

	@Override
	public OdiScenario findScenarioForMapping(Mapping map, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OdiScenario findBitaScenarioForMapping(Mapping map) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OdiScenario findTestScenarioForMapping(Mapping map) {
		return null;
	}
	
	

	
}