package ro.bcr.bita.model

import groovy.transform.CompileStatic;
import groovy.transform.TypeChecked;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List

import bsh.This;
import oracle.odi.domain.mapping.IMapComponent
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.mapping.physical.MapPhysicalNode

import ro.bcr.bita.odi.proxy.BitaOdiException;
import ro.bcr.bita.odi.proxy.IOdiEntityFactory
import ro.bcr.bita.odi.proxy.IOdiOperationsService;

@TypeChecked
class OdiMapping implements IOdiMapping {
	
	
	private static final String LZ_PREFIX="LZ_";
	private static final String BASE_PREFIX="LC_";
	private static final String TEMP_PREFIX="TEMP_";
	private static final String STAGE_PREFIX="ST_";
	
	private static final List<String> DATA_ACQUISITON_PREFIXES=[LZ_PREFIX,BASE_PREFIX];
	private static final List<String> SOURCES_PREFIXES=[LZ_PREFIX,BASE_PREFIX,TEMP_PREFIX,STAGE_PREFIX];
	
	
	private boolean isAnalyzed=false;
	private List<String> sourceTables=[];
	private List<String> targetTables=[];
	private String leadingSource="XXX_DMY";
	private boolean isDataAcquisitionMapping=false;
	private final boolean allowsMultipleTargets;
	
	
	

	private @Delegate Mapping odiObject;
	private final IBitaDomainFactory bitaFactory;
	
	
	protected OdiMapping(Mapping object,IBitaDomainFactory bitaFactory) {
		this(object,bitaFactory,false);
	}
	
	protected OdiMapping(Mapping object,IBitaDomainFactory bitaFactory,boolean allowsMultipleTargets) {
		if (object==null) throw new BitaModelException("Mapping parameter cannot be null for constructor [OdiMapping(oracle.odi.domain.mapping.Mapping)]");
		if (bitaFactory==null) throw new BitaModelException("BitaFactory parameter cannot be null for constructor [OdiMapping(oracle.odi.domain.mapping.Mapping)]");
		this.odiObject=object;
		this.bitaFactory=bitaFactory;
		this.allowsMultipleTargets=allowsMultipleTargets;
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
	
	protected void addTargetTable(String name) {
		this.targetTables << name;
	}
	
	
	protected List<String> getTargetTables() {
		return this.targetTables;
	}
	
	protected void setLeadingSource(String leadingSource) {
		this.leadingSource = leadingSource;
	}
	
	
	protected void analyzeLeadingSource() {
		
		if (this.sourceTables.size()<=0) {
			return;
		}
		
		if (this.sourceTables.size()==1) {
			this.setLeadingSource(this.sourceTables[0]);
			return;
		}
		
		boolean wasSet=false;
		
		String mappingName=this.getName();
		for (String lName:this.sourceTables) {
			String name2=lName;
			for (String x:SOURCES_PREFIXES) {
				if (name2.indexOf(x)==0) {
					name2=name2-x;
					break;
				}
			}
			if (mappingName.indexOf(name2)==4) {
				this.setLeadingSource(lName);
				wasSet=true;
			}
		}
		if (!wasSet) {
			this.setLeadingSource(this.sourceTables[0]);
		}
	}
	
	protected Boolean getAllowsMultipleTargets() {
		return this.allowsMultipleTargets;
	}
	
	
	protected void analyzeTypeOfMapping() {
		boolean foundPrefix=false;
		if ((this.sourceTables.size()==1) && (this.getTargetTables().size()==1) ){
			DATA_ACQUISITON_PREFIXES.each{String s->
				if (this.getTargetTables()[0].startsWith(s)) foundPrefix=true;
			}
			
		} 
		this.isDataAcquisitionMapping=foundPrefix;
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
			  if (lName.startsWith("#")) {//IF IT IS FILE
				  lName=lComponent.getName();
			  }
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
				  
				  
			  }
			  if (isTarget) {
				  counterForTarget++;
				  if ((counterForTarget>1) && (!this.getAllowsMultipleTargets()) ) throw new BitaModelException("More than one target have been identified for the mapping[${this.getName()}]: ${this.getTargetTables()},${lComponent.getBoundObjectName()} ");
				  this.addTargetTable(lComponent.getBoundObjectName());
			  }
		  
		  }
		
		}
		
		if (this.sourceTables.size()<=0) throw new BitaModelException("No source table was identified for the mapping mapping[${this.odiObject.name}]. This may also be a sign that the map has somethin incorrect not necesserly that it has no source.");
		if (counterForTarget<=0) throw new BitaModelException("No target table was identified for the mapping[${this.odiObject.name}]");
		
		this.analyzeLeadingSource();
		this.analyzeTypeOfMapping();
		
		this.isAnalyzed=true;
	}
	
	protected IOdiScenario getScenario(String version) throws BitaModelException {
		IOdiOperationsService odiOperationsService=this.bitaFactory.newOdiOperationsService();
		IOdiScenario scen=null;
		try {
			scen=odiOperationsService.findScenarioForMapping(this,version);
		} catch (BitaOdiException ex) {
			throw new BitaModelException("Exception occurend in the model",ex);
		}
		return scen;
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
		return this.targetTables[0];
	}
	
	@Override
	public List<String> identifyTargets() throws BitaModelException {
		this.analyze();
		return this.targetTables.collect{it};
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
	
	@Override
	public boolean isDataAcquisitionMapping() {
		return isDataAcquisitionMapping;
	}

	@Override
	public List<String> getParameterListOfScenario(String version) {
		IOdiScenario scen=this.getScenario(version);
		return scen.getParameterList();
	}
	
	


	
	
	

}
