package ro.bcr.bita.odi.script

class GlobalHolderConfigHelper {
	
	public GlobalHolder ghold;
	
	public GlobalHolderConfigHelper(GlobalHolder wrapped) {
		ghold=wrapped;
	}
	
	/********************************************************************************************
	 *
	 *Meta programming
	 *
	 ********************************************************************************************/
	
	def propertyMissing(String name,value) {
		ghold.metaClass."$name"=value;
	}

}
