package ro.bcr.bita.service.sql

import ro.bcr.bita.service.BitaServiceException;
import ro.bcr.bita.service.IBitaSqlSimpleOperations;

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
	public void executeInCurrentTransaction(String... sqls) throws BitaServiceException {
		for (String s:sqls) {
			this.db.execute(s);
		}
		this.commit();
	}

	@Override
	public void commit() throws BitaServiceException {
		this.db.commit();
		
	}

	@Override
	public void rollback() throws BitaServiceException {
		this.db.rollback();
		
	}

}
