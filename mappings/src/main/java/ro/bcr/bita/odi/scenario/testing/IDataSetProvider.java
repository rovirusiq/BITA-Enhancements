package ro.bcr.bita.odi.scenario.testing;

import org.dbunit.dataset.IDataSet;

public interface IDataSetProvider {
	
	public IDataSet getDataSet(String dataSetType,String... dataSetIdentifiers) throws OdiTestingException;

}
