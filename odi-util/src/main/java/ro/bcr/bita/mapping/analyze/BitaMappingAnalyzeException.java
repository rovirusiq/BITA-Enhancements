package ro.bcr.bita.mapping.analyze;

public class BitaMappingAnalyzeException extends RuntimeException {

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
	public BitaMappingAnalyzeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BitaMappingAnalyzeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public BitaMappingAnalyzeException(String message) {
		super(message);
	}
	
	
	

}
