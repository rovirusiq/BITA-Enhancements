package ro.bcr.bita.odi.script;
import groovy.transform.Field

import java.util.logging.*;

import ro.bcr.bita.common.ComposedDelegator
import ro.bcr.bita.odi.template.IOdiBasicCommand
import ro.bcr.bita.odi.template.IOdiCommandContext
import ro.bcr.bita.odi.template.OdiBasicTemplate


//TODO To add standard finders: mapFinder, projectFinder, folderFinder,scenarioFinder,scenFolderFinder. Either in context either as global variables
//TODO to add execution timer
//TODO add service pt mapping that will give all the details about a mapping

class BitaBaseScript extends Script{
	
	public String messageFromBaseScriptInstance="Message From Base Script Instance";
	
	
	public Object run() {
			
		
			def OdiBasicTemplate odiTemplate;
			
			if (binding.hasVariable("odiInstance")) {
				odiTemplate=new OdiBasicTemplate.Builder(odiInstance).build();
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
