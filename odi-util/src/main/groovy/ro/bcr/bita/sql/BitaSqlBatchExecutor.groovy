package ro.bcr.bita.sql

import ro.bcr.bita.model.IMessageCollection

import java.util.List

import groovy.sql.BatchingStatementWrapper
import groovy.sql.Sql


class BitaSqlBatchExecutor implements IBitaSqlBatchExecutor {
	
	private BigInteger batchSize;
	
	private final Sql db;
	
	
	public BitaSqlBatchExecutor(Sql sql,Long batchSize=Long.MAX_VALUE) {
		this.batchSize=batchSize;
		this.db=sql;
	}

	@Override
	public void executeInCurrentTransaction(String... sqls)
			throws BitaSqlException {
		this.db.withTransaction{
			db.withBatch(this.batchSize){ BatchingStatementWrapper stmt->
				sqls.each{String sqlCmd->
					stmt.addBatch(sqlCmd);
				}
			}
		}

	}

	@Override
	public void executeInCurrentTransaction(IMessageCollection... msgColls)
			throws BitaSqlException {
		this.db.withTransaction{
			db.withBatch(this.batchSize){ BatchingStatementWrapper stmt->
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

	@Override
	public void setBatchSize(Long batchSize) {
		this.batchSize=batchSize;
	}

	@Override
	public void executeInCurrentTransaction(List<String> sqls) throws BitaSqlException {
		this.db.withTransaction{
			db.withBatch(this.batchSize){ BatchingStatementWrapper stmt->
				for (String sqlCmd:sqls) {
					stmt.addBatch(sqlCmd);
				}
				
			}
		}
	}

	@Override
	public void close() throws BitaSqlException {
		this.db.close();
	}

}
