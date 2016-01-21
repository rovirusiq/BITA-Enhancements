package ro.ns.test.support.groovy;

import groovy.sql.Sql;
import groovy.xml.StreamingMarkupBuilder

import java.util.List;

import org.dbunit.IDatabaseTester
import org.dbunit.JdbcDatabaseTester
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet

public class HSQLInfrastructureProvider {
	
	private final String RUNTIME_CONNECTION_DRIVER;
	private final List<String> RUNTIME_CONNECTION_PROPS;
	
	private static class Builder{
		public HSQLInfrastructureProvider build() {
			return HSQLInfrastructureProvider.createProvider();
		}
	}
	
	
	private static IDataSet createDataSet(dataClosure) {
		new FlatXmlDataSet(new StringReader(new StreamingMarkupBuilder().bind{dataset dataClosure}.toString()));
	}
	
	private HSQLInfrastructureProvider(String databaseName="mem-tests",String username="sa", String password="sa",Boolean oracleSyntax=Boolean.TRUE) {
		RUNTIME_CONNECTION_DRIVER="org.hsqldb.jdbcDriver";
		String databaseUrl="jdbc:hsqldb:mem:${databaseName}"+((oracleSyntax)? ";sql.syntax_ora=true":"");
		RUNTIME_CONNECTION_PROPS=[databaseUrl,username,password];
	}
	
	
	public Sql createSqlGroovyObject() {
		return Sql.newInstance(*RUNTIME_CONNECTION_PROPS,RUNTIME_CONNECTION_DRIVER);
	}
	
	public JdbcDatabaseTester createJdbcDatabaseTester() {
		return new JdbcDatabaseTester(RUNTIME_CONNECTION_DRIVER,*RUNTIME_CONNECTION_PROPS);
	}
	
	public String getJdbcConnectionString() {
		return RUNTIME_CONNECTION_PROPS[0];
	}
	
	public String getJdbcUser() {
		return RUNTIME_CONNECTION_PROPS[1];
	}
	
	public String getJdbcPassword() {
		return RUNTIME_CONNECTION_PROPS[2];
	}
	
	public void createInitialDatbaseState(IDatabaseTester tester,Closure cl) {
		tester.dataSet=createDataSet(cl);
		tester.onSetup();
	}
	
	public void clearDatbaseState(IDatabaseTester tester) {
		tester.onTearDown();
	}
	
	
	public static final createProvider() {
		return new HSQLInfrastructureProvider();
	}
}
