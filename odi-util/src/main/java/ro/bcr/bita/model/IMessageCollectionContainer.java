package ro.bcr.bita.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IMessageCollectionContainer {

	/********************************************************************************************
	 *
	 *Public API
	 *
	 ********************************************************************************************/
	public abstract IMessageCollection getCollection(String idCollection);

	public abstract Integer getNoOfCollections();

	public abstract Set<String> getCollectionIds();

	public abstract void add(String idCollection, String key,
			List<String> messages) throws IllegalArgumentException;

	public abstract void add(String idCollection, String key,
			String... messages);

	/**
	 * @param messages List that contains, in this order: idCollection, key and the messages. The messages
	 * can be Strings in the list, or a List in the list.
	 *
	 */
	public abstract void add(List<String> messages);

	/**
	 * @param content Convenience parameter to add messages to multiple collections
	 * <UL>
	 * <LI>Key of the outer Map represents the id of the collection</LI>
	 * <LI>Key of the inner Map (which is the value for the outer map) represent the key for the messages</LI>
	 * <LI>The value of the inner Map is the list of messages</LI> 
	 * </UL>
	 */
	public abstract void add(Map<String, Map<String, List<String>>> content);

	public abstract Integer getNoOfMessages();

	public abstract IMessageCollection getAt(String idCollection);

}