package ro.bcr.bita.odi.script

import groovy.lang.Closure;

class MessageCollectionContainer {
	
	private Map<String,MessageCollection>mapOfMsgCollections=[:];
	
	
	
	/********************************************************************************************
	 *
	 *Private API
	 *
	 ********************************************************************************************/
	private final String EXCEP_MSG="The parameter does not have the expected format. Please see the javadoc";
	private final String MSG_EX_EACH_CLOSURE='''The parameters for the MessageCollectionContainer.each are:
1. String - the id of MessageCollection
2. MessageCollection - the id of the MessageCollection
3. String - the key of the messages
4. List<String> - the list of messages
''';
	
	
	protected IMessageCollection enforceCollectionExistence(String idCollection) {
		MessageCollection coll=mapOfMsgCollections[idCollection];
		if (coll==null) throw new NonExistentMessageCollectionException(idCollection);
		return coll;
	}
	
	private boolean checkCollectionExistence(String idCollection) {
		MessageCollection coll=mapOfMsgCollections[idCollection];
		return (coll!=null);
	}
	
	protected IMessageCollection createCollection(String idCollection) {
		if (idCollection==null) throw new IllegalArgumentException("The id of the MessageCollection to be created canot be null");
		MessageCollection coll=mapOfMsgCollections[idCollection];
		if (coll==null) {
			coll=new MessageCollection(idCollection);
			mapOfMsgCollections[idCollection]=coll;
		}
		return coll;
	}
	
	
	/********************************************************************************************
	 *
	 *Public API
	 *
	 ********************************************************************************************/
	public IMessageCollection getCollection(String idCollection) {
		if (idCollection==null) throw new IllegalArgumentException("The id of the MessageCollection to be retrieved canot be null");
		MessageCollection coll=enforceCollectionExistence(idCollection);
		return coll;
	}
	
	public Integer getNoOfCollections() {
		return this.mapOfMsgCollections.keySet().size();
	}
	
	public  Set<String> getCollectionIds() {
		return this.mapOfMsgCollections.keySet();
	}
	
	public void add(String idCollection,String key,List<String> messages) throws IllegalArgumentException{
		
		if (idCollection==null) throw new IllegalArgumentException("The first parameter of MessageCollectionContainer.add canot be null");
		
		MessageCollection coll=enforceCollectionExistence(idCollection);
		coll.add(key,messages);
	}
	
	public void add(String idCollection,String key,String... messages) {
		add(idCollection,key, (messages as List));
	}
	
	/**
	 * @param messages List that contains, in this order: idCollection, key and the messages. The messages
	 * can be Strings in the list, or a List in the list.
	 *
	 */
	public void add(List<String> messages) {
		
		if (messages==null) throw new IllegalArgumentException("The first parameter of MessageCollectionContainer.add canot be null");
		
		if ((messages==null) || (messages.size()<3)) {
			throw new IllegalArgumentException(EXCEP_MSG);
		}
		
		String idCollection=messages[0];
		String key=messages[1];
		List<String> lst;
		if ((messages[2] instanceof List)) {
			lst=messages[2];
		} else if (messages[2] instanceof String){
			lst=messages[2..messages.size()-1];
		} else {
			throw new IllegalArgumentException(EXCEP_MSG);
		}
		
		add(idCollection,key,lst);
	
	}
	
	/**
	 * @param content Convenience parameter to add messages to multiple collections
	 * <UL>
	 * <LI>Key of the outer Map represents the id of the collection</LI>
	 * <LI>Key of the inner Map (which is the value for the outer map) represent the key for the messages</LI>
	 * <LI>The value of the inner Map is the list of messages</LI> 
	 * </UL>
	 */
	public void add(Map<String,Map<String,List<String>>> content) {
		
		if (content==null) throw new IllegalArgumentException("The first parameter of MessageCollectionContainer.add canot be null");
		
		if (!(content instanceof Map<String,Map<String,List<String>>>)){
			throw new IllegalArgumentException(EXCEP_MSG);
		}
		
		content.each {k,v->
			try {
				
				this.getCollection(k).add(v);//delegate to the MessageCollection
				/*
				v.each{nk,vk->
					this.add(k,nk,vk);
				}
				*/
			} catch (MissingMethodException ex) {
				throw new IllegalArgumentException("For ket[$k] the value provided is not of exepcted format. Please see the javadoc.");
			}
		}
	}
	
	public Integer getNoOfMessages() {
		Integer rsp=0;
		mapOfMsgCollections.each{String k,MessageCollection v->
			rsp+=v.getNoOfMessages();
		}
		return rsp;
	}
	
	public String toString() {
		String rsp="${this.getClass()}@${Integer.toHexString(this.hashCode())}_Ids:${this.getNoOfCollections()}_${this.getCollectionIds()}";
		return rsp;
	}
	/********************************************************************************************
	 *
	 *MetaProgramming
	 *
	 ********************************************************************************************/
	public void plus(List<String> messages) {
		this.add(messages);
	}
	
	public void plus(Map<String,Map<String,List<String>>> content) {
		this.add(content);
	}
	
	public void leftShift(List<String> messages) {
		this.add(messages);
	}
	
	public void leftShift(Map<String,Map<String,List<String>>> content) {
		this.add(content);
	}
	public MessageCollection getAt(String idCollection) {
		return this.getCollection(idCollection);
	}
	
	public void each(Closure actionOnList) throws IllegalArgumentException{
		def keySet=this.getCollectionIds();
		
		Integer nP=actionOnList.getMaximumNumberOfParameters();
		
		keySet.each{k->//id of the MessageCollection
			try {
				if (nP==1) {
					actionOnList(k);
				
				} else {
				
					MessageCollection mC=this.getCollection(k);
					Closure a;
					
					boolean okToCall=true;
					if (nP==2){
						actionOnList(k,mC);
						okToCall=false;
					} else if (nP==3) {
						a= {ky->//key from the MessageCollection
							actionOnList(k,mC,ky);
						}
					} else if (nP==4) {
						a= {ky,lst->//key and list of messages from the MessageCollection
							actionOnList(k,mC,ky,lst);
						}
					} else {
						throw new IllegalArgumentException(MSG_EX_EACH_CLOSURE);
					}
					
					try {
						if (okToCall) mC.each a;
					} catch (MissingMethodException | IllegalArgumentException  excep) {
						throw new IllegalArgumentException(MSG_EX_EACH_CLOSURE);
					}
				}
				
			} catch (MissingMethodException ex) {
				throw new IllegalArgumentException(MSG_EX_EACH_CLOSURE,ex);
			}	
		}
	}
	
	public propertyMissing(String name) {
		if (this.checkCollectionExistence(name)) return this.getCollection(name);
		throw new MissingPropertyException("No collection with id[$name] in the MessageContainer[$this]", name, MessageCollectionContainer);
	}
	

}
