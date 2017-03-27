package ro.bcr.bita.model

import ro.bcr.bita.odi.proxy.BitaOdiException
import ro.bcr.bita.odi.proxy.IOdiBasicPersistenceService
import ro.bcr.bita.odi.proxy.IOdiEntityFactory
import ro.bcr.bita.odi.proxy.IOdiFullMappingPath
import ro.bcr.bita.odi.proxy.OdiPathUtil
import ro.bcr.bita.odi.proxy.OdiProjectPaths

import java.util.Map
import java.util.Set

import oracle.odi.core.persistence.transaction.ITransactionStatus
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFolderFinder
import oracle.odi.domain.topology.finder.IOdiContextFinder
import oracle.odi.domain.topology.finder.IOdiDataServerFinder
import oracle.odi.domain.topology.finder.IOdiLogicalSchemaFinder
import oracle.odi.domain.topology.finder.IOdiPhysicalSchemaFinder

class BitaDomainFactoryForMappingsWithMultipleTargets implements IBitaDomainFactory{
	
	@Delegate
	private final BitaDomainFactory bitaDomainFactory;
	
	private BitaDomainFactoryForMappingsWithMultipleTargets(IBitaModelFactory bitaFactory,IOdiEntityFactory odiFactory) {
		this.bitaDomainFactory=BitaDomainFactory.newInstance(bitaFactory,odiFactory);
	}

	@Override
	public IOdiMapping newOdiMapping(Mapping odiObject) {
		//allow multiple targets
		return new OdiMapping(odiObject,this,true);
	}
	
	/********************************************************************************************
	 *
	 *Static methods
	 *
	 ********************************************************************************************/
	public static final newInstance(IBitaModelFactory bitaFactory,IOdiEntityFactory odiFactory) {
		return new BitaDomainFactoryForMappingsWithMultipleTargets(bitaFactory,odiFactory);
	}
}
