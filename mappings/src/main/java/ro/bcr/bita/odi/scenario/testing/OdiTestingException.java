package ro.bcr.bita.odi.scenario.testing;

public class OdiTestingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4274206827748353568L;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public OdiTestingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public OdiTestingException(String arg0) {
		super(arg0);
	}
	
	

}
