package ro.bcr.bita.model.decorator;

public interface IJdbcConnection {
	
	public String provideJdbcConnectionString();
	public String provideJdbcUsername();
	public String provideJdbcPassword();
}
