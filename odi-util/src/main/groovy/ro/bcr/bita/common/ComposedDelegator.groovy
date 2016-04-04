package ro.bcr.bita.common

import java.util.List;

import groovy.transform.TupleConstructor


class ComposedDelegator {
	
	private List<Object> listToDelegateTo=[];
	
	/**
	 * @param listToDelegateTo
	 */
	public ComposedDelegator(List<Object> listOfObjects) {
		super();
		this.listToDelegateTo.addAll(listOfObjects);
	}
	

	def methodMissing(String name, args) {
		for (d in listToDelegateTo) {
			if(d.respondsTo(name)) {
				return d."$name"(*args)
			}
		}
		throw new MissingMethodException(name, ComposedDelegator, args)
	}

	def propertyMissing(String name) {
		for (d in listToDelegateTo) {
			if(d.hasProperty(name)) {
				return d."$name"
			} else if ( (d instanceof Map) && ( d.containsKey(name) )){
				return d["$name"];
			} else 	if (d.respondsTo("propertyMissing")) {
			/*for DSL it is important to have also a check on the propertyMissingMethod*/
				try {
					return d.propertyMissing(name);
				} catch (MissingPropertyException | MissingFieldException ex) {
					//we are ignoring it because we will throw it further away
				}
			}
		}
		throw new MissingPropertyException(name, ComposedDelegator);
	}
	
	def propertyMissing(String name,value) {
		for (d in listToDelegateTo) {
			if(d.hasProperty(name)) {
				d."$name"=value;
				return;
			} else if ( (d instanceof Map) && ( d.containsKey(name) )){
				return d["$name"]=value;
			} else 	if (d.respondsTo("propertyMissing")) {
				try {
					return d.propertyMissing(name,value);
				} catch (MissingPropertyException | MissingFieldException ex) {
					//we are ignoring it because we will throw it further away
				}
			}
		}
		throw new MissingPropertyException(name, ComposedDelegator);
	}

	String toString() {
		"ComposedDelegator(${listToDelegateTo})";
	}

}
