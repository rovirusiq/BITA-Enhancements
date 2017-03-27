package ro.bcr.bita.model

import groovy.transform.ToString

@ToString(includeNames=true,includes=["boundObjectName"])
class MockMappingNodeConfigurator {
	
	def Set downstreamNodes=[];
	def Set upstreamNodes=[];
	def isDataStore=true;
	def boundObjectName="";

	
	
	
}
