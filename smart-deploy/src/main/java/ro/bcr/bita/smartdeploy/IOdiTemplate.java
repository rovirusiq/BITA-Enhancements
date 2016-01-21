package ro.bcr.bita.smartdeploy;

interface IOdiTemplate {

	public void executeInTransaction(IOdiCommand cmd) throws RuntimeException;
	public void executeWithoutTransaction(IOdiCommand cmd) throws RuntimeException;
	public void cleanUp() throws RuntimeException;
	
	
}
