package ro.bcr.bita.odi.script

import ro.bcr.bita.model.IMessageCollectionContainer;
import ro.bcr.bita.model.MessageCollectionContainer;

class MessageCollectionContainerConfigHelper {
	
	
	private MessageCollectionContainer msgC;
	
	public MessageCollectionContainerConfigHelper(IMessageCollectionContainer wrapped) {
		def mc = new ExpandoMetaClass( MessageCollectionContainerConfigHelper, false, true)
		mc.initialize();
		this.metaClass = mc;
		this.msgC=wrapped;
	}
	
	/********************************************************************************************
	 *
	 *Meta programming
	 *
	 ********************************************************************************************/
	def propertyMissing(String name) {
		return this.msgC.createCollection(name);
	}

}
