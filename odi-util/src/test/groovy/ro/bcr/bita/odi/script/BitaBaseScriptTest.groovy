package ro.bcr.bita.odi.script;

import java.nio.file.*;

import com.sun.org.apache.xalan.internal.xsltc.compiler.ForEach;

import oracle.odi.core.OdiInstance
import oracle.odi.core.persistence.transaction.ITransactionDefinition
import oracle.odi.core.persistence.transaction.ITransactionManager;
import oracle.odi.core.persistence.transaction.ITransactionStatus;

import ro.bcr.bita.common.ComposedDelegator
import ro.bcr.bita.odi.template.IOdiBasicCommand;
import ro.bcr.bita.odi.template.IOdiCommandContext
import ro.bcr.bita.odi.template.IOdiEntityFactory

import spock.lang.Specification



class BitaBaseScriptTest extends Specification{
	
	def OdiInstance mOdiInstance;
	def ITransactionManager mTxManager;
	def ITransactionStatus mTxStatus;
	
	def setup() {
		mOdiInstance=Mock();
		mTxManager=Mock();
		mTxStatus=Mock();
	}
	
	def "Execute using a script syntax a ODI command without ODI Transaction"(){
		
		given: "The defined script"
				String scriptContent="""
						evaluate(ro.bcr.bita.odi.script.BitaScriptEnv.baseScript());
						
						holderContext=new Object();
						
						withoutOdiTransaction {
							holderForContext=delegate;
						}
				""";

				Binding bin=new Binding();
				bin.odiInstance=mOdiInstance;	
				//bin.holderForContext=new Object();
		when: "The script is called"
			GroovyShell shell = new GroovyShell(bin);
			shell.evaluate(scriptContent);
		then: "The binding parameter is changed based on the script commands"
			bin.holderForContext!=null;
			bin.holderForContext instanceof ComposedDelegator;
			
	}
	
	def "Execute using a script syntax a ODI command IN a  ODI Transaction"(){
		
		given: "The defined script"
				String scriptContent="""
						evaluate(ro.bcr.bita.odi.script.BitaScriptEnv.baseScript());

						inOdiTransaction {
							holderForContext=delegate;
						}
				""";

				Binding bin=new Binding();
				bin.odiInstance=mOdiInstance;
				bin.holderForContext=new Object();
		when: "The script is called"
			GroovyShell shell = new GroovyShell(bin);
			shell.evaluate(scriptContent);
		then: "The binding parameter is changed based on the script commands"
			//the number of interactions is tested in another test. We are not interested in interactions
			mOdiInstance.getTransactionManager() >> mTxManager;
			mTxManager.getTransaction(_ as ITransactionDefinition) >> mTxStatus;
			mTxManager.commit(mTxStatus);
			bin.holderForContext!=null;
			bin.holderForContext instanceof ComposedDelegator;
			
	}
	
}