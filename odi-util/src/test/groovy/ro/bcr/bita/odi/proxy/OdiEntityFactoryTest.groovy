package ro.bcr.bita.odi.proxy

import ro.bcr.bita.odi.proxy.OdiEntityFactory.OdiInstanceProperties;

import spock.lang.Specification
import oracle.odi.core.OdiInstance

class OdiEntityFactoryTest extends Specification{
	
	def OdiInstance mOdiInstance;
	def OdiEntityFactory subject;
	
	def setup() {
		mOdiInstance=Mock();
	}
	
	def "Test interaction with supplied odiInstance when getting access to it"(){
		given:	"""The objects in the setup method"""
		when:	"The OdiEntityFactory is instantiaded with the mocked object"
				subject=OdiEntityFactory.createInstance(mOdiInstance);
		then:	"The factory is used to retrieve the odiInstance"
				def cici=subject.getOdiInstance();
		then:	"Then the mocked object is retuned"
				cici==mOdiInstance;
	}
	
	def "Test interaction with supplied odiInstance when creating ODI objects"(){
		given:	"""The objects in the setup method"""
		when:	"The OdiEntityFactory is instantiaded with the mocked object and used to create an ODI object"
				subject=OdiEntityFactory.createInstance(mOdiInstance);
				subject.getTransactionManager();
		then:	"The mock object is used behind the scenes"
				1 * mOdiInstance.getTransactionManager();
				0 * _;//no other interactions
	}
	
	
	def "when the OdiInstance costructor argument is null, then an exception is thrown"(){
		given:	"""The objects in the setup method"""
		when:	"The OdiEntityFactory is instantiaded with null for OdiInstance parameter"
				subject=OdiEntityFactory.createInstance(null);
		then:	"An exception is thrown"
				thrown BitaOdiException;
	}
	
	def "when the OdiInstanceProperties costructor has one property not set, then an exception is thrown"(){
		given:	"""The objects in the setup method"""
		when:	"The OdiEntityFactory is instantiaded with null for OdiInstance parameter"
				def OdiInstanceProperties prop=new OdiEntityFactory.OdiInstanceProperties();
				prop.setMasterRepositoryUsername("cutu");
				subject=OdiEntityFactory.createInstance(prop);
		then:	"An exception is thrown"
				thrown BitaOdiException;
	}

}
