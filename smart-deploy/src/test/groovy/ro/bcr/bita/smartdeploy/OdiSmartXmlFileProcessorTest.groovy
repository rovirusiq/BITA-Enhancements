package ro.bcr.bita.smartdeploy

import groovy.xml.StreamingMarkupBuilder

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

class OdiSmartXmlFileProcessorTest {
	
	private static OdiSmartXmlFileProcessor subject

	private static final String XML_SMART = "src/test/resources/test-smart-export-2.xml"

	private static final String XML_SMART_EXPECTED = "src/test/resources/test-smart-export-expected-result-2.xml"

	private static final String XML_TEMP = "src/test/resources/temp-result.xml";
	
	@Before
	void beforeTest() {
		subject=new OdiSmartXmlFileProcessor();
	}
	
	@After
	void afterTest() {
		File f=new File(XML_TEMP)
		if (f.exists()) {
			f.delete();
		}
	}
	
	@Test
	public void removalOfNodeShouldBeSuccessfull() {
		File inputFile=new File(XML_SMART);
		File outputFile=new File(XML_TEMP);
		subject.removeAllNodesForToppology(inputFile,outputFile);
		
		File xmlRsp=new File(XML_TEMP);
		File xmlExp=new File(XML_SMART_EXPECTED);
		assert xmlRsp.text==xmlExp.text;
	
	}
	

}
