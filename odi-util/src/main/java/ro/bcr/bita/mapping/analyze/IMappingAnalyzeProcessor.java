package ro.bcr.bita.mapping.analyze;

import ro.bcr.bita.model.IOdiMapping;

/**
 * @author Andrei.Popovici
 * A IMappingAnalyzeProcessor is used to analyze one mapping. Such a processor will have its method
 * processMapping called for each of the mappings that are the subject of analysis. The processor
 * must not modify in any way the object that it is receiving as a parameter.
 */
public interface IMappingAnalyzeProcessor {
	
	/**
	 * @param mapping The mapping to be analyzed. The object received it must not be modified.
	 */
	public void processMapping(IOdiMapping mapping);

}
