package ro.bcr.bita.odi.script

import spock.lang.Specification

class MessageCollectionContainerScriptHelperTest extends Specification{
		
		private MessageCollectionContainerScriptHelper subject;
		private MessageCollectionContainer msgC;
		
		def setup() {
			msgC=new MessageCollectionContainer();
			subject=new MessageCollectionContainerScriptHelper(msgC);
		}
		
		def "Delegation to accepted methods"(){
			given:	"Objects from setup and a newly created collection"
					msgC.createCollection("cici");
					def MessageCollection holder;
			when:	"the collection is accessed through the helper"
					holder=subject.getCollection("cici");
			then:	"the collection is returned"
					holder==msgC.getCollection("cici");
			when:	"using a different notation"
					holder=subject["cici"];
			then:	"the correct collection is returned"
					holder==msgC.getCollection("cici");
			when:	"trying to access a non existent collection thorugh the '.' notation"
					holder=subject.cutu;
			then:	"the MissingPropertyException is thrown"
					thrown MissingPropertyException;
			when:	"trying to access a non existent collection thorugh the '[]' notation"
					holder=subject["cutu"];
			then:	"the NonExistentMessageCollectionException is thrown"
					thrown NonExistentMessageCollectionException;
					
		}
		
		def "Delegation to excluded methods"(){
			given:	"Objects from setup and a newly created collection"
					msgC.createCollection("cici");
			when:	"trying to create a collection using the helper"
					subject.createCollection("mumu");
			then:	"A MssingMethodException is thrown"
					thrown MissingMethodException;
		
					
		}
		

}
