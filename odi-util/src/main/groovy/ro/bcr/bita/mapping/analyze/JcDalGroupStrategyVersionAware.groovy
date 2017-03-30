package ro.bcr.bita.mapping.analyze

import groovy.transform.CompileStatic

@CompileStatic
class JcDalGroupStrategyVersionAware extends JcDalGroupStrategy {

	/**
	 * @param sqlGen
	 */
	public JcDalGroupStrategyVersionAware(JcSqlCommandsGenerator sqlGen) {
		super(sqlGen);
	}

	/* (non-Javadoc)
	 * @see ro.bcr.bita.mapping.analyze.JcDalGroupStrategy#generateBaseStandardGroupName(ro.bcr.bita.mapping.analyze.JcRequestContext, java.lang.String)
	 */
	@Override
	protected String generateBaseStandardGroupName(JcRequestContext params, String leadingSourceName) {
		return params.dwhVersion+"_"+leadingSourceName;
	}

	/* (non-Javadoc)
	 * @see ro.bcr.bita.mapping.analyze.JcDalGroupStrategy#generateBaseRecoGroupName(ro.bcr.bita.mapping.analyze.JcRequestContext, java.lang.String)
	 */
	@Override
	protected String generateBaseRecoGroupName(JcRequestContext param, String targetName) {
		return param.dwhVersion+"_"+"RECO_"+targetName;
	}

	
	

}
