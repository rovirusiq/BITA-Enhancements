package ro.bcr.bita.model

import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.proxy.OdiFullMappingPath;
import ro.bcr.bita.odi.proxy.OdiPathUtil
import ro.bcr.bita.odi.proxy.OdiProjectPaths

import spock.lang.Specification
import oracle.odi.domain.mapping.IMapComponent
import oracle.odi.domain.mapping.MapComponentType
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.mapping.physical.MapPhysicalDesign
import oracle.odi.domain.mapping.physical.MapPhysicalNode
import oracle.odi.domain.project.OdiFolder
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiProjectFinder

//TODO sa ma gandesc la un DSL mai frumos ca sa creeze PhysicalNodes
class BitaSpockSpecification extends Specification{
	
	
	protected IOdiEntityFactory stbOdiEntityFactory;
	protected IMappingFinder stbOdiMappingFinder;
	protected IOdiFolderFinder stbOdiFolderFinder;
	protected IOdiProjectFinder stbOdiProjectFinder;
	
	protected setup() {
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

	/********************************************************************************************
	 *
	 *Not exposed methods
	 *
	 ********************************************************************************************/
	protected OdiFolder stubOdiFolder(MckCfgFolder config) {
		OdiFolder fld=Stub();
		
		fld.getName() >> config.NAME;
		
		return fld;
	}
	
	protected MapPhysicalNode stubOdiMappingPhysicalNode(MckCfgPhysicalNode config) {
		MapPhysicalNode pn=Stub();
		
		//settings for component type
		MapComponentType compType=Stub();
		compType.isDataStorageComponent() >> config.IS_DATA_STORE;
	
		
		//settings for logical component
		IMapComponent logicalComp=Stub();
		logicalComp.getBoundObjectName() >> config.BOUND_OBJECT_NAME;
		
		
		//setting for physical node
		pn.getUpstreamConnectedNodes() >> config.UPSTREAM_NODES;
		
		//setting for physical node
		pn.getDownstreamConnectedNodes() >> config.DOWNSTREAM_NODES;
		
		
		//orchestrate objects
		logicalComp.getComponentType()>>compType;
		pn.getLogicalComponent() >> logicalComp;
			
	
		return pn;
	}
	
	protected Mapping stubOdiMapping(String name) {
		return this.stubOdiMapping(new MckCfgMapping(NAME:name));
	}
	
	protected Mapping stubOdiMapping(MckCfgMapping config) {
		Mapping odiObject=Stub();
		odiObject.getName() >> config.NAME;
		return odiObject;
	}
	protected Mapping stubOdiMapping(MapPhysicalNode... nodes) {
		Mapping odiObject=Stub();
		MapPhysicalDesign phyDsgn=Stub();
		
		odiObject.getPhysicalDesign(0) >> phyDsgn;

		
		phyDsgn.getPhysicalNodes() >> nodes;
		
		return odiObject;
	}
	
	
	
	/********************************************************************************************
	 *
	 *Mock and stub for commonly used type of objects
	 *
	 ********************************************************************************************/
	
	protected <T> T StubObject(Class<T> c){
		T entity=Stub(c);
		//configure(entity);
		return entity;
	}
	protected <T> T MockObject(Class<T> c){
		T entity=Mock(c);
		//configure(entity);
		return entity;
	}
	
	
	
	/********************************************************************************************
	 *
	 *Easy to use methods - DSL friendly
	 *
	 *
	 ********************************************************************************************/
	//TODO analyze methofds signature because they all should return mocked objects
	
	protected OdiFolder BITA_MOCK_ODI_FOLDER(Map params) {
		return this.stubOdiFolder(new MckCfgFolder(params));
	}
	
	protected MapPhysicalNode BITA_MOCK_ODI_PN() {
		return this.stubOdiMappingPhysicalNode(new MckCfgPhysicalNode())
	}
	
	protected MapPhysicalNode BITA_MOCK_ODI_PN(Map params) {
		return this.stubOdiMappingPhysicalNode(new MckCfgPhysicalNode(params));
	}
	
	protected Mapping BITA_MOCK_ODI_MAPPING(MapPhysicalNode... nodes) {
		return this.stubOdiMapping(nodes);
	}
	
	protected Mapping BITA_MOCK_ODI_MAPPING(String name,MapPhysicalNode... nodes) {
		Mapping rsp=this.stubOdiMapping(nodes);
		rsp.getName() >> name;
		return rsp;
	}
	/********************************************************************************************
	 *
	 *Experiment
	 *
	 ********************************************************************************************/
}
