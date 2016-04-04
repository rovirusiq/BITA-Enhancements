package ro.bcr.bita.odi.script

import bsh.This;

import java.util.List;

class MessageCollection implements IMessageCollection{

	private id="";
	private Map<String,List<String>> collection=[:];
	
	private final String  MSG_EX_EACH_CLOSURE="The closure provided to MessageCollection.each must allow as parameters, in this order, the Key and the List of messages for that key";
		
	public MessageCollection(String id) throws IllegalArgumentException {
		if ((id==null) || ("".equals(id))) {
			throw new IllegalArgumentException("When creating a collection the ID must not be empty or null.");
		}
		this.id=id;
	}
	
	/********************************************************************************************
	 *
	 *Private API
	 *
	 ********************************************************************************************/
	
	
	/********************************************************************************************
	 *
	 *IMessage Collection
	 *
	 ********************************************************************************************/

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public List<String> getMessagesForKey(String key) {
		List<String> rsp=collection[key];
		if (rsp==null) rsp=[];
		return rsp;
	}

	@Override
	public void add(String key, String... messages) {
		if (key==null) throw new IllegalArgumentException("The key provided for the MessageCollection[$id] cannot be null");
		this.add(key,messages as List);
	}
	
	public void add(Map<String,List<String>> content) {
		if (content==null) return;
		
		content.each{String k,List<String> v->
			if (v==null) {
				v=["$k"];
			} else {
				v.add(0,k);
			}
			this.add(v);
		}
	}
	
	@Override
	public void add(List<String> messages) {
		if (messages==null) return;
		if (messages.size()>=2) {
			this.add(messages[0],messages[1..messages.size()-1]);
		}else if (messages.size()==1) {
			this.add(messages[0],[]);
		} else {
			throw new IllegalArgumentException("The call to add messages to the MessageCollection[${this.id}] has an invalid parameter:$messages. The KEY is missing. Please consult the javadoc.");
		}
	}




	@Override
	public void add(String key,List<String> msgs) {
		if (key==null) throw new IllegalArgumentException("The key provided for the MessageCollection[$id] cannot be null");
		if (collection[key]==null){
			collection[key]=(msgs)
		} else {
			collection[key].addAll(msgs);
		}
		
	}
	
	@Override
	public void removeKey(String key) {
		collection.remove(key);
	}
	
	@Override
	public Set<String> getKeys() {
		return this.collection.keySet();
	}
	
	@Override
	public Integer getNoOfKeys() {
		return this.collection.keySet().size();
	}
	
	@Override
	public Integer getNoOfMessagesForKey(String key) {
		List<String> rsp=collection[key];
		if (rsp==null) return 0;
		return rsp.size();
	}
	
	@Override
	public Integer getNoOfMessages() {
		Integer t=0;
		this.collection.each{k,v->
			if (v!=null) t+=v.size();
		}
		return t;
	}
	
	public String toString() {
		String rsp="${this.getClass()}@${Integer.toHexString(this.hashCode())}_Keys:${this.getNoOfKeys()}_${this.getKeys()}";
		return rsp;
	}
	/********************************************************************************************
	 *
	 *MetaProgramming
	 *
	 ********************************************************************************************/
	public MessageCollection plus(List<String> messages) {
		this.add(messages);
		return this;
	}
	public MessageCollection leftShift(List<String> messages) {
		this.add(messages);
		return this;
	}
	public List getAt(String key) {
		return this.getMessagesForKey(key);
	}
	public MessageCollection each(Closure actionOnList) throws IllegalArgumentException{
		def keySet=this.getKeys();
		
		keySet.each{k->
			try {
				if (actionOnList.getMaximumNumberOfParameters()==2) {
					actionOnList(k,this.getMessagesForKey(k));
				} else{
					actionOnList(k);
				}
			} catch (MissingMethodException ex) {
				throw new IllegalArgumentException(MSG_EX_EACH_CLOSURE,ex);
			}	
		}
		return this;
	}
}
