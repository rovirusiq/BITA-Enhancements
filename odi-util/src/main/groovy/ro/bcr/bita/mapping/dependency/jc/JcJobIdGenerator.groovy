package ro.bcr.bita.mapping.dependency.jc

import ro.bcr.bita.core.IBitaGlobals;
import ro.bcr.bita.mapping.analyze.dependency.jc.IJobIdGenerator

class JcJobIdGenerator implements IJobIdGenerator,IBitaGlobals {

	@Override
	public String generateJobId(String jobGroupId,String mappingName, String scenarioVersionNumber) {
		if (BITA_ODI_SCENARIO_VERSION.equals(scenarioVersionNumber)) {
			return mappingName;
		} else {
			return mappingName+"_"+scenarioVersionNumber;
		}
	}
	
	

}
