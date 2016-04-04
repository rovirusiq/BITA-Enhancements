package ro.bcr.bita.odi.script

import java.nio.file.Path
import java.nio.file.Paths;

class BitaScriptEnv {
	
	public static final File baseScript() {
		List<String> possibleLocations=["ro/bcr/bita/odi/script/BitaBaseScript.groovy","BitaBaseScript.groovy","src/main/groovy/ro/bcr/bita/odi/script/BitaBaseScript.groovy"]
		
		for (loc in possibleLocations) {
			URL rs=BitaScriptEnv.getClassLoader().getResource(loc);
			if (rs!=null) {
				return new File(rs.toURI());
			}
		}
		
		//alternative locations for execution from IDE. Not needed in other cases
		Path p=Paths.get(new File("ceva.txt").toURI()).parent;
		File nF=new File(p.toString()+"/src/main/resources/ro/bcr/bita/odi/script/BitaBaseScript.groovy");
		if (nF.exists()) return nF;
	
		throw new RuntimeException("The base script cannot be identified on the classpath.");		
	}
	
}
