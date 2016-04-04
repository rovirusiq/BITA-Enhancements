package ro.bcr.bita.odi.script

import spock.lang.Specification

class MessageCollectionContainerConfigHelperTest extends Specification{

	private MessageCollectionContainerConfigHelper subject;
	private MessageCollectionContainer msgContainer;
	
	
	def setup() {
		msgContainer=new MessageCollectionContainer();
		subject=new MessageCollectionContainerConfigHelper(msgContainer);
	}
	
	def "Test creattion of collection on the MessageContainer"(){
		given:	"Our objects from setup method"
		when:	"We access an inexistent property on the subject"
				subject.newCollection
		then:	"The collection is created in the MessageContainer"
				msgContainer.getNoOfCollections()==1;
				["newCollection"].containsAll(msgContainer.getCollectionIds());
		when:	"We access again the same property"
				subject.newCollection
		then:	"Nothing changes"
				msgContainer.getNoOfCollections()==1;
				["newCollection"].containsAll(msgContainer.getCollectionIds());
	}
	
}
