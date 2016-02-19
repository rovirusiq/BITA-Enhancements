package ro.bcr.bita.test.odi;

import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;

public class CustomOracleDatabaseTester extends JdbcDatabaseTester {

	public CustomOracleDatabaseTester(String driverClass,
			String connectionUrl, String username, String password)
			throws ClassNotFoundException {
		super(driverClass, connectionUrl, username, password);
	}
	
	public CustomOracleDatabaseTester(String driverClass,
			String connectionUrl, String username, String password, String schemaName)
			throws ClassNotFoundException {
		super(driverClass, connectionUrl, username, password,schemaName);
	}
	
	@Override
	public IDatabaseConnection getConnection() throws Exception{
		
		IDatabaseConnection conn=super.getConnection();
		
		conn.getConfig().setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
		
		return conn;
	}

}
