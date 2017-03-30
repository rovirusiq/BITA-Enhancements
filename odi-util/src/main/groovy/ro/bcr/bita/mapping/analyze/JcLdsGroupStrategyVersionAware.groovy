package ro.bcr.bita.mapping.analyze

import groovy.transform.CompileStatic

@CompileStatic
class JcLdsGroupStrategyVersionAware extends JcLdsGroupStrategy {
	
	/**
	 * @param jobGroupName
	 * @param sqlGen
	 */
	public JcLdsGroupStrategyVersionAware(String jobGroupName,JcSqlCommandsGenerator sqlGen) {
		super(jobGroupName, sqlGen);
	}

	/* (non-Javadoc)
	 * @see ro.bcr.bita.mapping.analyze.JcLdsGroupStrategy#generateJobGroupName(ro.bcr.bita.mapping.analyze.JcRequestContext, java.lang.String)
	 */
	@Override
	protected String generateJobGroupName(JcRequestContext params,String jobGroupName) {
		return params.dwhVersion+"_"+jobGroupName;
	}
	
	

}
