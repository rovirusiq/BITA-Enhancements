package ro.bcr.bita.odi.script;

import spock.lang.Specification

import java.nio.file.*

import oracle.odi.core.OdiInstance
import oracle.odi.core.persistence.transaction.ITransactionManager
import oracle.odi.core.persistence.transaction.ITransactionStatus



class BitaBaseScriptTest extends Specification{
	
	def OdiInstance mOdiInstance;
	def ITransactionManager mTxManager;
	def ITransactionStatus mTxStatus;
	
	def setup() {
		mOdiInstance=Mock();
		mTxManager=Mock();
		mTxStatus=Mock();
	}
	
	def "Check Binding usage from a BITA Script"(){
		
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