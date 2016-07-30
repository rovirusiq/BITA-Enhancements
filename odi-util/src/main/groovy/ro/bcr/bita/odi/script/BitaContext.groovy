package ro.bcr.bita.odi.script

import ro.bcr.bita.common.ComposedDelegator
import ro.bcr.bita.model.MessageCollectionContainer
import ro.bcr.bita.odi.template.IOdiBasicCommand
import ro.bcr.bita.odi.template.IOdiCommandContext
import ro.bcr.bita.odi.template.OdiBasicTemplate

/********************************************************************************************
 *
 *Bita Config and Generic Context
 *
 ********************************************************************************************/

class BitaContext {
	
	private MessageCollectionContainer msgCollections;
	private ScriptLogger scriptLogger;
	private GlobalHolder globalHolder;
	private OdiBasicTemplate odiTemplate=null;

	private MessageCollectionContainerScriptHelper msgExposed;
	
	public static final Integer DELEGATORS_ADD_SUITE_BEFORE=1;
	public static final Integer DELEGATORS_ADD_SUITE_AFTER=2;
	public static final Integer DELEGATORS_NO_SUIT=3;
	
	
	protected Closure configGlobalProperties;
	protected Closure configGlobalMessages;
	
	
	public BitaContext(){
		this.msgCollections=new MessageCollectionContainer();
		this.msgCollections.createCollection("msgStd");
		this.msgCollections.createCollection("msgErr");
		
		this.scriptLogger=new ScriptLogger();
		
		this.globalHolder=new GlobalHolder();
		
		this.msgExposed=new MessageCollectionContainerScriptHelper(this.msgCollections);
		
		this.configGlobalProperties=executeWithDelegators.curry([new GlobalHolderConfigHelper(this.getGlobalHolder())],BitaContext.DELEGATORS_ADD_SUITE_BEFORE);
		this.configGlobalMessages=executeWithDelegators.curry([new MessageCollectionContainerConfigHelper(this.getMsgCollections())],BitaContext.DELEGATORS_ADD_SUITE_BEFORE);
	}
	
	
	private boolean checkOdiInfrastructure(boolean enforce=true) throws IllegalStateException{
		
		if (this.odiTemplate==null) {
			 
			if (enforce) throw new IllegalStateException("The ODI infrastructure was not initialized");
			 
			 return false;
		}
		return true;
	}
	
	public BitaContext(OdiBasicTemplate param){
		this();
		this.odiTemplate=param;
	}
	
	public getExecutionContext() {
		return new BitaExecutionContext();
	}
	
	public getConfigContext() {
		new BitaConfigContext();
	}

	/**
	 * @return the msgCollections
	 */
	public MessageCollectionContainer getMsgCollections() {
		return msgCollections;
	}

	/**
	 * @return the odiTemplate
	 */
	public OdiBasicTemplate getOdiTemplate() {
		return odiTemplate;
	}



	/**
	 * @return the scriptLogger
	 */
	public ScriptLogger getScriptLogger() {
		return scriptLogger;
	}


	/**
	 * @return the globalHolder
	 */
	public GlobalHolder getGlobalHolder() {
		return globalHolder;
	}


	/**
	 * @return the exposedToScript
	 */
	public Map<String, Object> getExposedToScript() {
		 def rsp=["msgAll":this.msgExposed]
		 
		 Set cIds=this.msgExposed.getCollectionIds();
		 
		 for (id in cIds) {
			 rsp[id]=this.msgExposed[id];
		 }
		 
		 return rsp;
	}

	/**
	 * @return the lstStandardDelegators
	 */
	public List<Object> getStandardDelegators() {
		return [this.getScriptLogger(),this.getGlobalHolder(),this.getExposedToScript()];
	}
	
	protected List addStandardDelegators(List toList){
		for (e in this.getStandardDelegators()) {
			if (!toList.contains(e)) toList<<e;
		}
		return toList;
	}
	
	/********************************************************************************************
	 *
	 *Exposed Closures
	 *
	 ********************************************************************************************/	
	public Closure createDelegatorOutOf= {List x,Integer addSuite=DELEGATORS_ADD_SUITE_AFTER ->
		def y=[];
		
		if (addSuite==DELEGATORS_ADD_SUITE_BEFORE) this.addStandardDelegators(y);
		
		if ((x!=null) && (x.size()>0)) {
			y.addAll(x);
		}
		
		if (addSuite==DELEGATORS_ADD_SUITE_AFTER) this.addStandardDelegators(y);
		
		new ComposedDelegator(y);
	}
	
	public Closure executeWithDelegators= {List delegators,Integer addSuite=DELEGATORS_ADD_SUITE_AFTER,Closure commandsToExecute->
		try {
			commandsToExecute.delegate=this.createDelegatorOutOf(delegators,addSuite);
			this.scriptLogger.logInfo("Delegator:"+commandsToExecute.delegate);
			commandsToExecute.resolveStrategy=Closure.DELEGATE_ONLY;
			commandsToExecute.call();
		} catch (MissingPropertyException ex) {
			throw new BitaScriptException("Scripting exception:",ex,true,false);
		} catch (MissingMethodException ex) {
			throw new BitaScriptException("Scripting exception:",ex,true,false);
		}
		
		
	}
	
	/********************************************************************************************
	 *
	 *Bita Config Context
	 *
	 ********************************************************************************************/
	private class BitaConfigContext{
		
		
		private BitaConfigContext() {
			
		}
		
		
		public configProp(Closure actionToExecute) {
			super.scriptLogger.logInfo("Started block[configProp]");
			super.configGlobalProperties(actionToExecute);
			super.scriptLogger.logInfo("Ended block[configProp]");
		}
		public configLog(Closure actionToExecute){
			super.scriptLogger.logInfo("Started block[configLog]");
			ScriptLogger.Config conf=super.getScriptLogger().createCopyOfConfiguration();
			super.executeWithDelegators([conf],BitaContext.DELEGATORS_ADD_SUITE_AFTER,actionToExecute);
			super.getScriptLogger().configure(conf);
			super.scriptLogger.logInfo("Ended block[configLog]");
		}
		
		
		public configMessages(Closure actionToExecute) {
			super.scriptLogger.logInfo("Started block[configMessages]");
			super.configGlobalMessages actionToExecute;
			super.scriptLogger.logInfo("Ended block[configMessages]");
		}
		
	}
	
	/********************************************************************************************
	 *
	 *Bita Execution Context
	 *
	 ********************************************************************************************/
	private class BitaExecutionContext{
		/*
		 * This object mus not have any property or method
		 * except the onse that must be visible from the 
		 * execution block
		 * Also it has to have access to the BitContext variables
		 */
		private BitaExecutionContext() {
			
		}
		
		public doAs (String name="Anonymous_Block", Closure actionToExecute) {
			super.scriptLogger.logInfo("Started block - "+name);
			super.executeWithDelegators ([this],DELEGATORS_ADD_SUITE_AFTER,actionToExecute);
			super.scriptLogger.logInfo("Ended block - "+name);
		}
		
		public doAs (Map input) {
			boolean v=true;
			if (input.size()>1) v=false;
			String k=input.keySet()[0];
			if (!(input[k] instanceof Closure)) v=false;
			if (!v) throw new IllegalArgumentException("The call for the methods start from ExecutionContext must have a name and a {}  block");
			doAs(k,input[k]);
		}
		
		public withoutOdiTransaction(Closure actionToExecute) throws IllegalStateException{
			
			super.scriptLogger.logInfo("Started block[withoutOdiTransaction]");
			
			super.checkOdiInfrastructure(true);//throws IllegalStateException
			
			IOdiBasicCommand issuedCommand=new IOdiBasicCommand() {
				public void execute (IOdiCommandContext ctx) {
					super.executeWithDelegators ([ctx],BitaContext.DELEGATORS_ADD_SUITE_AFTER,actionToExecute);
				}
			}
			super.odiTemplate.executeWithoutTransaction(issuedCommand);
			
			super.scriptLogger.logInfo("Ended block[withoutOdiTransaction]");
		};
		
		public inOdiTransaction(Closure actionToExecute) {
			
			super.scriptLogger.logInfo("Started block[inOdiTransaction]");
			
			super.checkOdiInfrastructure(true);//throws IllegalStateException
			
			IOdiBasicCommand issuedCommand=new IOdiBasicCommand() {
				public void execute (IOdiCommandContext ctx) {
					
					super.executeWithDelegators ([ctx],BitaContext.DELEGATORS_ADD_SUITE_AFTER,actionToExecute);
					
				}
			}
			
			super.odiTemplate.executeInTransaction(issuedCommand);
			
			super.scriptLogger.logInfo("Ended block[inOdiTransaction]");
		};
		
	}
	
	
	
	
	
	
	
}
