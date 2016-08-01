package ro.bcr.bita.sql;


public interface IBitaSqlSimpleOperations {

	public void executeInCurrentTransaction(String... sqls) throws BitaSqlException;
	public void commit() throws BitaSqlException;
	public void rollback() throws BitaSqlException;
	
}
