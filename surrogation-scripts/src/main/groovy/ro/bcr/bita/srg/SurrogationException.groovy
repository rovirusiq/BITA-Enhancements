package ro.bcr.bita.srg

class SurrogationException extends RuntimeException {

	/************************************************************************************************************
	 *
	 *Constructors
	 *
	 ************************************************************************************************************/	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public SurrogationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public SurrogationException(String arg0) {
		super(arg0);
	}
	
	
}
