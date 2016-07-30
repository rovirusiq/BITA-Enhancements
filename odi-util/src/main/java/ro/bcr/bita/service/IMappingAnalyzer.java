package ro.bcr.bita.service;

import ro.bcr.bita.odi.proxy.OdiPathUtil;


/**
 * @author Andrei.Popovici
 * Interface for the Analyzer Service. It is function is to search all the provided ODI Paths,
 * extract the mappings and send each one to the subscribed processors. Each processor will receive
 * an OdiMapping object which can use how ever it wants to retrieve information needed about the OdiMapping.
 * It is not allowed to modify the ODIMapping object.
 * @see IMappingAnalyzeProcessor
 * @see OdiPathUtil
 */
public interface IMappingAnalyzer {
	
	/**
	 * @param processor The processor to be added to the analyze.
	 * The processor is not allowed to modify the ODIMapping that it is receiving.
	 * 
	 * @see IMappingAnalyzeProcessor
	 */
	public void addAnalyzeProcessor(IMappingAnalyzeProcessor processor);
	/**
	 * @param odiPaths The paths from where to retrieve the mappings
	 * @see OdiPathUtil
	 */
	public void analyzeMappingsFrom(String... odiPaths);
	

}
