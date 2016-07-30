package ro.bcr.bita.model

import ro.bcr.bita.model.IMessageCollection;
import ro.bcr.bita.model.MessageCollection;

import spock.lang.Specification;


class MessageCollectionTest extends Specification {
	
	private IMessageCollection subject;
	private String idForCollection="TestCollection";
	
	def setup() {
		subject=new MessageCollection(idForCollection);
	}
	
	def "Test parameter check in the constructor"(){
		when:	"We try to create a new MessageCollection with a null id"
				def msgColl=new MessageCollection(null);
		then:	"An exception is thrown"
				thrown IllegalArgumentException;
		when:	"We try to create a new MessageCollection with empty id"
				msgColl=new MessageCollection("");
		then:	"An exception is thrown"
				thrown IllegalArgumentException;
	}
	
	def "Check the id of the collection"(){
		when:	"A collection is created. In out case through setup method"
		then:	"The retuned id must be equal with the one provided at creation"
				subject.getId()==idForCollection;
	}
	
	def "Test the values returned for the one key"(){
		when:	"we do not having anything for a key"
		then:	"A lsit with size 0  shoul be retunred"
				subject.getMessagesForKey("NON_EXISTENT_KEY").size()==0;
		when:	"a messsage is added for a key"
				subject.add("KEY","CUTU");
		then:	"we retrieve for that key a list"
				subject.getMessagesForKey("KEY") instanceof List
				subject.getMessagesForKey("KEY").size()==1
		when:	"several messages are added for one key"
				subject.add("KEY2","One Message","Second message","Third message");
		then:	"a list of messages is retrieved for that key"
				subject.getMessagesForKey("KEY2") instanceof List
				subject.getMessagesForKey("KEY2").size()==3
		when:	"a list of messages is added for a key"
				subject.add("KEY3",["One Message","Seconf message","Third message"]);
		then:	"a list of messages is retrieved for that key"
				subject.getMessagesForKey("KEY3") instanceof List
				subject.getMessagesForKey("KEY3").size()==3
		when:	"multiple messages are added for one key, in several formats"
				subject.add("KEY4","One Message");
				subject.add("KEY4","Second message","Third Message","Forth message");
				subject.add("KEY4",["Fifth message","Sixth message"]);
				subject.add("KEY4","Seventh message");
		then:	"a list of messages is retrieved for that key"
				subject.getMessagesForKey("KEY4") instanceof List;
				subject.getMessagesForKey("KEY4").size()==7;
				subject.getMessagesForKey("KEY4").flatten().size()==7;
	}
	
	def "The add method call with a List parameter"(){
		when:	"The method is called with 0 parameters"
				subject.add([]);
		then:	"An exception is thrown"
				thrown IllegalArgumentException;
		when:	"The method is called only with the key parameter"
				subject.add(["SMTH"]);
		then:	"The key is added and there are 0 messages associated with it"
				subject.getNoOfMessagesForKey("SMTH")==0;
		when:	"The method is called with only one key, but a diferent syntax"
				subject.add("SMTH2");
		then:	"The key is added and there are 0 messages associated with it"
				subject.getNoOfMessagesForKey("SMTH2")==0;
		when:	"The method parameter has the key and one messages"
				subject.add(["SMTH3","1"]);
		then:	"the correct number of messages is returned"
				subject.getNoOfMessagesForKey("SMTH3")==1;
		when:	"The method parameter has the key and several messages"
				subject.add(["SMTH4","1","2","3","4"]);
		then:	"the correct number of messages is returned"
				subject.getNoOfMessagesForKey("SMTH4")==4;
	}
	
	def "The add method call with a Map parameter"(){
		when:	"The method is called with 0 parameters"
				subject.add([:]);
		then:	"Nothing is chenaged in the collection"
				subject.getNoOfKeys()==0;
		when:	"The method is called with one key and empty list of messages"
				subject.add(["KEY1":[]]);
		then:	"The collection has one key and zero messages"
				subject.getNoOfKeys()==1;
				subject.getNoOfMessages()==0;
		when:	"The method is called with multiple content"
				subject.add ([
					"KEY2":[]
					,"KEY3":["Msg1"]
					,"KEY4":null
					]	
				);
		then:
				subject.getNoOfKeys()==4;
				subject.getNoOfMessages()==1;
	}
	
	def "The add method with null parameter"(){
		when:	"Add method is called with null"
				subject.add(null);
		then: "Nothing should happen"
				subject.getKeys().size()==0;
		when:	"Add method is called with null,null"
				subject.add(null,null);
		then:	"It will be matched agains the add(String,List) and exception will be thrown"
			thrown IllegalArgumentException;
		when:	"Add method is called with null,null,null"
				subject.add(null,null,null);
		then:	"It will be matched agains the add(String,String,String) and exception will be thrown"
			thrown IllegalArgumentException;
			
	}
	
	def "Removal of a key"(){
		when:	"add a message for a key"
				subject.add("KEY1","Stuff goes here");
		then:	"we have a list of messages of size 1"
				subject.getMessagesForKey("KEY1").size()==1
		when:	"we remove the the previous key"
				subject.removeKey("KEY1");
		then:	"we retrive for that key we get null"
				subject.getMessagesForKey("KEY1").size()==0;
	}
	
	def "Retrieving the Number of messages for a a key"(){
		when:	"The key does not exist"
				
		then:	"0 should be returned for the method call and also for the list call"
				subject.getNoOfMessagesForKey("KEY1")==0
				subject.getMessagesForKey("KEY1").size()==0
		when:	"The key exists and the list has 0 size"
				subject.add("KEY1",[]);
		then:	"0 should be returned"
				subject.getNoOfMessagesForKey("KEY1")==0
				subject.getMessagesForKey("KEY1").size()==0
		when:	"Messages are added for a key"
				subject.add("KEY2","One Message","Second message");
		then:	"The number of messages equals the messages added for the key"
				2==subject.getNoOfMessagesForKey("KEY2");
				2==subject.getMessagesForKey("KEY2").size();
	}
	
	def "Number of keys"(){
		when:	"The NO key exists"
		then:	"0 is the number returned"
				subject.getNoOfKeys()==0
		when:	"A key is added"
				subject.add("KEY1",[]);
		then:	"1 is the number returned"
				subject.getNoOfKeys()==1;
		when:	"Another key is added"
				subject.add("KEY2",["A","B"]);
		then:	"2 is the number returned"
				subject.getNoOfKeys()==2;
		when:	"Other keys is added"
				subject.add("KEY3",["C","D"]);
				subject.add("KEY4",["E","F"]);
				subject.add("KEY5","G","H","I");
		then:	"2 is the number returned"
				subject.getNoOfKeys()==5;
	}
	
	def "Number of total messages"(){
		when:	"No message is in the collection"
		then:	"0 IS RETURNED"
				subject.getNoOfMessages()==0
		when:	"Mesages are added in the collection for different keys"
				subject.add("KEY3",["1","2"]);
				subject.add("KEY4",["3","4"]);
				subject.add("KEY5","5","6","7");
		then:	"The corresponding number is returned"
				subject.getNoOfMessages()==7
	}
	
	def "Check plus and leftShift syntax by adding messages for a key"(){
		when:	"A call is made using + operator and a List as parameter"
				def holder=subject+["KEY1","Msg 1","Msg 2"];
		then:	"Behind the scens the add method is called"
				subject.getNoOfKeys()==1
				subject.getNoOfMessages()==2
				holder.is(subject);
		when:	"We use a the leftShift operator"
				subject << ["KEY1","Msg3"];
				holder=subject << ["KEY2","1","2"];
		then:	"Behind the scenes the add method is called"
				subject.getNoOfKeys()==2
				subject.getNoOfMessages()==5
				holder.is(subject);
	}
	
	
	def "Check getAt method"(){
		given:	"Objects from setup, and some messagges added to the MessageCollection"
				subject << ["KEY1","Msg1","Msg2","Msg3"]
				subject << ["KEY2","Msg4","Msg5"]
				subject + ["KEY1","Msg6"]
				def List<String> holderMessages;
		when:	"a call is made using the syntax messageCollection[key]"
				holderMessages=subject["KEY1"];
		then:	"Behind the scens the getAt method is called"
				holderMessages.is(subject.getMessagesForKey("KEY1"));
	}
	
	def "Check each with valid/expected input"(){
		given:	"Objects from setup and some messages in the object"
				subject << ["KEY1","Msg1","Msg2","Msg3"]
				subject << ["KEY2","Msg4","Msg5"]
				subject + ["KEY1","Msg6"]
				def List<String> holderKeys;
				def List<List<String>> holderMessages; 
		when:	"the method each is called with a closure with 1 parameter"
				holderKeys=[];
				holderMessages=[];
				subject.each{k->
					holderKeys<<k;
				}
		then:	"The correct keys are retrieved"
				
				["KEY1","KEY2"]==holderKeys;
		when:	"The method each is called with a closure with 2 parameters"
				holderKeys=[];
				holderMessages=[];
				subject.each{k,l->
					holderKeys<<k;
					holderMessages<<l;
				}
		then: 	"The correct keys and list of messages are retrieved"
				["KEY1","KEY2"]==holderKeys;
				holderMessages==[
							["Msg1","Msg2","Msg3","Msg6"]
							,["Msg4","Msg5"]
							];
	}
	
	def "Check each with invalid input"(){
		given:	"Objects from setup and some messages in the object"
				subject << ["KEY1","Msg1","Msg2","Msg3"]
				subject << ["KEY2","Msg4","Msg5"]
				subject + ["KEY1","Msg6"]
				def List<String> holderKeys;
				def List<List<String>> holderMessages;
		when:	"the method each is called with a closure with more than 2 parameters"
				holderKeys=[];
				holderMessages=[];
				subject.each{k,l,m->
					holderKeys<<k;
				}
		then:	"IllegalArgumentException is thrown"
				thrown IllegalArgumentException;
		when:	"the method each is called with a closure with 0 parameters"
				holderKeys=[];
				holderMessages=[];
				subject.each{
					holderKeys << it;
				}
		then:	"IllegalArgumentException is NOT thrown because of the implicit parameter for a closure"
				notThrown IllegalArgumentException;
				holderKeys==["KEY1","KEY2"]
		when:	"the method each is called with the worng parameter types"
				holderKeys=[];
				holderMessages=[];
				subject.each{List<List<String>>l,String k->
					holderKeys << k;
				}
		then:	"MissingMethodException is NOT thrown because of the implicit parameter for a closure"
				thrown IllegalArgumentException;
	}

}
