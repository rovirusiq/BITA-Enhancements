package ro.bcr.bita.odi.proxy

class OdiScenExecutionStatus {
	
	private String sessionNumber;
	private String code;
	private String message;
	
	
	
	/**
	 * 
	 */
	private OdiScenExecutionStatus() {
		super();
	}
	/**
	 * @param sessionNumber
	 * @param code
	 * @param message
	 */
	private OdiScenExecutionStatus(String sessionNumber, String code,
			String message) {
		super();
		this.sessionNumber = sessionNumber;
		this.code = code;
		this.message = message;
	}
	/**
	 * @return the sessionNumber
	 */
	public String getSessionNumber() {
		return sessionNumber;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param sessionNumber the sessionNumber to set
	 */
	public void setSessionNumber(String sessionNumber) {
		this.sessionNumber = sessionNumber;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	

}
