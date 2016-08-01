package ro.bcr.bita.sql

import ro.bcr.bita.mapping.analyze.BitaMappingAnalyzeException;
import ro.bcr.bita.model.IMessageCollection;
import ro.bcr.bita.sql.IBitaBatchSql;

import groovy.sql.BatchingStatementWrapper;
import groovy.sql.Sql;
import groovy.transform.CompileStatic;

@CompileStatic
class BitaBatchSql implements IBitaBatchSql{
	
	
	private final Sql db;
	private static Integer BATCH_SIZE=1000;
	
	public BitaBatchSql(Sql sql) {
		this.db=sql;
	}

	@Override
	public void executeInTransaction(IMessageCollection msgColl) throws BitaSqlException {
		IMessageCollection[] arr=new IMessageCollection[1];
		arr[0]=msgColl;
		this.executeInTransaction(arr);
		
	}

	@Override
	public void executeInTransaction(IMessageCollection... msgColls) throws BitaSqlException {
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
