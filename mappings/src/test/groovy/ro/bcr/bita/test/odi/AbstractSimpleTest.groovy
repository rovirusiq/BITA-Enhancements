package ro.bcr.bita.test.odi

import oracle.odi.core.OdiInstance
import oracle.odi.domain.finder.IFinder;
import oracle.odi.domain.mapping.Mapping;
import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.runtime.scenario.OdiScenario
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder
import oracle.odi.runtime.agent.invocation.StartupParams;
import oracle.odi.domain.mapping.finder.IMappingFinder;
import oracle.odi.generation.IOdiScenarioGenerator
import oracle.odi.generation.support.OdiScenarioGeneratorImpl
import oracle.odi.runtime.agent.RuntimeAgent
import oracle.odi.runtime.agent.invocation.ExecutionInfo

import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.ITable
import org.dbunit.dataset.excel.XlsDataSet
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ro.bcr.bita.model.MappingLine;
import ro.bcr.bita.utils.IOdiCommand
import ro.bcr.bita.utils.OdiTemplate

import org.dbunit.Assertion;
import org.dbunit.IDatabaseTester
import org.dbunit.JdbcDatabaseTester;

class AbstractSimpleTest {
	
	private OdiTemplate odiTemplate;
	public static final SCENARIO_VERSION_FOR_TESTING="099";
	public static final MAPPING_UNDER_TEST="Subject1";
	public static final PROJECT_WITH_MAP="TESTING_FRAMEWORK"

	private static final String ODI_USERNAME = "SUPERVISOR"

	private static final String ODI_PASSWORD = "par0la123";
	
	
	private static IDatabaseTester tester;
	
	
	
	protected OdiTemplate.Builder createBuilder(){
		OdiTemplate.Builder builder=new OdiTemplate.Builder();
		builder.setJdbcDriverClassName("oracle.jdbc.OracleDriver");
		builder.setJdbcUrl("jdbc:oracle:thin:@vmdb:1521:XE");
		builder.setMasterRepositoryUsername("ODI_REPO");
		builder.setMasterRepositoryPassword(ODI_PASSWORD);
		builder.setWorkRepositoryName("WORKREP");
		builder.setOdiUsername(ODI_USERNAME);
		builder.setOdiPassword(ODI_PASSWORD);
	}
	
	private String getInputXlsForMapExecution(String mapName) {
		return mapName+"_Input.xls";	
	}
	
	private String getOutputXlsForMapExecution(String mapName) {
		return mapName+"_Expected.xls";
	}
	
	@Before
	public void beforeTest() {
		odiTemplate=this.createBuilder().build();
		tester=new CustomOracleDatabaseTester("oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@vmdb:1521:XE", "ODI_STG", "par0la123");
	}
	
	@After
	public void afterTest() {
		tester.setTearDownOperation(DatabaseOperation.TRUNCATE_TABLE);
		tester.onTearDown();
		odiTemplate.cleanUp();
	}
	
	
	
	@Test
	public void testCeva() {
		odiTemplate.executeInTransaction(
			new IOdiCommand() {
				public void execute(OdiInstance odiInstance) {
					
					
					IMappingFinder mapFinder=(IMappingFinder) odiInstance.getTransactionalEntityManager().getFinder(Mapping.class);
					IOdiScenarioFinder scenFinder=(IOdiScenarioFinder) odiInstance.getTransactionalEntityManager().getFinder(OdiScenario.class);
					IOdiScenarioGenerator generator = new OdiScenarioGeneratorImpl(odiInstance);
					
					
					Collection<Mapping> colMaps=mapFinder.findByName(MAPPING_UNDER_TEST,PROJECT_WITH_MAP);
					
					
					assert 1==colMaps.size();
					
					Mapping mapOfInterest=colMaps.iterator().next();
					
					
					Collection<OdiScenario> colScenarios=scenFinder.findBySourceMapping(mapOfInterest.getInternalId());
					
					OdiScenario scenarioOfInterest=colScenarios.find{OdiScenario elem->
						SCENARIO_VERSION_FOR_TESTING==elem.getVersion();
					}
					
					OdiScenario generatedScenario=null;
					if (scenarioOfInterest) {
						println "Regenerate";
						generator.regenerateScenario(scenarioOfInterest);
						generatedScenario=scenarioOfInterest;
					} else {
						println "Create";
						generatedScenario=generator.generateScenario(mapOfInterest,mapOfInterest.getName(),SCENARIO_VERSION_FOR_TESTING);
					}
							
					assert null!=generatedScenario;
					
					
					
				}
			}
		);
	
		XlsDataSet inputDataSet=new XlsDataSet(new File("src/test/resources/"+getInputXlsForMapExecution(MAPPING_UNDER_TEST)));
		XlsDataSet expectedDataSet=new XlsDataSet(new File("src/test/resources/"+getOutputXlsForMapExecution(MAPPING_UNDER_TEST)));
		
		tester.setDataSet(inputDataSet);
		
		
		tester.onSetup();
	
		odiTemplate.executeWithoutTransaction(
			new IOdiCommand() {
				public void execute(OdiInstance odiInstance) {
					RuntimeAgent runtimeAgent=new RuntimeAgent(odiInstance,ODI_USERNAME,ODI_PASSWORD.toCharArray());
					println "TS: "+System.currentTimeMillis();
					ExecutionInfo execInfo=runtimeAgent.startScenario(MAPPING_UNDER_TEST,SCENARIO_VERSION_FOR_TESTING,new StartupParams(),"","GLOBAL",5,"",true);
					println "ES: "+System.currentTimeMillis();
					println "Execution Code:"+execInfo.getReturnCode();
					assert 0==execInfo.getReturnCode();
				}
			}
		);
	
		ITable expectedTable=expectedDataSet.getTable("POC.T_ORD");

		IDataSet actualDataSet=tester.getConnection().createDataSet("POC.T_ORD");
		ITable actualTable=actualDataSet.getTable("POC.T_ORD");
		ITable filteredActualTable = DefaultColumnFilter.includedColumnsTable(actualTable,expectedTable.getTableMetaData().getColumns())
		Assertion.assertEquals(expectedTable,filteredActualTable);
	}
	
	

}
