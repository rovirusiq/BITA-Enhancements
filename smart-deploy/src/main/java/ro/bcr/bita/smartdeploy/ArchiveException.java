package ro.bcr.bita.smartdeploy;

public class ArchiveException extends RuntimeException {

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
