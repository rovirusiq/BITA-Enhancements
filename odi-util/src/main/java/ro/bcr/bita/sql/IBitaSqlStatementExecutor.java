package ro.bcr.bita.sql;

public interface IBitaSqlStatementExecutor extends IBitaSqlExecutor{

	public void commit() throws BitaSqlException;
	public void rollback() throws BitaSqlException;
	public void close() throws BitaSqlException;
	
}
