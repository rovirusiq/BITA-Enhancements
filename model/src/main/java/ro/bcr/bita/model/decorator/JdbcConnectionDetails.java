package ro.bcr.bita.model.decorator;

import ro.bcr.bita.model.ConnectionDetails;
import ro.bcr.bita.model.IncompleteSpecifiedModelException;

public class JdbcConnectionDetails implements IJdbcConnection {
	
	private ConnectionDetails connection; 
	
	public JdbcConnectionDetails(ConnectionDetails connection) {
		this.connection=connection;
	}
	
	
	/************************************************************************************************************
	 *
	 *Internal Methods
	 *
	 ************************************************************************************************************/
	private boolean isInfoAvailable(String...info) {
		boolean ok=true;
		for (int i = 0; i < info.length; i++) {
			if ((info[i]==null) || ("".equals(info[i]))) return false;
		}
		return ok;
	}
	
	private String createExceptionMessage() {
		return "Not enough details to provide the required information:"+connection;
	}
	
	/************************************************************************************************************
	 *
	 *Public API
	 *
	 ************************************************************************************************************/
	@Override
	public String provideJdbcConnectionString() throws IncompleteSpecifiedModelException{
		StringBuilder result=new StringBuilder("");
		if ( isInfoAvailable(connection.getJdbcConnectionString()) ) {
			return connection.getJdbcConnectionString();
		}
		
		if (!isInfoAvailable(connection.getJdbcDriverClassName(),connection.getJdbcDriverType(),connection.getHost(),connection.getPort(),connection.getSid())) throw new IncompleteSpecifiedModelException(createExceptionMessage());
			
		result.append(connection.getJdbcDriverType()).append(":@");
		result.append(connection.getHost()).append(":").append(connection.getPort()).append(":").append(connection.getSid());
		
		return result.toString();
	}
	
	@Override
	public String provideJdbcUsername() throws IncompleteSpecifiedModelException{
		if (!isInfoAvailable(connection.getJdbcUser())) throw new IncompleteSpecifiedModelException(createExceptionMessage());
		return connection.getJdbcUser();
	}
	@Override
	public String provideJdbcPassword() throws IncompleteSpecifiedModelException{
		if (!isInfoAvailable(connection.getJdbcPassword())) throw new IncompleteSpecifiedModelException(createExceptionMessage());
		return  connection.getJdbcPassword();
	}
	
}
