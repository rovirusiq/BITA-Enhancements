package ro.bcr.bita.utils;

public class ArchiveException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1763216750054869232L;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ArchiveException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public ArchiveException(String arg0) {
		super(arg0);
	}
	
	

}
