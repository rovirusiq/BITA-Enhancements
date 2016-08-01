package ro.bcr.bita.sql;

import ro.bcr.bita.model.IMessageCollection;

public interface IBitaBatchSql {
	
	public void executeInTransaction(IMessageCollection msgColl) throws BitaSqlException;
	public void executeInTransaction(IMessageCollection... msgColl) throws BitaSqlException;

}
