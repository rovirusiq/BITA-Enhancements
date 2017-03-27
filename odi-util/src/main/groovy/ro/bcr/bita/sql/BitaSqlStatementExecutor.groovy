package ro.bcr.bita.sql

import ro.bcr.bita.model.IMessageCollection

import groovy.sql.Sql

import java.sql.Connection
import java.util.List

class BitaSqlStatementExecutor implements IBitaSqlStatementExecutor {
	
	private final Sql db;
	private Long counter=0L;
	private Long maxCounter=Long.MAX_VALUE;
	
	public BitaSqlStatementExecutor(Sql sql,Long max=Long.MAX_VALUE) {
		this.db=sql;
		Connection conn=this.db.getConnection();
		conn.setAutoCommit(false);
		this.maxCounter=max;
	}

	@Override
	public void executeInCurrentTransaction(String... sqls)
			throws BitaSqlException {
		for (String sqlCmd:sqls) {
			executeCommand(sqlCmd);
		}

	}

	@Override
	public void executeInCurrentTransaction(IMessageCollection... msgColls)
			throws BitaSqlException {
		for (IMessageCollection m:msgColls) {
			for (String k:m.getKeys()) {
				for (String sqlCmd:m.getMessagesForKey(k)) {
					executeCommand(sqlCmd);
				}
			}
					
		}

	}
	
	private void executeCommand(String sqlCmd) {
		this.db.execute(sqlCmd);
		counter++;
		if (counter>=maxCounter-1) {
			this.commit();
		}
	}

	@Override
	public void commit() throws BitaSqlException {
		this.db.commit();
		counter=0;
	}

	@Override
	public void rollback() throws BitaSqlException {
		this.db.rollback();
		counter=0;
	}

	@Override
	public void executeInCurrentTransaction(List<String> sqls) throws BitaSqlException {
		for (String sqlCmd:sqls) {
			executeCommand(sqlCmd);
		}
		
	}

	@Override
	public void close() throws BitaSqlException {
		this.db.close();
		
	}

}
