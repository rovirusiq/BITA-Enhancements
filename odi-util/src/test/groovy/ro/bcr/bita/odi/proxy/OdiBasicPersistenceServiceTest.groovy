package ro.bcr.bita.odi.proxy

import oracle.odi.core.OdiInstance
import oracle.odi.core.persistence.IOdiEntityManager;
import oracle.odi.core.persistence.transaction.ITransactionManager;

import ro.bcr.bita.model.BitaModelFactoryForTesting
import ro.bcr.bita.model.BitaSpockSpecification;

class OdiBasicPersistenceServiceTest extends BitaSpockSpecification {
	
	private OdiBasicPersistenceService subject;
	private OdiInstance mOdiInstance;
	private IOdiEntityManager mEntManager;
	private ITransactionManager mTrnManager;
	
	
	def setup() {
		mOdiInstance=Mock();
		mEntManager=Mock();
		mTrnManager=Mock();
		1 * mOdiInstance.getTransactionManager() >> mTrnManager;
		subject=new OdiBasicPersistenceService(mOdiInstance);
	}
	
	def "Test method clearPersistenceContext for success"(){
		given:	"""The objects from setup"""
		when:	"""The method subject of the test is called"""
				subject.clearPersistenceContext();
		then:	"""The delegation is happening"""
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.clear();
				0 * _;//no other interaction;
	}
	
	def "Test method clearPersistenceContext for error"(){
		given:	"""The objects from setup"""
				
		when:	"""The method subject of the test is called"""
				subject.clearPersistenceContext();
		then:	"""The delegation is happening"""
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.clear() >> {throw new RuntimeException("Testing Exception")};
				0 * _;//no other interaction;
		then:	"""The exception is catched and rethrown"""
				thrown BitaOdiException;
	}
	
	
	def "Test method removeFromPersistence for success"(){
		given:	"""The objects from setup"""
		when:	"""The method subject of the test is called"""
				subject.removeFromPersistenceContext(null);
		then:	"""The delegation is happening"""
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.remove(null);
				0 * _;//no other interaction;
	}
	
	def "Test method removeFromPersistence for error"(){
		given:	"""The objects from setup"""
				
		when:	"""The method subject of the test is called"""
				subject.removeFromPersistenceContext(null);
		then:	"""The delegation is happening"""
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.remove(null) >> {throw new RuntimeException("Testing Exception")};
				0 * _;//no other interaction;
		then:	"""The exception is catched and rethrown"""
				thrown BitaOdiException;
	}
	
	def "Test method persistInRepository for success"(){
		given:	"""The objects from setup"""
		when:	"""The method subject of the test is called"""
				subject.persistInRepository(null);
		then:	"""The delegation is happening"""
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.persist(null);
				0 * _;//no other interaction;
	}
	
	def "Test method persistInRepository for error"(){
		given:	"""The objects from setup"""
				
		when:	"""The method subject of the test is called"""
				subject.persistInRepository(null);
		then:	"""The delegation is happening"""
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.persist(null) >> {throw new RuntimeException("Testing Exception")};
				0 * _;//no other interaction;
		then:	"""The exception is catched and rethrown"""
				thrown BitaOdiException;
	}
	
	
	def "Test method flushPersistenceContext for success"(){
		given:	"""The objects from setup"""
		when:	"""The method subject of the test is called"""
				subject.flushPersistenceContext();
		then:	"""The delegation is happening"""
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.flush();
				0 * _;//no other interaction;
	}
	
	def "Test method flushPersistenceContext for error"(){
		given:	"""The objects from setup"""
				
		when:	"""The method subject of the test is called"""
				subject.flushPersistenceContext();
		then:	"""The delegation is happening"""
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.flush() >> {throw new RuntimeException("Testing Exception")};
				0 * _;//no other interaction;
		then:	"""The exception is catched and rethrown"""
				thrown BitaOdiException;
	}
	
	def "Test method detachFromPersistenceContext for success"(){
		given:	"""The objects from setup"""
		when:	"""The method subject of the test is called"""
				subject.detachFromPersistenceContext(null);
		then:	"""The delegation is happening"""
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.detach(null);
				0 * _;//no other interaction;
	}
	
	def "Test method detachFromPersistenceContext for error"(){
		given:	"""The objects from setup"""
				
		when:	"""The method subject of the test is called"""
				subject.detachFromPersistenceContext(null);
		then:	"""The delegation is happening"""
				1 * mOdiInstance.getTransactionalEntityManager() >> mEntManager;
				1 * mEntManager.detach(null) >> {throw new RuntimeException("Testing Exception")};
				0 * _;//no other interaction;
		then:	"""The exception is catched and rethrown"""
				thrown BitaOdiException;
	}
	
	def "Test method commit for success"(){
		given:	"""The objects from setup"""				
		when:	"""The method subject of the test is called"""
				subject.commitTransaction(null);
		then:	"""The delegation is happening"""
				1 * mTrnManager.commit(null);
				0 * _;//no other interaction;
	}
	
	def "Test method commit for error"(){
		given:	"""The objects from setup"""
		when:	"""The method subject of the test is called"""
				subject.commitTransaction(null);
		then:	"""The delegation is happening"""
				1 * mTrnManager.commit(null) >> {throw new RuntimeException("Testing Exception")};
				0 * _;//no other interaction;
		then:	"""The exception is catched and rethrown"""
				thrown BitaOdiException;
	}
	
	def "Test method rollback for success"(){
		given:	"""The objects from setup"""
		when:	"""The method subject of the test is called"""
				subject.rollbackTransaction(null);
		then:	"""The delegation is happening"""
				1 * mTrnManager.rollback(null);
				0 * _;//no other interaction;
	}
	
	def "Test method rollback for error"(){
		given:	"""The objects from setup"""
		when:	"""The method subject of the test is called"""
				subject.rollbackTransaction(null);
		then:	"""The delegation is happening"""
				1 * mTrnManager.rollback(null) >> {throw new RuntimeException("Testing Exception")};
				0 * _;//no other interaction;
		then:	"""The exception is catched and rethrown"""
				thrown BitaOdiException;
	}

}
