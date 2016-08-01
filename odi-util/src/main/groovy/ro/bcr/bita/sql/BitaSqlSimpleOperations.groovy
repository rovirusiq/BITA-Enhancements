package ro.bcr.bita.sql

import ro.bcr.bita.mapping.analyze.BitaMappingAnalyzeException;
import ro.bcr.bita.sql.IBitaSqlSimpleOperations;

import groovy.sql.Sql;

import java.sql.Connection

class BitaSqlSimpleOperations implements IBitaSqlSimpleOperations{
	
	private final Sql db;
	
	public BitaSqlSimpleOperations(Sql sql) {
		this.db=sql;
		Connection conn=this.db.getConnection();
		conn.setAutoCommit(false);
	}

	@Override
	public void executeInCurrentTransaction(String... sqls) throws BitaSqlException {
		for (String s:sqls) {
			this.db.execute(s);
		}
	}

	@Override
	public void commit() throws BitaSqlException {
		this.db.commit();
		
	}

	@Override
	public void rollback() throws BitaSqlException {
		this.db.rollback();
		
	}

}
