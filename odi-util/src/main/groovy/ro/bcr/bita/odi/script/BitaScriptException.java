package ro.bcr.bita.odi.script;

public class BitaScriptException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -454258883032300262L;

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public BitaScriptException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BitaScriptException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public BitaScriptException(String arg0) {
		super(arg0);
	}

}
