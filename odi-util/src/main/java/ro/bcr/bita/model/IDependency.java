package ro.bcr.bita.model;

public interface IDependency<H,O> {
	
	public H who();
	public O on();

}
