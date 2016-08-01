package ro.bcr.bita.odi.proxy

import ro.bcr.bita.model.BitaModelFactory
import ro.bcr.bita.model.IBitaModelFactory
import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.model.IOdiScenario
import ro.bcr.bita.odi.proxy.BitaOdiException;
import ro.bcr.bita.odi.proxy.IOdiBasicPersistenceService
import ro.bcr.bita.odi.proxy.IOdiEntityFactory
import ro.bcr.bita.odi.proxy.IOdiProjectPaths
import ro.bcr.bita.odi.proxy.OdiPathUtil
import ro.bcr.bita.odi.template.IOdiMappingVersions;

import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.domain.IOdiEntity;
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.domain.runtime.scenario.OdiScenario
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFolderFinder

class OdiOperationsService implements IOdiOperationsService,IOdiMappingVersions{
	
	private final IOdiEntityFactory odiEntityFactory;
	private final OdiPathUtil odiPathUtils;
	private IBitaModelFactory bitaModelFactory;
	
	public OdiOperationsService(IBitaModelFactory bitaModelFactory,IOdiEntityFactory odiEntityFactory) {
		this.odiEntityFactory=odiEntityFactory;
		this.bitaModelFactory=bitaModelFactory;
		this.odiPathUtils=this.odiEntityFactory.newOdiPathUtil();
	}
	
	/********************************************************************************************
	 *
	 *IOdiOperationsService
	 *
	 ********************************************************************************************/
	@Override
	public IMappingFinder getMappingFinder() {
		return this.odiEntityFactory.createMappingFinder()
	}

	@Override
	public IOdiProjectFinder getProjectFinder() {
		return this.odiEntityFactory.createProjectFinder();
	}

	@Override
	public IOdiScenarioFinder getScenarioFinder() {
		return this.odiEntityFactory.createScenarioFinder();
	}
	
	@Override
	public IOdiScenarioFolderFinder getScenarioFolderFinder() {
		return this.odiEntityFactory.createScenarioFolderFinder();
	}
	
	@Override
	public IOdiFolderFinder getProjectFolderFinder() {
		return this.odiEntityFactory.createProjectFolderFinder();
	}

	
	/********************************************************************************************
	 *Value added methods
	 ********************************************************************************************/
	@Override
	public List<IOdiMapping> findMappings(String... odiPaths) throws BitaOdiException{
		
		try {
		
			IOdiProjectPaths rsp=this.odiPathUtils.extractProjectPaths(odiPaths);
			
			List<IOdiMapping> mps=[];
			
			
			IMappingFinder finder=this.odiEntityFactory.createMappingFinder();
			
			for (String prj:rsp.getProjects()) {
				
				for (String folder:rsp.getFoldersForProject(prj)) {
				
					mps << (finder.findByProject(prj,folder).collect{bitaModelFactory.newOdiMapping(it)});
				}
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
		 return scenOfInterest;
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
