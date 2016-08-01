package ro.bcr.bita.odi.proxy

import ro.bcr.bita.model.BitaModelFactoryForTesting;
import ro.bcr.bita.model.IBitaModelFactory;
import ro.bcr.bita.odi.proxy.OdiEntityFactory.OdiInstanceProperties;
import ro.bcr.bita.odi.template.IOdiCommandContext;

import java.awt.image.renderable.ContextualRenderedImageFactory;

import spock.lang.Specification
import oracle.odi.core.OdiInstance
import oracle.odi.core.persistence.IOdiEntityManager;
import oracle.odi.core.persistence.transaction.ITransactionManager;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.domain.finder.IFinder;
import oracle.odi.domain.mapping.Mapping;
import oracle.odi.domain.mapping.finder.IMappingFinder;
import oracle.odi.domain.project.OdiFolder;
import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.runtime.scenario.OdiScenario;
import oracle.odi.domain.runtime.scenario.OdiScenarioFolder;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFolderFinder;
import oracle.odi.domain.topology.OdiContext;
import oracle.odi.domain.topology.OdiDataServer;
import oracle.odi.domain.topology.OdiLogicalSchema;
import oracle.odi.domain.topology.OdiPhysicalSchema;
import oracle.odi.domain.topology.finder.IOdiContextFinder;
import oracle.odi.domain.topology.finder.IOdiDataServerFinder;
import oracle.odi.domain.topology.finder.IOdiLogicalSchemaFinder;
import oracle.odi.domain.topology.finder.IOdiPhysicalSchemaFinder;
import oracle.odi.domain.topology.finder.toplink.OdiDataServerFinderImpl;

class OdiEntityFactoryTest extends Specification{
	
	def OdiInstance mOdiInstance;
	def OdiEntityFactory subject;
	def IOdiEntityManager mEntManager;
	def ITransactionManager mTrnManager;
	def IBitaModelFactory bitaModelFactory;
	
	def setup() {
		mOdiInstance=Mock();
		mEntManager=Mock();
		mTrnManager=Mock();
		this.bitaModelFactory=BitaModelFactoryForTesting.newInstance();
		subject=OdiEntityFactory.createInstance(mOdiInstance,this.bitaModelFactory);
	}
	
	def "Test interaction with supplied odiInstance when getting access to it"(){
		given:	"""The objects in the setup method"""
		when:	"The factory is used to retrieve the odiInstance"
				def cici=subject.getOdiInstance();
		then:	"Then the mocked object is retuned"
				cici==mOdiInstance;
	}
	
	def "when the OdiInstance costructor argument is null, then an exception is thrown"(){
		given:	"""The objects in the setup method"""
		when:	"The OdiEntityFactory is instantiaded with null for OdiInstance parameter"
				subject=OdiEntityFactory.createInstance(null,this.bitaModelFactory);
		then:	"An exception is thrown"
				thrown BitaOdiException;
	}
	
	def "when the OdiInstance costructor argument is null to the method createInstanceFromProperties, then an exception is thrown"(){
		given:	"""The objects in the setup method"""
		when:	"The OdiEntityFactory is instantiaded with null for OdiInstance parameter"
				subject=OdiEntityFactory.createInstanceFromProperties(null,this.bitaModelFactory);
		then:	"An exception is thrown"
				thrown BitaOdiException;
	}
	
	def "when the BitaModelFactory costructor argument is null, then an exception is thrown"(){
		given:	"""The objects in the setup method"""
		when:	"The OdiEntityFactory is instantiaded with null for OdiInstance parameter"
				subject=OdiEntityFactory.createInstance(mOdiInstance,null);
		then:	"An exception is thrown"
				thrown BitaOdiException;
	}
	
	def "when the BitaModelFactory costructor argument is null to the method createInstanceFromProperties, then an exception is thrown"(){
		given:	"""The objects in the setup method"""
		when:	"The OdiEntityFactory is instantiaded with null for OdiInstance parameter"
				def OdiInstanceProperties prop=new OdiEntityFactory.OdiInstanceProperties();
				subject=OdiEntityFactory.createInstanceFromProperties(prop,null);
		then:	"An exception is thrown"
				thrown BitaOdiException;
	}
	
	def "when the OdiInstanceProperties constructor has one property not set, then an exception is thrown"(){
		given:	"""The objects in the setup method"""
		when:	"The OdiEntityFactory is instantiaded with null for OdiInstance parameter"
				def OdiInstanceProperties prop=new OdiEntityFactory.OdiInstanceProperties();
				prop.setMasterRepositoryUsername("cutu");
				subject=OdiEntityFactory.createInstanceFromProperties(prop,this.bitaModelFactory);
		then:	"An exception is thrown"
				thrown BitaOdiException;
	}
	
	def "Test method createContextFinder for success"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiContextFinder mockResponse=Mock();
		when:	"The method under the test is called"
				def rsp=subject.createContextFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.getFinder(OdiContext.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"The rsp equlas the mock return value"
				rsp==mockResponse;
	}
	
	def "Test method createContextFinder for error"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiContextFinder mockResponse=Mock();
		when:	"The method under the test is called and an exception is thrown by the ODI infrastructure"
				def rsp=subject.createContextFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> {throw new RuntimeException("Testing Exception")};
				0 * mEntManager.getFinder(OdiContext.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"the exception is catched and rethrown"
				thrown BitaOdiException;
	}
	                     
	def "Test method createDataServerFinder for success"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiDataServerFinder mockResponse=Mock();
		when:	"The method under the test is called"
				def rsp=subject.createDataServerFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.getFinder(OdiDataServer.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"The rsp equlas the mock return value"
				rsp==mockResponse;
	}
	
	def "Test method createDataServerFinder for error"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiDataServerFinder mockResponse=Mock();
		when:	"The method under the test is called and an exception is thrown by the ODI infrastructure"
				def rsp=subject.createDataServerFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> {throw new RuntimeException("Testing Exception")};
				0 * mEntManager.getFinder(OdiDataServer.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"the exception is catched and rethrown"
				thrown BitaOdiException;
	}
	
	def "Test method createLogicalSchemaFinder for success"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiLogicalSchemaFinder mockResponse=Mock();
		when:	"The method under the test is called"
				def rsp=subject.createLogicalSchemaFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.getFinder(OdiLogicalSchema.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"The rsp equlas the mock return value"
				rsp==mockResponse;
	}
	
	def "Test method createLogicalSchemaFinder for error"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiLogicalSchemaFinder mockResponse=Mock();
		when:	"The method under the test is called and an exception is thrown by the ODI infrastructure"
				def rsp=subject.createLogicalSchemaFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> {throw new RuntimeException("Testing Exception")};
				0 * mEntManager.getFinder(OdiLogicalSchema.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"the exception is catched and rethrown"
				thrown BitaOdiException;
	}
	
	
	def "Test method createMappingFinder for success"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IMappingFinder mockResponse=Mock();
		when:	"The method under the test is called"
				def rsp=subject.createMappingFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.getFinder(Mapping.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"The rsp equlas the mock return value"
				rsp==mockResponse;
	}
	
	def "Test method createMappingFinder for error"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IMappingFinder mockResponse=Mock();
		when:	"The method under the test is called and an exception is thrown by the ODI infrastructure"
				def rsp=subject.createMappingFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> {throw new RuntimeException("Testing Exception")};
				0 * mEntManager.getFinder(Mapping.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"the exception is catched and rethrown"
				thrown BitaOdiException;
	}
	
	def "Test method createPhysicalSchemaFinder for success"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiPhysicalSchemaFinder mockResponse=Mock();
		when:	"The method under the test is called"
				def rsp=subject.createPhysicalSchemaFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.getFinder(OdiPhysicalSchema.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"The rsp equlas the mock return value"
				rsp==mockResponse;
	}
	
	def "Test method createPhysicalSchemaFinder for error"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiPhysicalSchemaFinder mockResponse=Mock();
		when:	"The method under the test is called and an exception is thrown by the ODI infrastructure"
				def rsp=subject.createPhysicalSchemaFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> {throw new RuntimeException("Testing Exception")};
				0 * mEntManager.getFinder(OdiPhysicalSchema.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"the exception is catched and rethrown"
				thrown BitaOdiException;
	}
	
	def "Test method createProjectFinder for success"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiProjectFinder mockResponse=Mock();
		when:	"The method under the test is called"
				def rsp=subject.createProjectFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.getFinder(OdiProject.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"The rsp equlas the mock return value"
				rsp==mockResponse;
	}
	
	def "Test method createProjectFinder for error"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiProjectFinder mockResponse=Mock();
		when:	"The method under the test is called and an exception is thrown by the ODI infrastructure"
				def rsp=subject.createProjectFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> {throw new RuntimeException("Testing Exception")};
				0 * mEntManager.getFinder(OdiProject.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"the exception is catched and rethrown"
				thrown BitaOdiException;
	}
	
	def "Test method createProjectFolderFinder for success"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiFolderFinder mockResponse=Mock();
		when:	"The method under the test is called"
				def rsp=subject.createProjectFolderFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.getFinder(OdiFolder.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"The rsp equlas the mock return value"
				rsp==mockResponse;
	}
	
	def "Test method createProjectFolderFinder for error"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiFolderFinder mockResponse=Mock();
		when:	"The method under the test is called and an exception is thrown by the ODI infrastructure"
				def rsp=subject.createProjectFolderFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> {throw new RuntimeException("Testing Exception")};
				0 * mEntManager.getFinder(OdiFolder.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"the exception is catched and rethrown"
				thrown BitaOdiException;
	}
	
	def "Test method createScenarioFinder for success"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiScenarioFinder mockResponse=Mock();
		when:	"The method under the test is called"
				def rsp=subject.createScenarioFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.getFinder(OdiScenario.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"The rsp equlas the mock return value"
				rsp==mockResponse;
	}
	
	def "Test method createScenarioFinder for error"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiScenarioFinder mockResponse=Mock();
		when:	"The method under the test is called and an exception is thrown by the ODI infrastructure"
				def rsp=subject.createScenarioFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> {throw new RuntimeException("Testing Exception")};
				0 * mEntManager.getFinder(OdiScenario.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"the exception is catched and rethrown"
				thrown BitaOdiException;
	}
	
	def "Test method createScenarioFolderFinder for success"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiScenarioFolderFinder mockResponse=Mock();
		when:	"The method under the test is called"
				def rsp=subject.createScenarioFolderFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.getFinder(OdiScenarioFolder.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"The rsp equlas the mock return value"
				rsp==mockResponse;
	}
	
	def "Test method createScenarioFolderFinder for error"(){
		given:	"""The objects in the setup method and the mocked from below"""
				IOdiScenarioFolderFinder mockResponse=Mock();
		when:	"The method under the test is called and an exception is thrown by the ODI infrastructure"
				def rsp=subject.createScenarioFolderFinder();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionalEntityManager() >> {throw new RuntimeException("Testing Exception")};
				0 * mEntManager.getFinder(OdiScenarioFolder.class) >> mockResponse;
				0 * _;//no other interactions
		then:	"the exception is catched and rethrown"
				thrown BitaOdiException;
	}
	
	def "Test method createTransaction for success"(){
		given:	"""The objects in the setup method and the mocked from below"""
				ITransactionStatus mockResponse=Mock();
		when:	"The method under the test is called"
				def rsp=subject.createTransaction();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionManager() >> mTrnManager;
				1 * mTrnManager.getTransaction(_) >> mockResponse;
				0 * _;//no other interactions
		then:	"The rsp equlas the mock return value"
				rsp==mockResponse;
	}
	
	def "Test method createTransaction for error"(){
		given:	"""The objects in the setup method and the mocked from below"""
				ITransactionStatus mockResponse=Mock();
		when:	"The method under the test is called and an exception is thrown by the ODI infrastructure"
				def rsp=subject.createTransaction();
		then:	"The mock object is used correctly behind the scenes"
				1 * mOdiInstance.getTransactionManager() >> {throw new RuntimeException("Testing Exception")};
				0 * mTrnManager.getTransaction(_) >> mockResponse;
				0 * _;//no other interactions
		then:	"the exception is catched and rethrown"
				thrown BitaOdiException;
	}
	
	def "Test method newOdiPathUtil for success"(){
		given:	"""The objects in the setup method and the mocked from below"""
		when:	"The method under the test is called"
				OdiPathUtil rsp=subject.newOdiPathUtil();
		then:	"The rsp is of correct type"
				rsp instanceof OdiPathUtil;
	}
	
	def "Test method newOdiProjectPaths for success"(){
		given:	"""The objects in the setup method and the mocked from below"""
		when:	"The method under the test is called"
				OdiProjectPaths rsp=subject.newOdiProjectPaths([:]);
		then:	"The rsp is of correct type"
				rsp instanceof OdiProjectPaths;
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
