package ro.bcr.bita.odi.script;
import ro.bcr.bita.model.BitaModelFactory;
import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.proxy.OdiBasicPersistenceService
import ro.bcr.bita.odi.proxy.OdiEntityFactory;
import ro.bcr.bita.odi.template.OdiBasicTemplate

import groovy.transform.Field

import java.util.logging.*


//TODO To add standard finders: mapFinder, projectFinder, folderFinder,scenarioFinder,scenFolderFinder. Either in context either as global variables
//TODO to add execution timer
//TODO add service pt mapping that will give all the details about a mapping

class BitaBaseScript extends Script{
	
	public String messageFromBaseScriptInstance="Message From Base Script Instance";
	
	
	public Object run() {
			
		
			def OdiBasicTemplate odiTemplate;
			
			if (binding.hasVariable("odiInstance")) {
				
				IOdiEntityFactory odiEntityFactory=OdiEntityFactory.createInstance(odiInstance,BitaModelFactory.newInstance());
				
				odiTemplate=odiEntityFactory.newOdiTemplate();
			}
			
		
			def BitaContext gBita=new BitaContext(odiTemplate);
					
			/*
			 * Main Configuration block
			 * Sub-blocks of this ones are in BitaContex.ExecutionContext
			 *
			 */
			config=gBita.executeWithDelegators.curry([gBita.getConfigContext()],BitaContext.DELEGATORS_ADD_SUITE_AFTER);
			
			
			/*
			 * Main Execution block
			 * Sub-blocks of this ones are in BitaContex.ExecutionContext
			 * 
			 */
			execute=gBita.executeWithDelegators.curry([gBita.getExecutionContext()],BitaContext.DELEGATORS_ADD_SUITE_AFTER);
			
	}
}
