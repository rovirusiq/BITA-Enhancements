package ro.bcr.bita.model

import groovy.transform.CompileStatic;
import groovy.transform.TypeChecked;

import java.io.Serializable;
import java.util.List

import oracle.odi.domain.mapping.IMapComponent
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.mapping.physical.MapPhysicalNode

@TypeChecked
class OdiMapping implements IOdiMapping {
	
	
	private boolean isAnalyzed=false;
	private List<String> sourceTables=[];
	private String targetTable;
	private String leadingSource="XXX_DMY";
	
	
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
	
	protected void setLeadingSource(String leadingSource) {
		this.leadingSource = leadingSource;
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
		   
			  String lName=lComponent.getBoundObjectName();
			  if (lName==null) throw new BitaModelException("No Bound Object for DataStorage["+lComponent+"] can be identified in map:"+this.getName()); 
			  
			  if (isSource) {
				  this.addToSourceTables(lName);
				  String name2=lName;
				  for (String x:["LZ_","LC_","TEMP_","ST_"]) {
					  if (name2.indexOf(x)==0) {
						  name2=name2-x;
						  break;
					  }
				  }
				  if (this.getName().indexOf(name2)==4) {
					  this.setLeadingSource(lName);
				  }
				  
			  }
			  if (isTarget) {
				  counterForTarget++;
				  if (counterForTarget>1) throw new BitaModelException("More than one target have been identified for the mapping[${this.getName()}]: ${this.getTargetTable()},${lComponent.getBoundObjectName()} ");
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
	public String getName() {
		return odiObject.getName();
	}
	
	@Override
	public String toString() {
		return """OdiMapping[${odiObject.getName()}]""";
	}

	@Override
	public String getLeadingSource() {
		return leadingSource;
	}
	public Mapping getUnderlyingOdiObject() {
		return this.odiObject;
	}

	@Override
	public String getFullPathName() throws BitaModelException {
		String rsp="";
		try {
			rsp=this.odiObject.getProject().getName()+"/"+this.odiObject.getParentFolder().getName()+"/"+this.getName();
		} catch (Exception ex) {
			throw new BitaModelException("An exception occured when calculaitng the full name of the mapping",ex);
		}
		return rsp;
	}


	
	
	

}
