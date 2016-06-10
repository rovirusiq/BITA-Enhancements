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
import ro.bcr.bita.proxy.odi.IOdiEntityFactory;

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
	
	def "Chekc Binding usage from a BITA Script"(){
		
		given: "The defined script"
				String scriptContent="""
						evaluate(ro.bcr.bita.odi.script.BitaScriptEnv.baseScript());
						
						myMessageFromScript="Message from BITA script";
						
				""";

				Binding bin=new Binding();
				bin.odiInstance=mOdiInstance;	
		when: "The script is called"
			GroovyShell shell = new GroovyShell(bin);
			shell.evaluate(scriptContent);
		then: "The binding parameter is changed based on the script commands"
			bin.myMessageFromScript=="Message from BITA script";
			
	}
	
	
}