package ro.bcr.bita.proxy.odi;

public class BitaOdiException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7771380556933610794L;

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public BitaOdiException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BitaOdiException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public BitaOdiException(String arg0) {
		super(arg0);
	}
	
	

}
