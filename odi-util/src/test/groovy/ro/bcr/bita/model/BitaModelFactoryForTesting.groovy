package ro.bcr.bita.model

import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.proxy.OdiPathUtil;
import ro.bcr.bita.odi.proxy.OdiProjectPaths;
import ro.bcr.bita.odi.template.IOdiCommandContext;

import java.util.Map;
import java.util.Set;

class BitaModelFactoryForTesting extends BitaModelFactory {

	protected BitaModelFactoryForTesting() {
		
	}
	
	public static IBitaModelFactory newInstance() {
		return new BitaModelFactoryForTesting();
	}
	
}
