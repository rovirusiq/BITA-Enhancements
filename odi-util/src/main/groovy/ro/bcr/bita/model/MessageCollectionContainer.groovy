package ro.bcr.bita.model

import ro.bcr.bita.model.IMessageCollection;
import ro.bcr.bita.model.IMessageCollectionContainer;
import ro.bcr.bita.odi.script.NonExistentMessageCollectionException;

import groovy.lang.Closure;
import groovy.transform.TypeChecked;

@TypeChecked
class MessageCollectionContainer implements IMessageCollectionContainer {
	
	private Map<String,IMessageCollection>mapOfMsgCollections=[:];
	private IBitaModelFactory bitaModelFactory=new BitaModelFactory();
	
	
	
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
		IMessageCollection coll=mapOfMsgCollections[idCollection];
		if (coll==null) throw new NonExistentMessageCollectionException(idCollection);
		return coll;
	}
	
	private boolean checkCollectionExistence(String idCollection) {
		IMessageCollection coll=mapOfMsgCollections[idCollection];
		return (coll!=null);
	}
	
	protected IMessageCollection createCollection(String idCollection) {
		if (idCollection==null) throw new IllegalArgumentException("The id of the MessageCollection to be created canot be null");
		IMessageCollection coll=mapOfMsgCollections[idCollection];
		if (coll==null) {
			coll=bitaModelFactory.newMessageCollection(idCollection);
			mapOfMsgCollections[idCollection]=coll;
		}
		return coll;
	}
	
	
	/* (non-Javadoc)
	 * @see ro.bcr.bita.odi.script.IMessageCollectionContainer#getCollection(java.lang.String)
	 */
	@Override
	public IMessageCollection getCollection(String idCollection) {
		if (idCollection==null) throw new IllegalArgumentException("The id of the MessageCollection to be retrieved canot be null");
		IMessageCollection coll=enforceCollectionExistence(idCollection);
		return coll;
	}
	
	/* (non-Javadoc)
	 * @see ro.bcr.bita.odi.script.IMessageCollectionContainer#getNoOfCollections()
	 */
	@Override
	public Integer getNoOfCollections() {
		return this.mapOfMsgCollections.keySet().size();
	}
	
	/* (non-Javadoc)
	 * @see ro.bcr.bita.odi.script.IMessageCollectionContainer#getCollectionIds()
	 */
	@Override
	public  Set<String> getCollectionIds() {
		return this.mapOfMsgCollections.keySet();
	}
	
	/* (non-Javadoc)
	 * @see ro.bcr.bita.odi.script.IMessageCollectionContainer#add(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public void add(String idCollection,String key,List<String> messages) throws IllegalArgumentException{
		
		if (idCollection==null) throw new IllegalArgumentException("The first parameter of MessageCollectionContainer.add canot be null");
		
		IMessageCollection coll=enforceCollectionExistence(idCollection);
		coll.add(key,messages);
	}
	
	/* (non-Javadoc)
	 * @see ro.bcr.bita.odi.script.IMessageCollectionContainer#add(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void add(String idCollection,String key,String... messages) {
		add(idCollection,key, (messages as List));
	}
	
	/* (non-Javadoc)
	 * @see ro.bcr.bita.odi.script.IMessageCollectionContainer#add(java.util.List)
	 */
	//TODO remove the check "messages[2] instanceof List". It should not be used
	@Override
	public void add(List<String> messages) {
		
		if (messages==null) throw new IllegalArgumentException("The first parameter of MessageCollectionContainer.add canot be null");
		
		if ((messages==null) || (messages.size()<3)) {
			throw new IllegalArgumentException(EXCEP_MSG);
		}
		
		String idCollection=messages[0];
		String key=messages[1];
		List<String> lst;
		if ((messages[2] instanceof List)) {
			lst=messages[2] as List;
		} else if (messages[2] instanceof String){
			lst=messages[2..messages.size()-1];
		} else {
			throw new IllegalArgumentException(EXCEP_MSG);
		}
		
		add(idCollection,key,lst);
	
	}
	
	/* (non-Javadoc)
	 * @see ro.bcr.bita.odi.script.IMessageCollectionContainer#add(java.util.Map)
	 */
	@Override
	public void add(Map<String,Map<String,List<String>>> content) {
		
		if (content==null) throw new IllegalArgumentException("The first parameter of MessageCollectionContainer.add canot be null");
		
		if (!(content instanceof Map<String,Map<String,List<String>>>)){
			throw new IllegalArgumentException(EXCEP_MSG);
		}
		
		content.each {String k,Object v->
			try {
				if (v instanceof Map) {
					this.getCollection(k).add(v as Map);//delegate to the MessageCollection
				} else if (v instanceof List) {
					this.getCollection(k).add(v as List);//delegate to the MessageCollection
				}
			} catch (MissingMethodException ex) {
				throw new IllegalArgumentException("For ket[$k] the value provided is not of exepcted format. Please see the javadoc.");
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see ro.bcr.bita.odi.script.IMessageCollectionContainer#getNoOfMessages()
	 */
	@Override
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
	/* (non-Javadoc)
	 * @see ro.bcr.bita.odi.script.IMessageCollectionContainer#getAt(java.lang.String)
	 */
	@Override
	public IMessageCollection getAt(String idCollection) {
		return this.getCollection(idCollection);
	}
	
	public void each(Closure actionOnList) throws IllegalArgumentException{
		def keySet=this.getCollectionIds();
		
		Integer nP=actionOnList.getMaximumNumberOfParameters();
		
		keySet.each{String k->//id of the MessageCollection
			try {
				if (nP==1) {
					actionOnList(k);
				
				} else {
				
					IMessageCollection mC=this.getCollection(k);
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
