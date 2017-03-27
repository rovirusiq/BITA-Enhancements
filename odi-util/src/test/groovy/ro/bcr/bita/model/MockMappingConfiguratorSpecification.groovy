package ro.bcr.bita.model

import ro.ns.spock.base.odi.IInitSubjectTestHelper

import spock.lang.Specification

class MockMappingConfiguratorSpecification extends Specification implements IInitSubjectTestHelper{
	
	def MockMappingConfigurator subject;
	
	def initSubject(Map params) {
		return subject=new MockMappingConfigurator();
	}
	
	def extractMappingNode(String name, def listOfNodes) {
		listOfNodes.find({it.boundObjectName==name});
	}
	
	def "Test parse dependency - one empty line"(){
		given:	"a initialized subject"
				subject=initSubject();
				def initialList=subject.build();
		when:	"it is asked to parse an empty line"
				subject.parseDependency "";
		then:	"noException is thrown"
				notThrown Exception;
		and:	"the list of returned nodes it is an empty list"
				def afterList=subject.build();
				initialList==afterList;
				initialList!=null;
	}
	
	def "Test parse dependency -null parameter"(){
		given:	"a initialized subject"
				subject=initSubject();
				def initialList=subject.build();
		when:	"it is asked to parse an empty line"
				subject.parseDependency null;
		then:	"noException is thrown"
				notThrown Exception;
		and:	"the list of returned nodes it is an empty list"
				def afterList=subject.build();
				initialList==afterList;
				initialList!=null;
	}
	
	def "Test parse dependency - one line, one physical node"(){
		given:	"a initialized subject"
				subject=initSubject();
		when:	"it is asked to parse an empty line"
				subject.parseDependency "LC_A";
				def afterList=subject.build();
		then:	"noException is thrown"
				notThrown Exception;
		and:	"the list of returned nodes containes the expected physical node"
				afterList.size()==1;
				afterList[0].boundObjectName=="LC_A";
				afterList[0].downstreamNodes!=null;
				afterList[0].downstreamNodes.size()==0;
				afterList[0].upstreamNodes!=null;
				afterList[0].upstreamNodes.size()==0;
	}
	
	def "Test parse dependency - one line, 2 nodes with dependency expressed"(){
		given:	"a initialized subject"
				subject=initSubject();
		when:	"it is asked to parse an empty line"
				subject.parseDependency "LC_A>>LC_B";
				def afterList=subject.build();
		then:	"noException is thrown"
				notThrown Exception;
		and:	"the list of returned nodes contains all the expected nodes"
				afterList.size()==2;
		and:	"the first node is as expected"
				afterList[0].boundObjectName=="LC_A";
				afterList[0].downstreamNodes.size()==1;
				afterList[0].upstreamNodes.size()==0;
		and:	"the second node is as expected"
				afterList[1].boundObjectName=="LC_B";
				afterList[1].downstreamNodes.size()==0;
				afterList[1].upstreamNodes.size()==1;
	}
	
	def "Test parse dependency - one line, multiple nodes with dependency expressed"(){
		given:	"a initialized subject"
				subject=initSubject();
		when:	"it is asked to parse an empty line"
				subject.parseDependency "LC_A>>LC_B>>LC_C";
				def afterList=subject.build();
		then:	"noException is thrown"
				notThrown Exception;
		and:	"the list of returned nodes contains all the expected nodes"
				afterList.size()==3;
		and:	"the first node is as expected"		
				afterList[0].boundObjectName=="LC_A";
				afterList[0].downstreamNodes.size()==2;
				afterList[0].upstreamNodes.size()==0;
		and:	"the second node is as expected"
				afterList[1].boundObjectName=="LC_B";
				afterList[1].downstreamNodes.size()==1;
				afterList[1].upstreamNodes.size()==1;
		and:	"the third node is as expected"
				afterList[2].boundObjectName=="LC_C";
				afterList[2].downstreamNodes.size()==0;
				afterList[2].upstreamNodes.size()==2;
	}
	
	def "Test parse dependency - one line, physical node appears multiple times"(){
		given:	"a initialized subject"
				subject=initSubject();
		when:	"it is asked to parse an empty line"
				subject.parseDependency "LC_A>>LC_B>>LC_A";
				def afterList=subject.build();
		then:	"an exception is thrown"
				RuntimeException ex=thrown();
				ex instanceof RuntimeException;
		and:	"the message contains the line of the dependency in error"
				ex.message.contains("LC_A>>LC_B>>LC_A");
	}
	
	def "Test parse dependency - multiple lines"(){
		given:	"a initialized subject"
				subject=initSubject();
		when:	"it is asked to parse an empty line"
				subject.parseDependency """
					LC_A>>LC_B>>LC_C
					LC_D>>LC_B
					LC_E>>LC_C
					LC_X>>LC_D
				""";
				def afterList=subject.build();
		then:	"no exception is thrown"
				notThrown Exception;
		and:	"the nodes and their links are as expected"
				afterList.size()==6;
				extractMappingNode("LC_A",afterList).downstreamNodes.size()==2;
				extractMappingNode("LC_A",afterList).upstreamNodes.size()==0;
				extractMappingNode("LC_B",afterList).downstreamNodes.size()==1;
				extractMappingNode("LC_B",afterList).upstreamNodes.size()==3;
				extractMappingNode("LC_C",afterList).downstreamNodes.size()==0;
				extractMappingNode("LC_C",afterList).upstreamNodes.size()==5;
				extractMappingNode("LC_D",afterList).downstreamNodes.size()==2;
				extractMappingNode("LC_D",afterList).upstreamNodes.size()==1;
				extractMappingNode("LC_E",afterList).downstreamNodes.size()==1;
				extractMappingNode("LC_E",afterList).upstreamNodes.size()==0;
				extractMappingNode("LC_X",afterList).downstreamNodes.size()==3;
				extractMappingNode("LC_X",afterList).upstreamNodes.size()==0;
	}
	
	
	def "Test parse dependency - multiple lines and circular downstream dependency"(){
		given:	"a initialized subject"
				subject=initSubject();
		when:	"it is asked to parse an empty line"
				subject.parseDependency """
					LC_A>>LC_B>>LC_C
					LC_D>>LC_B
					LC_B>>LC_A
				""";
				def afterList=subject.build();
		then:	"a specific runtime exception is thrown"
				RuntimeException ex=thrown();
		and:	"the message contains a specific pattern"
				ex.message.contains("node has as itself as a downstream dependency");
	}
	
	def "Test parse dependency - multiple calles to one line"(){
		given:	"a initialized subject"
				subject=initSubject();
		when:	"it is asked to parse an empty line"
				subject.parseDependency "LC_A>>LC_B>>LC_C";
				subject.parseDependency "LC_D>>LC_B>>LC_C";
				subject.parseDependency "LC_E>>LC_C";
				def afterList=subject.build();
		then:	"no exception is thrown"
				notThrown Exception;
		and:	"the nodes and their links are as expected"
				afterList.size()==5;
				extractMappingNode("LC_A",afterList).downstreamNodes.size()==2;
				extractMappingNode("LC_A",afterList).upstreamNodes.size()==0;
				extractMappingNode("LC_B",afterList).downstreamNodes.size()==1;
				extractMappingNode("LC_B",afterList).upstreamNodes.size()==2;
				extractMappingNode("LC_C",afterList).downstreamNodes.size()==0;
				extractMappingNode("LC_C",afterList).upstreamNodes.size()==4;
				extractMappingNode("LC_D",afterList).downstreamNodes.size()==2;
				extractMappingNode("LC_D",afterList).upstreamNodes.size()==0;
				extractMappingNode("LC_E",afterList).downstreamNodes.size()==1;
				extractMappingNode("LC_E",afterList).upstreamNodes.size()==0;
				
	}
	
}
