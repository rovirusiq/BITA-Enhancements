package ro.bcr.bita.odi.script

import spock.lang.Specification

class GlobalHolderConfigHelperTest extends Specification {
	
	private GlobalHolder gHolder;
	private GlobalHolderConfigHelper subject;
	
	def setup() {
		gHolder=new GlobalHolder();
		subject=new GlobalHolderConfigHelper(gHolder);
	}
	
	def "Adding a new prioperty through ConfigHelper"(){
		given:	"Objects from setup"
		when:	"A new property is initialized through the ConfigHelper"
				subject.p=3;
		then:	"That property is created in the GlobalHolder"
				gHolder.p==3;
		when:	"That property is modified using GlobalHolder"
				gHolder.p=5;
		then:	"It can be read from it"
				5==gHolder.p;
	}

}
