package ro.bcr.bita.mapping.analyze;

public interface IMappingAnalyzeExtendedProcessor extends IMappingAnalyzeProcessor {

		
	/**
	 * Method that is called before starting the analyze of the mappings
	 */
	public abstract void beforeAnalyze();
	/**
	 * Method that is called after the analyze of the mappings has been finished
	 */
	public abstract void afterAnalyze();
}
