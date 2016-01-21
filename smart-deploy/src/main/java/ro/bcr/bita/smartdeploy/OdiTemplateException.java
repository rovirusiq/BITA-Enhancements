package ro.bcr.bita.smartdeploy;

public class OdiTemplateException extends RuntimeException {

	/**
	 * @param message
	 * @param cause
	 */
	public OdiTemplateException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public OdiTemplateException(String message) {
		super(message);
	}
	
	

}
