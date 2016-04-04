package ro.bcr.bita.odi.template;

import ro.bcr.bita.odi.template.IOdiBasicCommand;
import ro.bcr.bita.odi.template.IOdiCommandContext;
import ro.bcr.bita.odi.template.OdiBasicTemplate;

import oracle.odi.core.OdiInstance
import oracle.odi.core.persistence.transaction.ITransactionDefinition;
import oracle.odi.core.persistence.transaction.ITransactionManager
import oracle.odi.core.persistence.transaction.ITransactionStatus
import spock.lang.Specification;

class OdiBasicTemplateTest extends Specification{
	
	def OdiInstance mOdiInstance;
	def ITransactionManager mTxManager;
	def ITransactionStatus mTxStatus;
	def IOdiBasicCommand mCommand;
	
	def setup() {
		mOdiInstance=Mock();
		mTxManager=Mock();
		mTxStatus=Mock();
		mCommand=Mock();
	}
	
	def "Test Interaction of OdiBasicTemplate in transaction"(){	
		
		given: """A bunch of objects need to create an OdiTemplate.
				The objects are created in the setup method"""
			
			
		when:  "a command is executed without Odi Transaction"
			OdiBasicTemplate odiTmpl=(new OdiBasicTemplate.Builder(mOdiInstance)).build();
			odiTmpl.executeInTransaction(mCommand);
			
		then: "no trasaction related interactions, only the execute method on the command object"
			1 * mOdiInstance.getTransactionManager() >> mTxManager;
			1 * mTxManager.getTransaction(_ as ITransactionDefinition) >> mTxStatus;
			1 * mCommand.execute(_ as IOdiCommandContext);
			1 * mTxManager.commit(mTxStatus);
			0 * _;//no other interactions
	}
	
	def "Test Interaction of OdiBasicTemplate without transaction"(){
		
		given: """A bunch of objects need to create an OdiTemplate.
				The objects are created in the setup method"""
			
		when: "A command is executed without Odi Transaction"
			OdiBasicTemplate odiTmpl=(new OdiBasicTemplate.Builder(mOdiInstance)).build();
			odiTmpl.executeWithoutTransaction(mCommand);
			
		then: "no trasaction related interactions, only the execute method on the command object"
			1 * mCommand.execute(_ as IOdiCommandContext);
			0 * _;//no other interactions
	}
	
	def "when builder has null OdiInstance parameters an OdiTemplateException is thrown"(){
		given:
			OdiBasicTemplate.Builder builder=new OdiBasicTemplate.Builder();
		when: 'try to construct the OdiTemplate object'
			builder.build();
		then: "OdiTemplateException is expected"
			thrown OdiTemplateException;
			
			
	}
	
	def "when builder has at least one null parameter an OdiTemplateException is thrown"(){
		given:
			OdiBasicTemplate.Builder builder=new OdiBasicTemplate.Builder();
			builder.setJdbcUrl("jdbc://ceva.undeva@altceva");
		when: 'try to construct the OdiTemplate object'
			builder.build();
		then: "OdiTemplateException is expected"
			thrown OdiTemplateException;	
	}
	
	def "when builder has at least one empty parameter an OdiTemplateException is thrown"(){
		given:
			OdiBasicTemplate.Builder builder=new OdiBasicTemplate.Builder();
			builder.setJdbcUrl("");
		when: 'try to construct the OdiTemplate object'
			builder.build();
		then: "OdiTemplateException is expected"
			thrown OdiTemplateException;
	}
	
	def "when one needs the OdiInstance it can obtain it using a dirty trick"(){
		given: """A bunch of objects need to create an OdiTemplate.
				The objects are created in the setup method"""
			def contextReceived;
		when: "The OdiTemplate is build and you execute a command"
			contextReceived=null;
			OdiBasicTemplate odiTmpl=(new OdiBasicTemplate.Builder(mOdiInstance)).build();
			odiTmpl.executeWithoutTransaction(mCommand);
		then: "In the command you have acces to the OdiComandContext"
			1 * mCommand.execute(_ as IOdiCommandContext) >> {arg-> contextReceived=arg[0];};
		then: "Which in turn gives you access to the OdiInstance"
			contextReceived instanceof OdiCommandContext;
			OdiEntityFactory f=((OdiCommandContext)contextReceived).getOdiEntityFactory();
			f.getOdiInstance()==mOdiInstance;
	}

}
