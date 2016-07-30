package ro.bcr.bita.service;

public interface IBitaSqlSimpleOperations {

	public void executeInCurrentTransaction(String... sqls) throws BitaServiceException;
	public void commit() throws BitaServiceException;
	public void rollback() throws BitaServiceException;
	
}
