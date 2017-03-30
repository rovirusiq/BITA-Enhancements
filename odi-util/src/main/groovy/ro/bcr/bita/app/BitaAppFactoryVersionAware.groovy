package ro.bcr.bita.app

import ro.bcr.bita.mapping.analyze.JcDalGroupStrategyVersionAware
import ro.bcr.bita.mapping.analyze.JcJobIdGeneratorVersionAware
import ro.bcr.bita.mapping.analyze.JcLdsGroupStrategyVersionAware

import oracle.odi.core.OdiInstance

class BitaAppFactoryVersionAware extends BitaAppFactory {

	/**
	 * @param odiInstance
	 */
	protected BitaAppFactoryVersionAware(OdiInstance odiInstance) {
		super(odiInstance);
	}

	/* (non-Javadoc)
	 * @see ro.bcr.bita.app.BitaAppFactory#newJcJobGroupsCreatorForBase(java.lang.String, java.lang.String)
	 */
	@Override
	public IJcJobGroupsCreator newJcJobGroupsCreatorForBase(String dwhVersionCd, String topologyServerName) {
		JcGroupsCreator jcCreator=super.newJcJobGroupsCreatorForBase(dwhVersionCd, topologyServerName);
		jcCreator.setJobIdPolicy(new JcJobIdGeneratorVersionAware());
		jcCreator.setJcGroupIdentificationStrategy(new JcDalGroupStrategyVersionAware(newJcSqlCommandGenerator()));
		return jcCreator;
		
	}

	/* (non-Javadoc)
	 * @see ro.bcr.bita.app.BitaAppFactory#newJcJobGroupsCreatorForLds(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public IJcJobGroupsCreator newJcJobGroupsCreatorForLds(String jobGroupName,String dwhVersionCd, String topologyServerName) {
		JcGroupsCreator jcCreator=super.newJcJobGroupsCreatorForLds(jobGroupName, dwhVersionCd,topologyServerName);
		jcCreator.setJobIdPolicy(new JcJobIdGeneratorVersionAware());
		jcCreator.setJcGroupIdentificationStrategy(new JcLdsGroupStrategyVersionAware(jobGroupName,newJcSqlCommandGenerator()));
		return jcCreator;
	}

	
	
}
