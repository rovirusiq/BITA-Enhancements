package ro.bcr.bita.mapping.analyze

import ro.bcr.bita.core.IBitaGlobals;
import ro.bcr.bita.mapping.analyze.IJcJobIdGenerator
import ro.bcr.bita.model.IOdiMapping

public class JcJobIdGenerator implements IJcJobIdGenerator,IBitaGlobals {

	@Override
	public String generateJobId(JcRequestContext params,String mappingName) {
		
		String rsp;
		if (BITA_ODI_SCENARIO_VERSION.equals(params.scenarioVersion)) {
			rsp=mappingName;
		} else {
			rsp=mappingName+"_"+params.scenarioVersion;
		}
		
		if (rsp.length()>100) {
			Integer hcd=rsp.hashCode();
			rsp=hcd.toString();
			if (hcd<0) {
				rsp=rsp.replace("-","")+"_";
			}
		}
		return rsp;
	}
	
	

}
