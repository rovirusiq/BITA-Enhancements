package ro.bcr.bita.server.service;



public class OdiScenarioExecutionResult {
	
	public String odiSessionNumber="";
	public String code="";
	public String message="";
	/**
	 * @return the odiSessionNumber
	 */
	public String getOdiSessionNumber() {
		return odiSessionNumber;
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
	 * @param odiSessionNumber the odiSessionNumber to set
	 */
	public void setOdiSessionNumber(String odiSessionNumber) {
		this.odiSessionNumber = odiSessionNumber;
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
