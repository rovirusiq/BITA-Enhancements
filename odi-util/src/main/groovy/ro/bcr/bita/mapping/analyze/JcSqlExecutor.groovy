package ro.bcr.bita.mapping.analyze

import ro.bcr.bita.app.BitaAppFactory
import ro.bcr.bita.mapping.analyze.IJcGroupIdentificationStrategy
import ro.bcr.bita.mapping.analyze.IMappingAnalyzeExtendedProcessor
import ro.bcr.bita.mapping.analyze.IMappingAnalyzeProcessor
import ro.bcr.bita.model.BitaDomainFactory
import ro.bcr.bita.model.IDependency
import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.model.MappingDependency
import ro.bcr.bita.sql.IBitaSqlExecutor
import groovy.transform.CompileStatic
import java.util.Set

//TODO: Trebuie sa retina toata obiectele de OdiMapping. sa ma asigur ca nu imapcteaza performanatas

@CompileStatic
public class JcSqlExecutor implements IMappingAnalyzeExtendedProcessor{
	
	private MappingDependencyAnalyzerProcessor dependencyAnalyzer;
	private final JcRequestContext params;
	private IBitaSqlExecutor bitaSqlExecutor;
	private final JcSqlCommandsGenerator sqlGenerator;
	
	public JcSqlExecutor(BitaDomainFactory bitaDomainFactory,JcRequestContext params,IBitaSqlExecutor bitaSqlExecutor) {
		this.dependencyAnalyzer=bitaDomainFactory.newMappingDependencyAnalyzerProcessor();
		this.params=params;
		this.bitaSqlExecutor=bitaSqlExecutor;
		this.sqlGenerator=bitaDomainFactory.newJcSqlCommandGenerator();
	}
	
	
	/********************************************************************************************
	 *
	 *Private methods
	 *
	 ********************************************************************************************/
	private void generateAndExecuteSqlForOneMapping(IOdiMapping mapping) {
		
		String jobId=this.params.generateJobId(mapping.getName());
		
		List<String> sqlJobDefinition=sqlGenerator.generateSqlJobDefinition(params,jobId,mapping.getName(),mapping.getLeadingSource(),mapping.getParameterListOfScenario(params.getScenarioVersion()));
				
		this.bitaSqlExecutor.executeInCurrentTransaction(sqlJobDefinition as String[]);
		
	}
	
	/********************************************************************************************
	 *
	 *IMappingAnalyzeExtendedProcessor
	 *
	 ********************************************************************************************/
	@Override
	public void processMapping(IOdiMapping mapping) {
		dependencyAnalyzer.processMapping(mapping);
		generateAndExecuteSqlForOneMapping(mapping);
	}

	@Override
	public void beforeAnalyze() {
		
	}

	@Override
	public void afterAnalyze() {
		Set<MappingDependency> dpds=this.dependencyAnalyzer.getDependencies();
		 for (MappingDependency dep:dpds) {
			
			String whoJobId=this.params.generateJobId(dep.who());
			String onJobId=this.params.generateJobId(dep.on());
			
			List<String> sql=this.sqlGenerator.generateSqlDependency(params,whoJobId,onJobId)
			this.bitaSqlExecutor.executeInCurrentTransaction(sql);
		}
		this.params.createGroups(this.dependencyAnalyzer,this.bitaSqlExecutor);
		
	}

}
