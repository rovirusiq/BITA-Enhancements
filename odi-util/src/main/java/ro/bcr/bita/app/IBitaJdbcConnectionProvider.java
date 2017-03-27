package ro.bcr.bita.app;

public interface IBitaJdbcConnectionProvider {
	
	public groovy.sql.Sql getGroovySql();

}
