package ro.bcr.bita.model

import ro.bcr.bita.proxy.odi.IOdiEntityFactory
import spock.lang.Specification
import oracle.odi.domain.mapping.IMapComponent
import oracle.odi.domain.mapping.MapComponentType
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.mapping.physical.MapPhysicalDesign
import oracle.odi.domain.mapping.physical.MapPhysicalNode
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiProjectFinder

//TODO sa ma gandesc la un DSL mai frumos ca sa creeze PhysicalNodes
class BitaMockModelFactory extends Specification{
	
	
	protected IOdiEntityFactory stbOdiEntityFactory;
	protected IMappingFinder stbOdiMappingFinder;
	protected IOdiFolderFinder stbOdiFolderFinder;
	protected IOdiProjectFinder stbOdiProjectFinder;
	
	protected setup() {
		stbOdiEntityFactory=Stub();
		stbOdiMappingFinder=Stub();
		stbOdiFolderFinder=Stub();
		stbOdiProjectFinder=Stub();
		
		stbOdiEntityFactory.createMappingFinder() >> stbOdiMappingFinder;
		stbOdiEntityFactory.createProjectFolderFinder() >> stbOdiFolderFinder;
		stbOdiEntityFactory.createProjectFinder() >> stbOdiProjectFinder;
		
	}

	/********************************************************************************************
	 *
	 *Not exposed methods
	 *
	 ********************************************************************************************/
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
	protected Mapping stubOdiMapping(MckCfgPhysicalNode... nodes) {
		Mapping odiObject=Stub();
		MapPhysicalDesign phyDsgn=Stub();
		
		odiObject.getPhysicalDesign(0) >> phyDsgn;
		
		def realNodes=nodes.collect{MckCfgPhysicalNode n-> return this.stubOdiMappingPhysicalNode(n)}

		
		phyDsgn.getPhysicalNodes() >> realNodes;
		
		return odiObject;
	}
	/********************************************************************************************
	 *
	 *Easy to use methods - DSL friendly
	 *
	 *
	 ********************************************************************************************/
	//TODO to review
	protected MckCfgPhysicalNode BITA_MOCK_ODI_PN() {
		return new MckCfgPhysicalNode()
	}
	
	protected MckCfgPhysicalNode BITA_MOCK_ODI_PN(Map params) {
		return new MckCfgPhysicalNode(params)
	}
	
	protected Mapping BITA_MOCK_ODI_MAPPING(MckCfgPhysicalNode... nodes) {
		return this.stubOdiMapping(nodes);
	}
	
	protected Mapping BITA_MOCK_ODI_MAPPING(String name,MckCfgPhysicalNode... nodes) {
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
