package ro.bcr.bita.server.config

import groovy.transform.CompileStatic;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "bita.odi")
@CompileStatic
class BitaOdiConfig {
	
	private String agentUrl;
	private String username;
	private char[] password;
	private String context;
	private Integer logLevel;
	private String workRepositoryName;
	/**
	 * @return the agentUrl
	 */
	public String getAgentUrl() {
		return agentUrl;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @return the password
	 */
	public char[] getPassword() {
		return password;
	}
	/**
	 * @return the context
	 */
	public String getContext() {
		return context;
	}
	/**
	 * @return the logLevel
	 */
	public Integer getLogLevel() {
		return logLevel;
	}
	/**
	 * @return the workRepositoryName
	 */
	public String getWorkRepositoryName() {
		return workRepositoryName;
	}
	/**
	 * @param agentUrl the agentUrl to set
	 */
	public void setAgentUrl(String agentUrl) {
		this.agentUrl = agentUrl;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(char[] password) {
		this.password = password;
	}
	/**
	 * @param context the context to set
	 */
	public void setContext(String context) {
		this.context = context;
	}
	/**
	 * @param logLevel the logLevel to set
	 */
	public void setLogLevel(Integer logLevel) {
		this.logLevel = logLevel;
	}
	/**
	 * @param workRepositoryName the workRepositoryName to set
	 */
	public void setWorkRepositoryName(String workRepositoryName) {
		this.workRepositoryName = workRepositoryName;
	}
	
	
	
	
	
	

}
