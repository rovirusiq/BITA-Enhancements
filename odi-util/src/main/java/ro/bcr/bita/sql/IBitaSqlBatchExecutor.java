package ro.bcr.bita.sql;


public interface IBitaSqlBatchExecutor extends IBitaSqlExecutor{
	
	public void setBatchSize(Long batchSize);
	public void close() throws BitaSqlException;

}
