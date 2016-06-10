package ro.bcr.bita.odi.script

import java.nio.file.Path
import java.nio.file.Paths;

class BitaScriptEnv {
	
	public static final String baseScript() {
		return '''
		package ro.bcr.bita.odi.script

		class BitaOdiBaseScript extends BitaBaseScript{
			
			
			public Object run() {
				super.run();
			}
		
		}
		''';
	}
	
}
