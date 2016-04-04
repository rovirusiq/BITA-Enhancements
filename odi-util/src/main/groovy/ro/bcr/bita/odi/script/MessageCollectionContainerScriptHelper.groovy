package ro.bcr.bita.odi.script

class MessageCollectionContainerScriptHelper {
	
	@Delegate MessageCollectionContainer msgC;
	
	public MessageCollectionContainerScriptHelper(MessageCollectionContainer wrapped){
		this.msgC=wrapped;
	}
	
	public String toString() {
		"MsgScriptH@${Integer.toHexString(this.hashCode())}_for_${this.msgC.toString()}"
	}

}
