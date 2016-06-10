package ro.bcr.bita.odi.template;

import ro.bcr.bita.odi.template.IOdiBasicCommand;
import ro.bcr.bita.odi.template.IOdiCommandContext;
import ro.bcr.bita.odi.template.OdiBasicTemplate;
import ro.bcr.bita.proxy.odi.IOdiEntityFactory
import ro.bcr.bita.proxy.odi.OdiEntityFactory;

import oracle.odi.core.OdiInstance
import oracle.odi.core.persistence.transaction.ITransactionDefinition;
import oracle.odi.core.persistence.transaction.ITransactionManager
import oracle.odi.core.persistence.transaction.ITransactionStatus
import spock.lang.Specification;

class OdiBasicTemplateTest extends Specification{
	
	def IOdiEntityFactory mOdiEntityFactory;
	def ITransactionManager mTxManager;
	def ITransactionStatus mTxStatus;
	def ITransactionDefinition mTxDefinition;
	def IOdiBasicCommand mCommand;
	
	def setup() {
		mOdiEntityFactory=Mock();
		mTxManager=Mock();
		mTxStatus=Mock();
		mCommand=Mock();
	}
	
	def "Test Interaction of OdiBasicTemplate in transaction"(){	
		
		given: """A bunch of objects need to create an OdiTemplate.
				The objects are created in the setup method"""
			
			
		when:  "a command is executed without Odi Transaction"
			OdiBasicTemplate odiTmpl=new OdiBasicTemplate(mOdiEntityFactory);
			odiTmpl.executeInTransaction(mCommand);
			
		then: "no trasaction related interactions, only the execute method on the command object"
			1 * mOdiEntityFactory.createDefaultTransactionDefinition() >> mTxDefinition;
			1 * mOdiEntityFactory.getTransactionManager() >> mTxManager;
			1 * mOdiEntityFactory.createTransactionStatus(mTxManager,mTxDefinition) >> mTxStatus;
			1 * mCommand.execute(_ as IOdiCommandContext);
			1 * mTxManager.commit(mTxStatus);
			0 * _;//no other interactions for any mockup
	}
	
	
	def "Test Interaction of OdiBasicTemplate without transaction"(){
		
		given: """A bunch of objects need to create an OdiTemplate.
				The objects are created in the setup method"""
			
		when: "A command is executed without Odi Transaction"
			OdiBasicTemplate odiTmpl=new OdiBasicTemplate(mOdiEntityFactory);
			odiTmpl.executeWithoutTransaction(mCommand);
			
		then: "no trasaction related interactions, only the execute method on the command object"
			1 * mCommand.execute(_ as IOdiCommandContext);
			0 * _;//no other interactions
	}
	
	
	def "when constructor parameter odiEntityFactory is null, an OdiTemplateException is thrown"(){
		given:
			OdiBasicTemplate odiTmpl;
		when: 'try to construct the OdiTemplate object'
			odiTmpl=new OdiBasicTemplate(null);
		then: "OdiTemplateException is expected"
			thrown OdiTemplateException;
			
			
	}
	
	def "when one needs the OdiInstance it can obtain it using a dirty trick"(){
		given: """A bunch of objects need to create an OdiTemplate.
				The objects are created in the setup method"""
			def contextReceived;
		when: "The OdiTemplate is build and you execute a command"
			contextReceived=null;
			OdiBasicTemplate odiTmpl=new OdiBasicTemplate(mOdiEntityFactory);
			odiTmpl.executeWithoutTransaction(mCommand);
		then: "In the command you have acces to the OdiComandContext"
			1 * mCommand.execute(_ as IOdiCommandContext) >> {arg-> contextReceived=arg[0];};
		then: "Which in turn gives you access to the OdiInstance"
			contextReceived instanceof OdiCommandContext;
			IOdiEntityFactory f=((OdiCommandContext)contextReceived).getOdiEntityFactory();
			f==mOdiEntityFactory;
	}
}
