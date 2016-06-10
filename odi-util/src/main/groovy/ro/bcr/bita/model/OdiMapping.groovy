package ro.bcr.bita.model

import java.io.Serializable;
import java.util.List
import oracle.odi.domain.mapping.IMapComponent
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.mapping.physical.MapPhysicalNode

//TODO sa adaug exceptiile la defintitia interfetei si a clasei
class OdiMapping implements IOdiMapping {
	
	
	private boolean isAnalyzed=false;
	private List<String> sourceTables=[];
	private String targetTable;
	
	
	private @Delegate Mapping odiObject;
	
	
	
	protected OdiMapping(Mapping object) {
		if (object==null) throw new BitaModelException("Mapping parameter cannot be null for constructor [OdiMapping(oracle.odi.domain.mapping.Mapping)]");
		this.odiObject=object;
	}
	
	/********************************************************************************************
	 *
	 *Private
	 *
	 ********************************************************************************************/
	protected boolean isAnalyzed() {
		return this.isAnalyzed;
	}
	
	protected void addToSourceTables(String name) {
		this.sourceTables.add(name);
	}
	
	protected setTargetTable(String name) {
		this.targetTable=name;
	}
	
	protected getTargetTable() {
		return this.targetTable;
	}
	protected void analyze() throws BitaModelException {
		if (this.isAnalyzed) return;
		
		int counterForTarget=0;
		
		odiObject.getPhysicalDesign(0).getPhysicalNodes().each {MapPhysicalNode vNode->
		  IMapComponent lComponent=vNode.getLogicalComponent()
		  boolean isDataStorage=lComponent.getComponentType().isDataStorageComponent();
		  
		  int sizeUp=vNode.getUpstreamConnectedNodes().size();
		  int sizeDown=vNode.getDownstreamConnectedNodes().size();
		  
		  boolean isSource=(sizeUp<=0) && (sizeDown>=1);
		  boolean isTarget=(sizeDown<=0) && (sizeUp>=1);
		  
		  if (isDataStorage){
		   
			  if (isSource) this.addToSourceTables(lComponent.getBoundObjectName());
		   
			  if (isTarget) {
				  counterForTarget++;
				  if (counterForTarget>1) throw new BitaModelException("More than one target have been identified for the mapping[${this.odiObject.name}]: ${this.getTargetTable()},${lComponent.getBoundObjectName()} ");
				  this.setTargetTable(lComponent.getBoundObjectName());
			  }
		  
		  }
		
		}
		
		if (this.sourceTables.size()<=0) throw new BitaModelException("No source table was identified for the mapping mapping[${this.odiObject.name}]");
		if (counterForTarget<=0) throw new BitaModelException("No target table was identified for the mapping[${this.odiObject.name}]");
		this.isAnalyzed=true;
	}
	/********************************************************************************************
	 *
	 *Public
	 *
	 ********************************************************************************************/
	@Override
	public List<String> identifySources() throws BitaModelException  {
		this.analyze();
		return this.sourceTables.collect{it};
	}

	@Override
	public String identifyTarget() throws BitaModelException {
		this.analyze();
		return this.targetTable;
	}

	@Override
	public Number getInternalId() throws BitaModelException {
		return this.odiObject.getInternalId();
	}

	@Override
	public String getGlobalId() throws BitaModelException {
		return this.odiObject.getGlobalId();
	}
	
	@Override
	public String toString() {
		return """OdiMapping[${odiObject.getName()}]""";
	}

}
