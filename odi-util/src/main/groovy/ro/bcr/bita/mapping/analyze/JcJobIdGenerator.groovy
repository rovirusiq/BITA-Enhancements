package ro.bcr.bita.mapping.analyze

import ro.bcr.bita.core.IBitaGlobals;
import ro.bcr.bita.mapping.analyze.IJcJobIdGenerator
import ro.bcr.bita.model.IOdiMapping

public class JcJobIdGenerator implements IJcJobIdGenerator,IBitaGlobals {

	@Override
	public String generateJobId(JcRequestContext params,String mappingName) {
		if (BITA_ODI_SCENARIO_VERSION.equals(params.scenarioVersion)) {
			return mappingName;
		} else {
			return mappingName+"_"+params.scenarioVersion;
		}
	}
	
	

}
