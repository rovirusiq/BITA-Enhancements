package ro.bcr.bita.service.sql

import ro.bcr.bita.model.IMessageCollection;
import ro.bcr.bita.service.BitaServiceException;
import ro.bcr.bita.service.IBitaSql;

import groovy.sql.BatchingStatementWrapper;
import groovy.sql.Sql;
import groovy.transform.CompileStatic;

@CompileStatic
class BitaSql implements IBitaSql{
	
	
	private final Sql db;
	private static Integer BATCH_SIZE=1000;
	
	public BitaSql(Sql sql) {
		this.db=sql;
	}

	@Override
	public void executeInTransaction(IMessageCollection msgColl) throws BitaServiceException {
		IMessageCollection[] arr=new IMessageCollection[1];
		arr[0]=msgColl;
		this.executeInTransaction(arr);
		
	}

	@Override
	public void executeInTransaction(IMessageCollection... msgColls) throws BitaServiceException {
		
		this.db.withTransaction{
			db.withBatch(BATCH_SIZE){ BatchingStatementWrapper stmt->
				for (IMessageCollection m:msgColls) {
					for (String k:m.getKeys()) {
						for (String sqlCmd:m.getMessagesForKey(k)) {
							stmt.addBatch(sqlCmd);
						}
					}
					
				}
			}
		}
		
	}

}
