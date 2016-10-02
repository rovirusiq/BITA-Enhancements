package ro.bcr.bita.odi.proxy

class OdiScenExecutionEnv {
	
	private String agentUrl;
	private String odiUsername;
	private char[] odiPassword;
	private String odiContext;
	private Integer odiLogLevel;
	private String odiWorkRepName;
	
	
	
	
	/**
	 * 
	 */
	private OdiScenExecutionEnv() {
		super();
	}
	
	
	/**
	 * @param agentUrl
	 * @param odiUsername
	 * @param odiPassword
	 * @param odiContext
	 * @param odiLogLevel
	 * @param odiWorkRepName
	 */
	public OdiScenExecutionEnv(String agentUrl, String odiUsername,
			char[] odiPassword, String odiContext, Integer odiLogLevel,
			String odiWorkRepName) {
		super();
		this.agentUrl = agentUrl;
		this.odiUsername = odiUsername;
		this.odiPassword = odiPassword;
		this.odiContext = odiContext;
		this.odiLogLevel = odiLogLevel;
		this.odiWorkRepName = odiWorkRepName;
	}


	/**
	 * @return the agentUrl
	 */
	public String getAgentUrl() {
		return agentUrl;
	}
	/**
	 * @return the odiUsername
	 */
	public String getOdiUsername() {
		return odiUsername;
	}
	/**
	 * @return the odiPassword
	 */
	public char[] getOdiPassword() {
		return odiPassword;
	}
	/**
	 * @return the odiContext
	 */
	public String getOdiContext() {
		return odiContext;
	}
	/**
	 * @return the odiLogLevel
	 */
	public Integer getOdiLogLevel() {
		return odiLogLevel;
	}
	/**
	 * @param agentUrl the agentUrl to set
	 */
	public void setAgentUrl(String agentUrl) {
		this.agentUrl = agentUrl;
	}
	/**
	 * @param odiUsername the odiUsername to set
	 */
	public void setOdiUsername(String odiUsername) {
		this.odiUsername = odiUsername;
	}
	/**
	 * @param odiPassword the odiPassword to set
	 */
	public void setOdiPassword(char[] odiPassword) {
		this.odiPassword = odiPassword;
	}
	/**
	 * @param odiContext the odiContext to set
	 */
	public void setOdiContext(String odiContext) {
		this.odiContext = odiContext;
	}
	/**
	 * @param odiLogLevel the odiLogLevel to set
	 */
	public void setOdiLogLevel(Integer odiLogLevel) {
		this.odiLogLevel = odiLogLevel;
	}
	/**
	 * @return the odiWorkRepName
	 */
	public String getOdiWorkRepName() {
		return odiWorkRepName;
	}
	/**
	 * @param odiWorkRepName the odiWorkRepName to set
	 */
	public void setOdiWorkRepName(String odiWorkRepName) {
		this.odiWorkRepName = odiWorkRepName;
	}
	
	
	
	

}
