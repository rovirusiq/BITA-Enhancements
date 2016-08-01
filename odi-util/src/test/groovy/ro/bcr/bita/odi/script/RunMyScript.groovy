package ro.bcr.bita.odi.script

import java.nio.file.Path;
import java.nio.file.Paths;

import oracle.odi.core.OdiInstance;
import oracle.odi.core.persistence.IOdiEntityManager;
import oracle.odi.core.persistence.transaction.ITransactionDefinition
import oracle.odi.core.persistence.transaction.ITransactionManager;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import spock.lang.Specification

class RunMyScript extends Specification{
	
	def OdiInstance mOdiInstance;
	def ITransactionManager mTxManager;
	def ITransactionStatus mTxStatus;
	def IOdiEntityManager mEntityManager;
	
	def setup() {
		mOdiInstance=Mock();
		mTxManager=Mock();
		mTxStatus=Mock();
		mEntityManager=Mock();
	}
	
	
	def "Not A real test. An execution environment for DSL"(){
		
		given: "The defined script"
				Path p=Paths.get(new File("ceva.txt").toURI()).parent;
				File nF=new File(p.toString()+"/src/test/resources/ro/bcr/bita/odi/script/MyTestScript.groovy");
				String scriptContent=nF.getText();
				def Binding bin=new Binding();
				bin.odiInstance=mOdiInstance;
				
		when: "The script is called"
				GroovyShell shell = new GroovyShell(bin);
				shell.evaluate(scriptContent);
		then: "The binding parameter is changed based on the script commands"
				//the number of interactions is tested in another test. We are not interested in interactions
				mOdiInstance.getTransactionManager() >> mTxManager;
				mOdiInstance.getTransactionalEntityManager() >> mEntityManager;
				mTxManager.getTransaction(_ as ITransactionDefinition) >> mTxStatus;
				mTxManager.commit(mTxStatus);
			
			
	}

}
