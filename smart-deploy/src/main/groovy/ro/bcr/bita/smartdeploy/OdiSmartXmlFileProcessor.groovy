package ro.bcr.bita.smartdeploy

import com.sun.org.apache.bcel.internal.generic.LSTORE;
import groovy.xml.XmlUtil;

import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder

class OdiSmartXmlFileProcessor {

	private static final String SUSPECT_NODE_NAME = "Object"
	private static final CLASSES_TO_BE_DELETED=[
		"com.sunopsis.dwg.dbobj.SnpConnect",
		"com.sunopsis.dwg.dbobj.SnpContext",
		"com.sunopsis.dwg.dbobj.SnpLschema",
		"com.sunopsis.dwg.dbobj.SnpMorigTxt",
		"com.sunopsis.dwg.dbobj.SnpMtxt",
		"com.sunopsis.dwg.dbobj.SnpPschema",
		"com.sunopsis.dwg.dbobj.SnpPschemaCont",
		"com.sunopsis.dwg.dbobj.SnpReferencedObject",
		
	];
	
	private GPathResult file2Xml(File xmlFile) {
		return new XmlSlurper().parse(xmlFile);
	}
	
	public void removeAllNodesForToppology(File inputXmlFile, File outputXmlFile) {
		
		def origContent=file2Xml(inputXmlFile);
		
		Iterator itr=origContent.children().breadthFirst();
		boolean ok=true;
		
		while ((itr.hasNext()) && (ok)){
			def n=itr.next();
			if (!SUSPECT_NODE_NAME.equals(n.name())) continue;
			if (CLASSES_TO_BE_DELETED.contains(n.@class)) {
				n.replaceNode{};
			} else {
				//println "Stop Game:"+n.@class;
				ok=false;
			}
			
		}
		FileWriter fW=new FileWriter(outputXmlFile);
		
		StreamingMarkupBuilder builder=new StreamingMarkupBuilder();
		builder.encoding="ISO-8859-1";
		def content= {
			mkp.xmlDeclaration(version:'1.0');
			mkp.yield origContent
		};
		
		fW << builder.bind(content);
		fW.close();
	}

}
