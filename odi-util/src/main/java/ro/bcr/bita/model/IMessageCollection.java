package ro.bcr.bita.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IMessageCollection {
	
	public String getId();
	/**
	 * @param key
	 * @return
	 * Returns a List of messages associated with that key
	 * If the key has no messages then the empty list will be returned
	 */
	public List<String> getMessagesForKey(String key);
	/**
	 * @param Key
	 * @param messages
	 * Adds the provided messages to the list of messages associated with the key
	 */
	public void add(String Key,String...messages);
	/**
	 * @param Key
	 * @param msgs
	 * Adds the provided messages to the list of messages associated with the key
	 */
	public void add(String Key,List<String> msgs);
	/**
	 * @param key
	 * Removes the messages associated with that key, if any.
	 */
	public void removeKey(String key);
	
	/**
	 * @return the number of keys that were added to the collection. Please be aware that is not mandatory 
	 * for all of them to have messages.
	 */
	public abstract Integer getNoOfKeys();
	/**
	 * @return a Set with the keys present in the collection
	 */
	public abstract Set<String> getKeys();
	/**
	 * @param key
	 * @return the number of messages for a key. If a key is not present 0 is returned.
	 */
	public abstract Integer getNoOfMessagesForKey(String key);
	/**
	 * @return the total number of messages from the MessageCollection.
	 */
	public abstract Integer getNoOfMessages();
	
	/**
	 * @param messages A List with the following format:
	 * <UL>
	 * <LI>First element id the key used</LI>
	 * <LI>Following elements are the messages</LI>
	 * </UL>
	 */
	public abstract void add(List<String> messages);
	/**
	 * @param content
	 */
	public abstract void add(Map<String,List<String>> content);

}
