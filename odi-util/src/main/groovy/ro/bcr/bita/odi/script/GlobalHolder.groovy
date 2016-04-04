package ro.bcr.bita.odi.script

class GlobalHolder {
	
	public GlobalHolder() {
		def mc = new ExpandoMetaClass( GlobalHolder, false, true)
		mc.initialize();
		this.metaClass = mc;
	}
	
	
}
