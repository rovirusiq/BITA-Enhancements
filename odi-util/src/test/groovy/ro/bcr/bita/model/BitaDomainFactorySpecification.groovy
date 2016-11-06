package ro.bcr.bita.model

import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.template.IOdiCommandContext;

import spock.lang.Specification

class BitaDomainFactorySpecification extends Specification {
	
	
	private IBitaDomainFactory subject;
	private IOdiEntityFactory stbOdiFactory;
	
	def setup() {
		stbOdiFactory=Stub();
		subject=BitaDomainFactory.newInstance(
			BitaModelFactory.newInstance()
			,stbOdiFactory
			);
	}
	
	def "Test method newOdiTemplateCommandContext for success"(){
		given:	"""The objects in the setup method and the mocked from below"""
		when:	"The method under the test is called"
				IOdiCommandContext rsp=subject.newOdiTemplateCommandContext();
		then:	"The rsp is of correct type"
				rsp instanceof IOdiCommandContext;
				rsp.inTransaction()==false;
				rsp.getTransactionStatus()==null;
	}

}
