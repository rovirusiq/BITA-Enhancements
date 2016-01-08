package ro.bcr.bita.model;



public class ConnectionDetails{
	
	private String jdbcDriverClassName=JdbcDriverList.ORACLE.value();
	private String jdbcUser="";
	private String jdbcPassword="";
	private String host="";
	private String jdbcDriverType=JdbcDriverType.ORACLE_THIN.value();
	private String port="1521";
	private String sid="";
	private String jdbcConnectionString="";
	
	
	
	
	/************************************************************************************************************
	 *
	 *Constructors
	 *
	 ************************************************************************************************************/
	protected ConnectionDetails() {
		
	}

	/************************************************************************************************************
	 *
	 *Getters and Setters
	 *
	 ************************************************************************************************************/
	/**
	 * @return the jdbcDriverClassName
	 */
	public String getJdbcDriverClassName() {
		return jdbcDriverClassName;
	}
	/**
	 * @return the jdbcUser
	 */
	public String getJdbcUser() {
		return jdbcUser;
	}
	/**
	 * @return the jdbcPassword
	 */
	public String getJdbcPassword() {
		return jdbcPassword;
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @return the jdbcDriverType
	 */
	public String getJdbcDriverType() {
		return jdbcDriverType;
	}
	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}
	/**
	 * @return the sid
	 */
	public String getSid() {
		return sid;
	}
	/**
	 * @param jdbcDriverClassName the jdbcDriverClassName to set
	 */
	public void setJdbcDriverClassName(String jdbcDriverClassName) {
		this.jdbcDriverClassName = jdbcDriverClassName;
	}
	/**
	 * @param jdbcUser the jdbcUser to set
	 */
	public void setJdbcUser(String jdbcUser) {
		this.jdbcUser = jdbcUser;
	}
	/**
	 * @param jdbcPassword the jdbcPassword to set
	 */
	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @param jdbcDriverType the jdbcDriverType to set
	 */
	public void setJdbcDriverType(String jdbcDriverType) {
		this.jdbcDriverType = jdbcDriverType;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}
	/**
	 * @param sid the sid to set
	 */
	public void setSid(String sid) {
		this.sid = sid;
	}
	
	/**
	 * @return the jdbcConnectionString
	 */
	public String getJdbcConnectionString() {
		return jdbcConnectionString;
	}

	/**
	 * @param jdbcConnectionString the jdbcConnectionString to set
	 */
	public void setJdbcConnectionString(String jdbcConnectionString) {
		this.jdbcConnectionString = jdbcConnectionString;
	}

	/************************************************************************************************************
	 *
	 *Public Exposed Methods
	 *
	 ************************************************************************************************************/
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConnectionDetails [jdbcDriverClassName=" + jdbcDriverClassName
				+ ", jdbcUser=" + jdbcUser + ", jdbcPassword=" + jdbcPassword
				+ ", host=" + host + ", jdbcDriverType=" + jdbcDriverType
				+ ", port=" + port + ", sid=" + sid + ", jdbcConnectionString="
				+ jdbcConnectionString + "]";
	}
	
	
	
}
