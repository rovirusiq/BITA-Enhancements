package ro.bcr.bita.test.odi

import oracle.odi.core.OdiInstance
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.runtime.scenario.OdiScenario
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder
import oracle.odi.generation.IOdiScenarioGenerator
import oracle.odi.generation.support.OdiScenarioGeneratorImpl
import ro.bcr.bita.utils.IOdiCommand
import ro.bcr.bita.utils.OdiTemplate
import ro.bcr.bita.utils.OdiTemplateException

class MapScenarioLocator implements IOdiScenarioLocator{
	
	private static final String SCENARIO_VERSION_FOR_TESTING="099";
	
	private OdiTemplate odiTemplate;
	
	public MapScenarioLocator(OdiTemplate odiTemplate) {
		this.odiTemplate=odiTemplate;
	}

	@Override
	public OdiScenarioUnderTest locateScenario(String... identifiers) throws OdiTestingException {
		
		if (identifiers.length!=2) throw new OdiTestingException("In order to locate the scenario one needs to provide 2 parameters: 1. Name of the project; 2. Name of the map");
		
		
		final String MAPPING_UNDER_TEST=identifiers[1];
		final String PROJECT_WITH_MAP=identifiers[0];
		final OdiScenarioUnderTest IDENTIFIED_SCENARIO=new OdiScenarioUnderTest();
		
		try {
			odiTemplate.executeInTransaction(
				new IOdiCommand() {
					public void execute(OdiInstance odiInstance) throws OdiTemplateException{
						
						IMappingFinder mapFinder=(IMappingFinder) odiInstance.getTransactionalEntityManager().getFinder(Mapping.class);
						IOdiScenarioFinder scenFinder=(IOdiScenarioFinder) odiInstance.getTransactionalEntityManager().getFinder(OdiScenario.class);
						IOdiScenarioGenerator generator = new OdiScenarioGeneratorImpl(odiInstance);
						
						
						Collection<Mapping> colMaps=mapFinder.findByName(MAPPING_UNDER_TEST,PROJECT_WITH_MAP);
						
						if (colMaps.size()!=1) throw new RuntimeException("Map["+MAPPING_UNDER_TEST+"] cannot be found in project["+PROJECT_WITH_MAP+"]");
						
						Mapping mapOfInterest=colMaps.iterator().next();
						
						
						Collection<OdiScenario> colScenarios=scenFinder.findBySourceMapping(mapOfInterest.getInternalId());
						
						OdiScenario scenarioOfInterest=colScenarios.find{OdiScenario elem->
							SCENARIO_VERSION_FOR_TESTING==elem.getVersion();
						}
						
						OdiScenario generatedScenario=null;
						if (scenarioOfInterest) {
							generator.regenerateScenario(scenarioOfInterest);
							generatedScenario=scenarioOfInterest;
						} else {
							generatedScenario=generator.generateScenario(mapOfInterest,mapOfInterest.getName(),SCENARIO_VERSION_FOR_TESTING);
						}
								
						if (null==generatedScenario) throw new RuntimeException("The ODIScenario could not be identified.");
						
						IDENTIFIED_SCENARIO.setName(generatedScenario.getName());
						IDENTIFIED_SCENARIO.setVersion(generatedScenario.getVersion());
						
					}
				}
			);
		} catch (Exception ex) {
				throw new OdiTestingException("An error has occured when searching for the map["+MAPPING_UNDER_TEST+"] in the project["+PROJECT_WITH_MAP+"]",ex);
		}
		return IDENTIFIED_SCENARIO;
	}
	

}
