package ro.bcr.bita.utils

import ro.bcr.bita.utils.OdiTemplate;

import groovy.mock.interceptor.MockFor
import groovy.mock.interceptor.StubFor
import oracle.odi.core.OdiInstance

import org.junit.Test;

class OdiTemplateTest {
	
	//@Test
	public void testCeva() {
		
		MockFor forBuilder=new MockFor(OdiTemplate.Builder);
		StubFor forOdiInstance=new StubFor(OdiInstance);
		
		OdiInstance odiInstance=forOdiInstance.proxyInstance();
		
		forBuilder.demand.build{new OdiTemplate(odiInstance)}
		
		OdiTemplate.Builder builder=forBuilder.proxyInstance();
		
		OdiTemplate tmpl=builder.build();
		
		forBuilder.verify builder
		
	}

}
