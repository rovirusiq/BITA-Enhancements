package ro.bcr.bita.odi.template;

import ro.bcr.bita.model.BitaModelFactoryForTesting;
import ro.bcr.bita.model.IBitaModelFactory;
import ro.bcr.bita.odi.proxy.IOdiBasicPersistenceService;
import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.proxy.OdiEntityFactory;

import spock.lang.Ignore;
import spock.lang.Specification
import oracle.odi.core.persistence.transaction.ITransactionDefinition
import oracle.odi.core.persistence.transaction.ITransactionManager
import oracle.odi.core.persistence.transaction.ITransactionStatus

class OdiBasicTemplateTest extends Specification{
	
	IOdiEntityFactory mOdiEntityFactory;
	ITransactionStatus mTxStatus;
	IOdiBasicCommand mCommand;
	IOdiCommandContext mCtx;
	
	def setup() {
		mOdiEntityFactory=Mock();
		mTxStatus=Mock();
		mCommand=Mock();
		mCtx=Mock();
	}
	
	def "Test Interaction of OdiBasicTemplate in transaction"(){	
		
		given: """A bunch of objects need to create an OdiTemplate.
				The objects are created in the setup method"""
			
			
		when:  "a command is executed in Odi Transaction"
			OdiBasicTemplate odiTmpl=new OdiBasicTemplate(mOdiEntityFactory);
			odiTmpl.executeInTransaction(mCommand);
			
		then: "the interaction are as follows"
			1 * mOdiEntityFactory.createTransaction() >> mTxStatus;
			1 * mOdiEntityFactory.newOdiTemplateCommandContext() >> mCtx;
			1 * mCommand.execute(mCtx);
			1 * mCtx.setTransactionStatus(mTxStatus);
			1 * mCtx.setInTransaction(true);
			1 * mCtx.commitTransaction(mTxStatus);
			0 * _;//no other interactions for any mockup
	}
	
	
	def "Test Interaction of OdiBasicTemplate without transaction"(){
		
		given: """A bunch of objects need to create an OdiTemplate.
				The objects are created in the setup method"""
			
		when: "A command is executed without Odi Transaction"
			OdiBasicTemplate odiTmpl=new OdiBasicTemplate(mOdiEntityFactory);
			odiTmpl.executeWithoutTransaction(mCommand);
			
		then: "the interactions are followed. In the backround a transaciton is opened but also clear for the persistence context is called before commit"
			1 * mOdiEntityFactory.createTransaction() >> mTxStatus;
			1 * mOdiEntityFactory.newOdiTemplateCommandContext() >> mCtx;
			1 * mCommand.execute(mCtx);
			1 * mCtx.setTransactionStatus(mTxStatus);
			1 * mCtx.setInTransaction(false);
			1 * mCtx.clearPersistenceContext();
			1 * mCtx.commitTransaction(mTxStatus);
			0 * _;//no other interactions for any mockup
	}
	
	
	def "when constructor parameter odiEntityFactory is null, an OdiTemplateException is thrown"(){
		given:
			OdiBasicTemplate odiTmpl;
		when: 'try to construct the OdiTemplate object'
			odiTmpl=new OdiBasicTemplate(null);
		then: "OdiTemplateException is expected"
			thrown OdiTemplateException;
			
			
	}
	
	@Ignore
	def "when one needs the OdiInstance it can obtain it using a dirty trick"(){
		given: """A bunch of objects need to create an OdiTemplate.
				The objects are created in the setup method"""
				def contextReceived;
				OdiCommandContext mFullContext=Mock();
				OdiEntityFactory mFullOdiFactory=Mock();
				
		when:	"The OdiTemplate is build and you execute a command"
				contextReceived=null;
				OdiBasicTemplate odiTmpl=new OdiBasicTemplate(mFullOdiFactory);
				odiTmpl.executeWithoutTransaction(mCommand);
		then:	"In the command you have acces to the OdiComandContext"
				mFullContext.getOdiEntityFactory() >> mFullOdiFactory;
				mFullOdiFactory.newOdiTemplateCommandContext() >> mFullContext;
				mCommand.execute(_ as IOdiCommandContext) >> {arg-> contextReceived=arg[0];};
		then:	"Which in turn gives you access to the OdiInstance"
				contextReceived instanceof OdiCommandContext;
				contextReceived==mFullContext;
				println mFullContext.getOdiEntityFactory();
				println mFullOdiFactory;
				OdiEntityFactory f=((OdiCommandContext)contextReceived).getOdiEntityFactory();
				f==mFullOdiFactory;
	}
}
