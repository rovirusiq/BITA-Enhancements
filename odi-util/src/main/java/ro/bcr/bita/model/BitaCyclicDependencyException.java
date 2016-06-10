package ro.bcr.bita.model;

import java.util.List;

public class BitaCyclicDependencyException extends BitaModelException {

	private List<IDependency<? extends Object,? extends Object>> listOfCyclicDependencies;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7696966077496395562L;
	
	
	public BitaCyclicDependencyException(String message,List<IDependency<? extends Object,? extends Object>> listOfCyclicDependencies) {
		super(message);
		this.listOfCyclicDependencies=listOfCyclicDependencies;
	}
	
	
	public List<IDependency<? extends Object,? extends Object>> getCyclicDependencies(){
		return listOfCyclicDependencies;
	}


}
