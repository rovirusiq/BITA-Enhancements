package ro.bcr.bita.sql;

import ro.bcr.bita.model.IMessageCollection;

import java.util.List;

public interface IBitaSqlExecutor {
	
	public void executeInCurrentTransaction(String... sqls) throws BitaSqlException;
	public void executeInCurrentTransaction(IMessageCollection... msgColls) throws BitaSqlException;
	public void executeInCurrentTransaction(List<String> sqls) throws BitaSqlException;
	
}
