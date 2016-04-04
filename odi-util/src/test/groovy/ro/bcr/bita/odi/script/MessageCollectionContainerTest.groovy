package ro.bcr.bita.odi.script

import org.junit.Ignore;

import spock.lang.Specification

class MessageCollectionContainerTest extends Specification {
	
	private MessageCollectionContainer subject;
	
	def setup() {
		subject=new MessageCollectionContainer();
	}
	
	
	def "Create MessageCollection"(){
		when:	"Method to create a colelction is called"
				subject.createCollection("SMTH");
		then:	"The colelction is created and it has size 0"
				subject.getCollection("SMTH") instanceof MessageCollection;
				subject.getCollection("SMTH").getNoOfKeys()==0;
	}
	
	def "Create MessageCollection with invalid arguments"(){
		when:	"Method to create a colelction is called with null"
				subject.createCollection(null);
		then:	"An exception is thrown"
				thrown IllegalArgumentException;
	}
	
	def "Retrieve MessageCollection that was not previously created"(){
		when:	"getCollection is called on the subject with an inexistent idCollection"
				subject.getCollection("NON_EXISTENT");
		then:
				thrown NonExistentMessageCollectionException;
		when:	"getCollection is called on the subject with null as idCollection"
				subject.getCollection(null);
		then:
				thrown IllegalArgumentException;
	}
	
	
	def "Adding messages to the MessageCollectionContainer with valid arguments"(){
		when:	"Adding a messgae to the container1"
				subject.createCollection("C1");
				subject.add("C1","KEY1","First Message");
				subject.add("C1","KEY1","Second Message","Third Message");
				subject.add("C1","KEY1",["Forth Message","Sixth Message","Seven Message"]);
		then:	"The number of collections, the nuumber of keys, and the number of messages matches the expectation"
				subject.getCollectionIds().size()==1
				subject.getCollection("C1").getMessagesForKey("KEY1").size()==6;
				subject.getCollection("C1").getKeys().size()==1;
		when:	"Adding a message to the container2"
				setup();
				subject.createCollection("C1");
				subject.add(["C1","KEY1","One Message"]);
				subject.add(["C1","KEY1","Second Message","Third Message","Forth Message"]);
				subject.add(["C1","KEY1",["Fifth Message"]]);
				subject.add(["C1","KEY1",["Sixth Message","Seventh Message","Eight message"]]);
				subject.add(["C1","KEY2","Message for another Key"]);
		then:	"The number of collections, the number of keys, and the number of messages matches the expectation"
				subject.getCollectionIds().size()==1
				subject.getCollection("C1").getMessagesForKey("KEY1").size()==8;
				subject.getCollection("C1").getKeys().size()==2;
		when:	"Adding messages to the container3"
				setup();
				subject.createCollection("C1");
				subject.createCollection("C2");
				subject.createCollection("C3");
				def messages=[
					"C1":[
						"KEY1":["One Message","Second mesage","Third Message"]
						,"KEY2":["Forth Mesage","Fifth Messages"]
						]
					,"C2":[
						"KEY1":["1 Message","2 Message","3 Message"]
						,"KEY3":["4 Message","5 Message","6 Message"]
						]
				]
				subject.add(messages);
		then:	"The number of collections, the nuumber of keys, and the number of messages matches the expectation"
				subject.getCollectionIds().size()==3
				
				subject.getCollection("C1").getKeys().size()==2;
				subject.getCollection("C1").getMessagesForKey("KEY1").size()==3;
				
				subject.getCollection("C2").getNoOfKeys()==2;
				subject.getCollection("C2").getKeys().size()==2;
				subject.getCollection("C2").getMessagesForKey("KEY1").size()==3;
				
				subject.getCollection("C3").getNoOfKeys()==0;
				subject.getCollection("C3").getNoOfMessages()==0;
				
	}
	
	def "Adding messages to the MessageCollectionContainer with INVALID arguments"(){
		given:	"We create a collection to use during a test"
				subject.createCollection("C1");
		when:	"""Adding a messgage to the container using the List parameter
				and the key si mising 
				"""
				
				subject.add(["C1"]);
		then:	"An exception must be thrown"
				thrown IllegalArgumentException;
				
		when:	"""Adding a messgage to the container using the List parameter
				and no message is present
				"""
				subject.add(["C1","KEY1"]);
		then:	"An exception must be thrown"
				thrown IllegalArgumentException;
				
		when:	"Add method is called with null"
				subject.add(null);
		then:	"An exception is thrown"
				thrown IllegalArgumentException;
		when:	"Add method is called with null,null"
				subject.add(null,null);
		then:	"An exception is thrown"
				thrown IllegalArgumentException;
		when:	"Add method is called with null,null,null"
				subject.add(null,null,null);
		then:	"An exception is thrown"
				thrown IllegalArgumentException;
		when:	"Add method is called with null,null,null,null"
				subject.add(null,null,null,null);
		then:	"An exception is thrown"
				thrown IllegalArgumentException;
		when:	"Add method is called with null,null,null,null,null"
				subject.add(null,null,null,null,null);
		then:	"An exception is thrown"
				thrown IllegalArgumentException;
		when:	"Add method is called with an existing collection but null afterwards"
				subject.add("C1",null,null,null,null);
		then:	"An exception is thrown"
				thrown IllegalArgumentException;
		when:	"""Adding a message to the container using the Map parameter
				and no inner Map is present
				"""
				subject.add (
					[
					"C1":["KEY1"]
					]
				);
		then:	"The key is added to the collection, with no messages"
				subject.getCollection("C1").getNoOfKeys()==1;
				subject.getCollection("C1").getNoOfMessages()==0;
	}
	
	def "Total Number of Messages"(){
		when:	"Adding multiple messages to the container"
				subject.createCollection("C1");
				subject.createCollection("C2");
				def messages=[
					"C1":[
						"KEY1":["One Message","Second mesage","Third Message"]
						,"KEY2":["Forth Mesage","Fifth Messages"]
						]
					,"C2":[
						"KEY1":["1 Message","2 Message","3 Message"]
						,"KEY3":["4 Message","5 Message","6 Message"]
						]
				]
				subject.add(messages);
		then:	"the correct number of messages is returned"
				subject.getNoOfMessages()==11;
	}
	
	def "Check plus and leftShift syntax by adding messages for a collection at a time"(){
		given:	"We create a collection"
				subject.createCollection("C1");
				def holderToCheck;
		when:	"A call is made using + operator and a List as parameter"
				subject+["C1","KEY1","Msg 1","Msg 2"];
		then:	"Behind the scens the add method is called"
				subject.getCollection("C1") instanceof MessageCollection;
				subject.getCollection("C1").getNoOfKeys()==1
				subject.getCollection("C1").getNoOfMessages()==2
		when:	"We use a the leftShift operator"
				subject << ["C1","KEY2","1","2","3"];
		then:	"Behind the scenes the add method is called"
				subject.getCollection("C1") instanceof MessageCollection;
				subject.getCollection("C1").getNoOfKeys()==2
				subject.getCollection("C1").getNoOfMessages()==5
				
	}
	
	def "Check plus and leftShift syntax by adding messages for multiple collections at a time"(){
		given:	"We create several collections"
				subject.createCollection("C1");
				subject.createCollection("c2");
				def holderToCheck;
		when:	"A call is made using + operator and a List as parameter"
				subject+[
					"C1":["KEY1":["Msg 1","Msg 2"]],
					"c2":[
						"KEY1":["1","2","3"]
						,"KEY2":["4","5","6"]
						]
				];
		then:	"Behind the scens the add method is called"
				subject.getCollection("C1") instanceof MessageCollection;
				subject.getCollection("C1").getNoOfKeys()==1
				subject.getCollection("C1").getNoOfMessages()==2
				
				subject.getCollection("c2") instanceof MessageCollection;
				subject.getCollection("c2").getNoOfKeys()==2
				subject.getCollection("c2").getNoOfMessages()==6
				
		when:	"We use a the leftShift operator"
				subject << [
					"C1":["KEY2":["Msg 3","Msg 4"]],
					"c2":[
						"KEY1":["7"]
						,"KEY3":["8","9","10"]
						]
					];
		then:	"Behind the scenes the add method is called"
				subject.getCollection("C1") instanceof MessageCollection;
				subject.getCollection("C1").getNoOfKeys()==2
				subject.getCollection("C1").getNoOfMessages()==4
				
				subject.getCollection("c2") instanceof MessageCollection;
				subject.getCollection("c2").getNoOfKeys()==3
				subject.getCollection("c2").getNoOfMessages()==10
				
				subject.getNoOfCollections()==2;
	}
	
	def "Check getAt method"(){
		given:	"Objects from sertup and a newly created MessageCollection and some messages in it"
				subject.createCollection("c1");
				subject <<["c1","KEY1","Msg 1","Msg 2","Msg 3"]
				def MessageCollection holder;
		when:	"one tries to access a collection, by using the notation['nameOfCollection']"
				holder=subject["c1"]
		then:	"the collection is returned"
				holder==subject.getCollection("c1");
	}
	
	def "Check missing property method"(){
		given: "objects from setup and a newly created collection"
				subject.createCollection("smth");
				def MessageCollection holder;
		when:	"one tries to acces the collection using container.name_of_collection notation"
				holder=subject.smth
		then: 	"the collection is retuned"
				holder==subject.getCollection("smth");
		when:	"one tries to access a collection that do not exists"
				holder==subject.smthElse
		then:	"An exception of MissingProperty is thrown"
				thrown MissingPropertyException;
	}
	
	def "each method with valid/expected input"(){
		given:	"objects from setup, some collections and some input messages"
				subject.createCollection("c1");
				subject.createCollection("c2");
				subject << [
					"c1":[
						"KEY1":["Msg 1","Msg 2","Msg 3"]
						,"KEY2":["Msg 4"]
						]
					,"c2":[
						"KEY1":["1","2"]
						,"KEY3":["3","4","5"]
						]
					];
				def List<String>holderOfIds;
				def List<String>holderOfKeys;
				def List<String>holderOfMsgs;
		when:	"each method is caled with 1 parameter"
				holderOfIds=[];
				holderOfKeys=[];
				holderOfMsgs=[];
				subject.each{id->
					holderOfIds<<id;
				}
		then:	"All ids are retirived"
				["c1","c2"]==holderOfIds;
		when:	"each method is caled with 2 parameter"
				holderOfIds=[];
				holderOfKeys=[];
				holderOfMsgs=[];
				subject.each{id,msgColl->
					holderOfIds<<id;
				}
		then:	"All ids are retirived"
				["c1","c2"]==holderOfIds;
		when:	"each method is caled with 3 parameter"
				holderOfIds=[];
				holderOfKeys=[];
				holderOfMsgs=[];
				subject.each{id,msgColl,key->
					holderOfIds<<id;
					holderOfKeys<<key;
				}
		then:	"All ids and keys are retirived"
				["c1","c1","c2","c2"]==holderOfIds;
				["KEY1","KEY2","KEY1","KEY3"]==holderOfKeys;
		when:	"each method is caled with 4 parameter"
				holderOfIds=[];
				holderOfKeys=[];
				holderOfMsgs=[];
				subject.each{id,msgColl,key,lst->
					holderOfIds<<id;
					holderOfKeys<<key;
					holderOfMsgs<<lst;
					holderOfMsgs=holderOfMsgs.flatten();
				}
		then:	"All ids and keys are retirived"
				["c1","c1","c2","c2"]==holderOfIds;
				["KEY1","KEY2","KEY1","KEY3"]==holderOfKeys;
				["Msg 1","Msg 2","Msg 3","Msg 4","1","2","3","4","5"]==holderOfMsgs;
		
	}
	
	def "each method with invalid input"(){
		given:	"objects from setup, some collections and some input messages"
				subject.createCollection("c1");
				subject.createCollection("c2");
				subject << [
					"c1":[
						"KEY1":["Msg 1","Msg 2","Msg 3"]
						,"KEY2":["Msg 4"]
						]
					,"c2":[
						"KEY1":["1","2"]
						,"KEY3":["3","4","5"]
						]
					];
				def List<String>holderOfIds;
				def List<String>holderOfKeys;
				def List<String>holderOfMsgs;
		when:	"each method is caled with 5 parameters"
				holderOfIds=[];
				holderOfKeys=[];
				holderOfMsgs=[];
				subject.each{p1,p2,p3,p4,p5->
				}
		then:	"IllegalArgumentException is thrown"
				thrown IllegalArgumentException;		
		
		when:	"each method is caled with wrong parameter types"
				holderOfIds=[];
				holderOfKeys=[];
				holderOfMsgs=[];
				subject.each{MessageCollection p1,p2,p3->
				}
		then:	"IllegalArgumentException is thrown"
				thrown IllegalArgumentException;
				
		when:	"each method is caled with empty closure"
				holderOfIds=[];
				holderOfKeys=[];
				holderOfMsgs=[];
				subject.each{
					holderOfIds<<it;
				};
		then:	"Nothing happens because it will have the default closure parameter"
				["c1","c2"]==holderOfIds;
		
		
	}

}
