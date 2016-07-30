package ro.bcr.bita.odi.script

import ro.bcr.bita.model.IMessageCollectionContainer;

class MessageCollectionContainerScriptHelper {
	
	@Delegate IMessageCollectionContainer msgC;
	
	public MessageCollectionContainerScriptHelper(IMessageCollectionContainer wrapped){
		this.msgC=wrapped;
	}
	
	public String toString() {
		"MsgScriptH@${Integer.toHexString(this.hashCode())}_for_${this.msgC.toString()}"
	}

}
