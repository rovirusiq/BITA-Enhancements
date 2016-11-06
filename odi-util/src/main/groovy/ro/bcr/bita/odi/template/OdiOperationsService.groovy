package ro.bcr.bita.odi.template

import ro.bcr.bita.model.BitaModelFactory
import ro.bcr.bita.model.IBitaDomainFactory;
import ro.bcr.bita.model.IBitaModelFactory
import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.model.IOdiScenario
import ro.bcr.bita.odi.proxy.BitaOdiException;
import ro.bcr.bita.odi.proxy.IOdiBasicPersistenceService
import ro.bcr.bita.odi.proxy.IOdiEntityFactory
import ro.bcr.bita.odi.proxy.IOdiFullMappingPath;
import ro.bcr.bita.odi.proxy.IOdiOperationsService;
import ro.bcr.bita.odi.proxy.IOdiProjectPaths
import ro.bcr.bita.odi.proxy.OdiPathUtil
import ro.bcr.bita.odi.proxy.OdiPathUtil.MappingPaths;
import ro.bcr.bita.odi.template.IOdiMappingVersions;

import java.util.HashMap;

import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.domain.IOdiEntity;
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.domain.runtime.scenario.OdiScenario
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFolderFinder
import oracle.odi.runtime.agent.invocation.ExecutionInfo;
import oracle.odi.runtime.agent.invocation.RemoteRuntimeAgentInvoker
import oracle.odi.runtime.agent.invocation.StartupParams;

class OdiOperationsService implements IOdiOperationsService,IOdiMappingVersions{
	
	private final OdiPathUtil odiPathUtils;
	private IBitaDomainFactory bitaFactory;
	
	public OdiOperationsService(IBitaDomainFactory bitaDFactory) {
		this.bitaFactory=bitaDFactory;
		this.odiPathUtils=this.bitaFactory.newOdiPathUtil();
	}
	
	/********************************************************************************************
	 *
	 *IOdiOperationsService
	 *
	 ********************************************************************************************/
	@Override
	public IMappingFinder getMappingFinder() {
		return this.bitaFactory.createMappingFinder()
	}

	@Override
	public IOdiProjectFinder getProjectFinder() {
		return this.bitaFactory.createProjectFinder();
	}

	@Override
	public IOdiScenarioFinder getScenarioFinder() {
		return this.bitaFactory.createScenarioFinder();
	}
	
	@Override
	public IOdiScenarioFolderFinder getScenarioFolderFinder() {
		return this.bitaFactory.createScenarioFolderFinder();
	}
	
	@Override
	public IOdiFolderFinder getProjectFolderFinder() {
		return this.bitaFactory.createProjectFolderFinder();
	}

	
	/********************************************************************************************
	 *Value added methods
	 ********************************************************************************************/
	@Override
	public List<IOdiMapping> findMappings(String... odiPaths) throws BitaOdiException{
		
		try {
		
			MappingPaths rsp=this.odiPathUtils.extractMappingPaths(odiPaths);
			
			List<IOdiMapping> mps=[];
			
			Set<IOdiFullMappingPath> includedMappings=rsp.getIncludeMappings();
			Set<IOdiFullMappingPath> excludedMappings=rsp.getExcludeMappings();
			
			IMappingFinder finder=this.bitaFactory.createMappingFinder();
			
			for (String prj:rsp.getProjectPaths().getProjects()) {
				
				for (String folder:rsp.getProjectPaths().getFoldersForProject(prj)) {
				
					mps << (finder.findByProject(prj,folder).collect{it->
								IOdiFullMappingPath p=this.bitaFactory.newOdiMappingFullPath(prj,folder,it.getName());						
								if (!excludedMappings.contains(p)) {
									return bitaFactory.newOdiMapping(it);
								} else {
									return [];
								}
							});
				}
			}
			
			for (IOdiFullMappingPath mp:includedMappings) {
				Collection c=finder.findByName(mp.getMappingName(),mp.getProjectCode(),mp.getFolderName());
				if (c.size()==1) {
					mps << bitaFactory.newOdiMapping(c.getAt(0));
				} else if (c.size()>1) throw new BitaOdiException("Multiple mappinggs with the same name in the same project and the same fodler [${mp.getProjectCode()}:${mp.getMappingName()}:${mp.getFolderName()}]");
			}
	
			return mps.flatten();
		} catch (Exception ex) {
			throw new BitaOdiException("An exception occured when trying to identify mappings from the pats[$odiPaths].",ex);
		}
	}
	

	@Override
	public IOdiScenario findScenarioForMapping(IOdiMapping map, String pVersion) throws BitaOdiException {
		 OdiScenario scenOfInterest=this.getScenarioFinder().findBySourceMapping(map.getInternalId())?.find{elem->elem.getVersion()=="$pVersion"};
		 if (scenOfInterest==null) throw new BitaOdiException("No scenario with version $pVersion was identified for mapping[${map}]");
		 return this.bitaFactory.newOdiScenario(scenOfInterest);
	}

	@Override
	public IOdiScenario findBitaScenarioForMapping(IOdiMapping map) throws BitaOdiException {
		return this.findScenarioForMapping(map,MAPPING_VERSION_NUMBER_FOR_PROD);
	}

	@Override
	public IOdiScenario findTestScenarioForMapping(IOdiMapping map) throws BitaOdiException {
		return this.findScenarioForMapping(map,MAPPING_VERSION_NUMBER_FOR_TESTING);
	}

}
