package ro.bcr.bita.service;

public class BitaServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8549990371801028243L;

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public BitaServiceException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BitaServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public BitaServiceException(String message) {
		super(message);
	}
	
	
	

}
