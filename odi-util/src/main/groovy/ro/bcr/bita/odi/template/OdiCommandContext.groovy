package ro.bcr.bita.odi.template

import ro.bcr.bita.model.BitaModelFactory
import ro.bcr.bita.model.IBitaModelFactory
import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.model.IOdiScenario
import ro.bcr.bita.odi.proxy.BitaOdiException;
import ro.bcr.bita.odi.proxy.IOdiBasicPersistenceService
import ro.bcr.bita.odi.proxy.IOdiEntityFactory
import ro.bcr.bita.odi.proxy.IOdiOperationsService;
import ro.bcr.bita.odi.proxy.IOdiProjectPaths
import ro.bcr.bita.odi.proxy.OdiEntityFactory;
import ro.bcr.bita.odi.proxy.OdiPathUtil

import bsh.This;
import groovy.transform.TypeChecked;

import java.util.List;

import com.sun.org.apache.bcel.internal.generic.RETURN;

import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.domain.IOdiEntity;
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.domain.runtime.scenario.OdiScenario
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFolderFinder

public  class OdiCommandContext implements IOdiCommandContext,IOdiMappingVersions{
	private final IOdiEntityFactory odiEntityFactory;
	private final IOdiBasicPersistenceService odiPersSrv;
	private final IOdiOperationsService odiOpSrv;
	private static ThreadLocal<ITransactionStatus> TRN_STATUS=new ThreadLocal<ITransactionStatus>();
	private static ThreadLocal<Boolean> IN_TRANSACTION=new ThreadLocal<Boolean>() {
		@Override 
		protected Boolean initialValue() {
			return false;
		}
	}
	
	
	public OdiCommandContext(IOdiEntityFactory odiEntityFactory,IOdiOperationsService opService,IOdiBasicPersistenceService persistService) {
		this.odiEntityFactory=odiEntityFactory;
		this.odiPersSrv=persistService;
		this.odiOpSrv=opService;
	}
	
	@TypeChecked
	public OdiEntityFactory getOdiEntityFactory() {
		return (OdiEntityFactory)this.odiEntityFactory;
	}
	
	/********************************************************************************************
	 *Persistence related
	 ********************************************************************************************/
	@Override
	@TypeChecked
	public void commitTransaction(ITransactionStatus txnStatus) throws BitaOdiException {
		this.odiPersSrv.commitTransaction(txnStatus);
	}

	@Override
	@TypeChecked
	public void rollbackTransaction(ITransactionStatus txnStatus) throws BitaOdiException {
		this.odiPersSrv.rollbackTransaction(txnStatus);
		
	}

	@Override
	@TypeChecked
	public void clearPersistenceContext() throws BitaOdiException {
		this.odiPersSrv.clearPersistenceContext();
		
	}

	@Override
	@TypeChecked
	public void removeFromPersistenceContext(IOdiEntity odiObject) throws BitaOdiException {
		this.odiPersSrv.removeFromPersistenceContext(odiObject);
		
	}

	@Override
	@TypeChecked
	public void persistInRepository(IOdiEntity odiObject) throws BitaOdiException {
		this.odiPersSrv.persistInRepository(odiObject);
		
	}

	@Override
	@TypeChecked
	public void flushPersistenceContext() {
		this.odiPersSrv.flushPersistenceContext();
		
	}

	@Override
	@TypeChecked
	public void detachFromPersistenceContext(IOdiEntity odiObject) throws BitaOdiException {
		this.odiPersSrv.detachFromPersistenceContext(odiObject);
	}
	/********************************************************************************************
	 *ODI Operations related
	 ********************************************************************************************/

	@Override
	@TypeChecked
	public IMappingFinder getMappingFinder() throws BitaOdiException {
		return this.odiOpSrv.getMappingFinder();
	}

	@Override
	@TypeChecked
	public IOdiProjectFinder getProjectFinder() throws BitaOdiException {
		return this.odiOpSrv.getProjectFinder();
	}

	@Override
	@TypeChecked
	public IOdiFolderFinder getProjectFolderFinder() throws BitaOdiException {
		return this.odiOpSrv.getProjectFolderFinder();
	}

	@Override
	@TypeChecked
	public IOdiScenarioFinder getScenarioFinder() throws BitaOdiException {
		return this.odiOpSrv.getScenarioFinder();
	}

	@Override
	@TypeChecked
	public IOdiScenarioFolderFinder getScenarioFolderFinder() throws BitaOdiException {
		return this.odiOpSrv.getScenarioFolderFinder();
	}

	@Override
	@TypeChecked
	public List<IOdiMapping> findMappings(String... odiPaths) throws BitaOdiException {
		return this.odiOpSrv.findMappings(odiPaths);
	}

	@Override
	@TypeChecked
	public IOdiScenario findScenarioForMapping(IOdiMapping map, String version) throws BitaOdiException {
		return this.odiOpSrv.findScenarioForMapping(map,version);
	}

	@Override
	@TypeChecked
	public IOdiScenario findBitaScenarioForMapping(IOdiMapping map) throws BitaOdiException {
		return this.odiOpSrv.findBitaScenarioForMapping(map);
	}

	@Override
	@TypeChecked
	public IOdiScenario findTestScenarioForMapping(IOdiMapping map) throws BitaOdiException {
		return this.odiOpSrv.findTestScenarioForMapping(map);
	}
	/********************************************************************************************
	 *Transaction status
	 ********************************************************************************************/
	
	@Override
	public void setTransactionStatus(ITransactionStatus st) {
		TRN_STATUS.set(st);
	}

	@Override
	public ITransactionStatus getTransactionStatus() {
		return TRN_STATUS.get();
	}

	@Override
	public Boolean inTransaction() {
		return IN_TRANSACTION.get();
	}

	@Override
	public void setInTransaction(Boolean inTransaction) {
		IN_TRANSACTION.set(inTransaction);
	}
	
	

	
}