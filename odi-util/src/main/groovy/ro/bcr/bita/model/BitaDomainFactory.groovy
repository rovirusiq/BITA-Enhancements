package ro.bcr.bita.model

import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.proxy.IOdiOperationsService;
import ro.bcr.bita.odi.template.IOdiCommandContext;
import ro.bcr.bita.odi.template.OdiBasicTemplate;
import ro.bcr.bita.odi.template.OdiCommandContext;
import ro.bcr.bita.odi.template.OdiOperationsService;

import groovy.lang.Delegate;
import groovy.transform.TypeChecked;
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.runtime.scenario.OdiScenario as RealOdiScenario;

class BitaDomainFactory implements IBitaDomainFactory {
	
	private final @Delegate IBitaModelFactory bitaFactory;
	private final @Delegate IOdiEntityFactory odiFactory;
	private final IOdiOperationsService odiOpSrv;
	
	
	protected BitaDomainFactory(IBitaModelFactory bitaFactory,IOdiEntityFactory odiFactory) {
		this.bitaFactory=bitaFactory;
		this.odiFactory=odiFactory;
		this.odiOpSrv=new OdiOperationsService(this);
	}

	
	@Override
	@TypeChecked
	public  IOdiCommandContext newOdiTemplateCommandContext(){
		return new OdiCommandContext(this.odiFactory,this.odiOpSrv,this.odiFactory.newOdiBasicPersistenceService());
	}
	
	@Override
	@TypeChecked
	public OdiBasicTemplate newOdiTemplate() {
		return new OdiBasicTemplate(this);
	}
	
	@Override
	public IOdiOperationsService newOdiOperationsService() {
		return this.odiOpSrv;
	}
	
	@Override
	public IOdiMapping newOdiMapping(Mapping odiObject) {
		return new OdiMapping(odiObject,this);
	}

	@Override
	public IOdiScenario newOdiScenario(RealOdiScenario odiObject) {
		return new OdiScenario(odiObject);
	}
	/********************************************************************************************
	 *
	 * Static methods
	 *
	 ********************************************************************************************/
	public static final newInstance(IBitaModelFactory bitaFactory,IOdiEntityFactory odiFactory) {
		return new BitaDomainFactory(bitaFactory,odiFactory);
	}
	
	

}
