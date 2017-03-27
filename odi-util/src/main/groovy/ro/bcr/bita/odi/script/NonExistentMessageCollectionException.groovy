package ro.bcr.bita.odi.script

import groovy.transform.CompileStatic

@CompileStatic
public class NonExistentMessageCollectionException extends IllegalArgumentException {

	/**
	 * @param arg0
	 */
	public NonExistentMessageCollectionException(String idCollection) {
		super("You are trying to use a MessageCollection[$idCollection] that was not created. You must explicitly create a MessageCollection.");
	}
	
	

}
