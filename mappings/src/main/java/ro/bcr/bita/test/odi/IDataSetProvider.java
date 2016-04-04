package ro.bcr.bita.test.odi;

import org.dbunit.dataset.IDataSet;

public interface IDataSetProvider {
	
	public IDataSet getDataSet(String dataSetType,String... dataSetIdentifiers) throws OdiTestingException;

}
