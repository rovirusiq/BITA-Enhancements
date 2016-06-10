package ro.bcr.bita.odi.template;

public interface IOdiBasicTemplate {
	
	public void executeInTransaction(IOdiBasicCommand cmd) throws RuntimeException;
	public void executeWithoutTransaction(IOdiBasicCommand cmd) throws RuntimeException;

}
