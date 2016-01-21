package ro.bcr.bita.smartdeploy

import groovy.mock.interceptor.MockFor
import groovy.mock.interceptor.StubFor
import oracle.odi.core.OdiInstance
import org.junit.Test;

class OdiTemplateTest {
	
	public void testCeva() {
		
		def bld=new MockFor(OdiTemplate.Builder);
		def odiInstance=new StubFor(OdiInstance);
		
		bld.demand.build{new OdiTemplate(odiInstance)}
		
		bld.use{
			OdiTemplate.Builder myBuilder=new OdiTemplate.Builder();
			OdiTemplate tmpl=myBuilder.build();
			println tmpl;
		}
		
		bld.expect.verify();
		
	}

}
