package ro.bcr.bita.model

class MockMappingConfigurator {
	
	def downstreamNodeRepo=[:];
	def upstreamNodeRepo=[:];
	
	def Closure processStreamNodes= {def lst,def createExternalList->
		
		if (lst==null) return [];
		
		def newLst=lst.collect{elem->elem;};
		def Integer counter=0;
		def Boolean b=true;
		def additionalList=[];
		while ((b) && (counter<100)) {
			counter++;
			b=false;
			
			newLst.each{n->
				def externalList=createExternalList(n);
				if (!newLst.containsAll(externalList)) {
					additionalList<<externalList;
					b=true;
				}
			}
			newLst.addAll(additionalList.flatten());
		}
		return newLst;
	}
	
	def void parseDependency(String smth) {
		
		if (smth==null) return;
		
		smth.trim().split(/\n/).each{String configLine->
			configLine=configLine.replaceAll(/[\r\s\t]/,"");
			if (configLine!=null && configLine!="") {
			
				def dependencyList=configLine.split(/>>/);
				
				if (dependencyList.size()!=(dependencyList as Set).size()) 
					throw new RuntimeException(
						"""One dependency line contains at lease physical node that has itself as dependency:$configLine""");
				
				int depSize=dependencyList.size();
				
				dependencyList.eachWithIndex{nodeName,idx->
					def lst1=downstreamNodeRepo[nodeName]!=null? downstreamNodeRepo[nodeName]:[];
					lst1 <<  dependencyList[(idx+1)..<depSize];
					downstreamNodeRepo[nodeName]=lst1;
					def lst2=upstreamNodeRepo[nodeName]!=null? upstreamNodeRepo[nodeName]:[];
					lst2 <<  dependencyList[0..<idx];
					upstreamNodeRepo[nodeName]=lst2;
				}
			}			
		}
		
		
	}
	
	def List<MockMappingNodeConfigurator> build() {
		
		def allNodes=[:];
		
		downstreamNodeRepo.each{k,v->
			allNodes[k]!=null? allNodes[k].downstreamNodes<<v:allNodes.put(k,new MockMappingNodeConfigurator(isDataStore:true,boundObjectName:k,downstreamNodes:v));
		}
		upstreamNodeRepo.each{k,v->
			allNodes[k]!=null? allNodes[k].upstreamNodes<<v:allNodes.put(k,new MockMappingNodeConfigurator(isDataStore:true,boundObjectName:k,upstreamNodes:v));
		}
		allNodes.each{k,v->
			v.downstreamNodes=(v.downstreamNodes.flatten() as Set);
			v.upstreamNodes=(v.upstreamNodes.flatten() as Set);
		}
		return allNodes.collect{k,v->
			
			//ugly hack, but should work with small number of physical nodes
			Boolean allowLogging=false;
			
			def nDwnLst=processStreamNodes(v.downstreamNodes) {n->
				allNodes[n].downstreamNodes
			}
			def nUpLst=processStreamNodes(v.upstreamNodes) {n->
				allNodes[n].upstreamNodes
			}
						
			v.downstreamNodes=(nDwnLst.flatten() as Set);
			v.upstreamNodes=(nUpLst.flatten() as Set);
			
			if (v.downstreamNodes.contains(k)) throw new RuntimeException("The $k node has as itself as a downstream dependency");
			if (v.upstreamNodes.contains(k)) throw new RuntimeException("The $k node has as itself as a upstream dependency");
		
			return v;
		}
	}

}
