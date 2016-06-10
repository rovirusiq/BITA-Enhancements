package ro.bcr.bita.model;

public class BitaModelException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6704643997609578966L;

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public BitaModelException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BitaModelException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public BitaModelException(String message) {
		super(message);
	}
	
	

}
