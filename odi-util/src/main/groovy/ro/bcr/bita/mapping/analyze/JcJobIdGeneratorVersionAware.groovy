package ro.bcr.bita.mapping.analyze
import java.security.MessageDigest

class JcJobIdGeneratorVersionAware implements IJcJobIdGenerator {

	@Override
	public String generateJobId(JcRequestContext params, String mappingName) {
			String rsp=params.dwhVersion+"_"+mappingName;
			if (rsp.length()>100) {
				return params.dwhVersion+"_"+rsp.hashCode().toString().replace("-","_");
			}
			return rsp;
	}

}
