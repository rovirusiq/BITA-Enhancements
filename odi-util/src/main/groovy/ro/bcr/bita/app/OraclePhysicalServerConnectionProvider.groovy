package ro.bcr.bita.app

import groovy.sql.Sql

import ro.bcr.bita.odi.proxy.IOdiEntityFactory

import com.sun.org.apache.bcel.internal.generic.RETURN

class OraclePhysicalServerConnectionProvider implements IBitaJdbcConnectionProvider {
	
	private final String oraclePhysicalServerName;
	private final IOdiEntityFactory odiEntityFactory;
	
	/**
	 * @param oraclePhysicalServerName
	 */
	public OraclePhysicalServerConnectionProvider(String oraclePhysicalServerName,IOdiEntityFactory bitaOdiEntityFactory) {
		super();
		this.oraclePhysicalServerName = oraclePhysicalServerName;
		this.odiEntityFactory=bitaOdiEntityFactory;
	}



	@Override
	public Sql getGroovySql() {
		return new Sql(this.odiEntityFactory.createSqlConnection(oraclePhysicalServerName));
	}

}
