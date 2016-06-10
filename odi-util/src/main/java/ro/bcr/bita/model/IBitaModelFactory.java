package ro.bcr.bita.model;

import ro.bcr.bita.proxy.odi.IOdiEntityFactory;

import oracle.odi.domain.mapping.Mapping;

public interface IBitaModelFactory {

	public abstract IOdiMapping createOdiMapping(Mapping odiObject) throws BitaModelException;
	
	///public abstract IOdiMapping createOdiProject(IOdiEntityFactory odiEntityFactory,String projectCode) throws BitaModelException;

}