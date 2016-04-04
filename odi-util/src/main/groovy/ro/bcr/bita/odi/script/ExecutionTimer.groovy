package ro.bcr.bita.odi.script

import java.util.concurrent.atomic.AtomicInteger

//TODO sa implementez functionalitatea de a stoca timerele intr-o colectie.
//TODO sa poti sa faci retrieve dupa aceea din acea colecite si eventual rapoarte
//TODO momentan nu trebuie sa suport mult threading
class ExecutionTimer {
	
	private long startTime;
	private long endTime;
	private String name="Timer";
	private static volatile AtomicInteger nameContor=new AtomicInteger(0);
	/**
	 * @param name
	 */
	public ExecutionTimer(String name) {
		super();
		this.name = name;
	}
	
	public ExecutionTimer() {
		super();
		this.name = name;
	}
	
	
	

}
