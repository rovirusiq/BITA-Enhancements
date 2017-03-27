package ro.bcr.bita.model

import ro.bcr.bita.odi.proxy.IOdiEntityFactory

import spock.lang.Ignore
import spock.lang.Specification
import oracle.odi.domain.mapping.IMapComponent
import oracle.odi.domain.mapping.MapComponentType
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.mapping.physical.MapPhysicalDesign
import oracle.odi.domain.mapping.physical.MapPhysicalNode
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiProjectFinder

import ro.bcr.bita.odi.proxy.OdiPathUtil
import ro.bcr.bita.odi.proxy.OdiProjectPaths
import ro.bcr.bita.odi.proxy.OdiFullMappingPath;

@Ignore
class BaseMockMappingSpecification extends Specification{
	
	protected IOdiEntityFactory stbOdiEntityFactory;
	protected IMappingFinder stbOdiMappingFinder;
	protected IOdiFolderFinder stbOdiFolderFinder;
	protected IOdiProjectFinder stbOdiProjectFinder;
	
	def setup(){
		stbOdiEntityFactory=Stub();
		stbOdiEntityFactory.newOdiPathUtil() >> {
			return new OdiPathUtil(stbOdiEntityFactory);
		}
		
		stbOdiEntityFactory.newOdiProjectPaths(_ as Map)>> {Map x->
			return new OdiProjectPaths(x);
		}
		
		stbOdiMappingFinder=Stub();
		stbOdiFolderFinder=Stub();
		stbOdiProjectFinder=Stub();
		
		stbOdiEntityFactory.createMappingFinder() >> stbOdiMappingFinder;
		stbOdiEntityFactory.createProjectFolderFinder() >> stbOdiFolderFinder;
		stbOdiEntityFactory.createProjectFinder() >> stbOdiProjectFinder;
		
		stbOdiEntityFactory.newOdiMappingFullPath(_ as String, _ as String, _ as String) >> {String a, String b, String c ->return new OdiFullMappingPath(a, b, c)}
	}
	
	protected Closure createMockOdiMapping= {String mappingName,String dependencyDefinition->
		MockMappingConfigurator mapCfg=new MockMappingConfigurator();
		mapCfg.parseDependency(dependencyDefinition);
		def List<MockMappingNodeConfigurator> lstConfigNodes=mapCfg.build();
		
		def lstPhysicalNodeStubs=lstConfigNodes.collect{MockMappingNodeConfigurator configNode->
			
			MapPhysicalNode pn=Stub();
			
			//settings for component type
			MapComponentType compType=Stub();
			compType.isDataStorageComponent() >> configNode.isDataStore;
		
			
			//settings for logical component
			IMapComponent logicalComp=Stub();
			logicalComp.getBoundObjectName() >> configNode.boundObjectName;
			
			
			//setting for physical node
			pn.getUpstreamConnectedNodes() >> (configNode.upstreamNodes as List);
			
			//setting for physical node
			pn.getDownstreamConnectedNodes() >> (configNode.downstreamNodes as List);
			
			
			//orchestrate objects
			logicalComp.getComponentType()>>compType;
			pn.getLogicalComponent() >> logicalComp;
				
		
			return pn;
			
		}
		
		Mapping odiObject=Stub();
		MapPhysicalDesign phyDsgn=Stub();

		odiObject.getName() >> mappingName;		
		odiObject.getPhysicalDesign(0) >> phyDsgn;

		
		phyDsgn.getPhysicalNodes() >> lstPhysicalNodeStubs;
		
		return odiObject;
		
	}
		
}
