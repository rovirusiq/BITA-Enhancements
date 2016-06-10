package ro.bcr.bita.model;

import oracle.odi.domain.mapping.Mapping;

public class BitaModelFactory implements IBitaModelFactory {
	
	
	/* (non-Javadoc)
	 * @see ro.bcr.bita.model.IBitaModelFactory#createOdiMapping(oracle.odi.domain.mapping.Mapping)
	 */
	@Override
	public IOdiMapping createOdiMapping(Mapping odiObject) throws BitaModelException{
		return new OdiMapping(odiObject);
	}

}
