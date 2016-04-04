package ro.bcr.bita.odi.script

class MessageCollectionContainerConfigHelper {
	
	
	private MessageCollectionContainer msgC;
	
	public MessageCollectionContainerConfigHelper(MessageCollectionContainer wrapped) {
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
