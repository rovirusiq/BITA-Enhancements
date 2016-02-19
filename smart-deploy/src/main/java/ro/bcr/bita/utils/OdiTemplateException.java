package ro.bcr.bita.utils;

public class OdiTemplateException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -384008711662412320L;

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
