package ro.bcr.bita.sql;

public class BitaSqlException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6172154984600397724L;

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public BitaSqlException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BitaSqlException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public BitaSqlException(String arg0) {
		super(arg0);
	}
	
	

}
