package ro.bcr.bita.model

import groovy.lang.Delegate;

import java.util.HashMap;

import oracle.odi.domain.runtime.scenario.OdiScenario as RealOdiScenario;
import oracle.odi.domain.runtime.scenario.OdiScenarioVariable;

class OdiScenario implements IOdiScenario {	
	
	private @Delegate RealOdiScenario odiObject;
	
	
	protected OdiScenario(oracle.odi.domain.runtime.scenario.OdiScenario odiObj ) {
		this.odiObject=odiObj;
	}
	

	@Override
	public List<String> getParameterList() {
		Collection c=this.odiObject.getScenarioVariables();
		if (c==null) return [];
		
		return c.collect{OdiScenarioVariable v-> v.name}
	}

}
