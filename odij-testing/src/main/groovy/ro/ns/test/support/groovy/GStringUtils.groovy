package ro.ns.test.support.groovy;


class GStringUtils {
	
	public static GString readFromFile(String fileName) {
		return readFromFile(new File(fileName));
	}
	
	public static GString readFromFile(File fileObject) {
		Reader rdr=fileObject.newReader();
		GString rsp=readFrom(rdr);
		rdr?.close();
		return rsp;
	}
	
	public static GString readFrom(Reader rdr) {
		def rsp="";
		rsp=GString.EMPTY+rdr.getText();
		return rsp;
	}

}
