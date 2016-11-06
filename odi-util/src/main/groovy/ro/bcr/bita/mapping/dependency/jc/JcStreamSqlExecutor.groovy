package ro.bcr.bita.mapping.dependency.jc

import ro.bcr.bita.mapping.analyze.BitaMappingAnalyzeException;
import ro.bcr.bita.mapping.analyze.IMappingAnalyzeProcessor;
import ro.bcr.bita.model.IBitaModelFactory;
import ro.bcr.bita.model.IDependency
import ro.bcr.bita.model.IMappingDependencyRepositoryCyclicAware;
import ro.bcr.bita.model.IOdiMapping;
import ro.bcr.bita.model.MappingDependency;
import ro.bcr.bita.model.MappingDependencyRepository
import ro.bcr.bita.odi.proxy.BitaOdiException;
import ro.bcr.bita.sql.IBitaSqlSimpleOperations;

import groovy.transform.CompileStatic;

@CompileStatic
class JcStreamSqlExecutor implements IMappingAnalyzeProcessor{
	
	private final IMappingDependencyRepositoryCyclicAware repo;
	private final IBitaSqlSimpleOperations sqlExecutor;
	private final JcParameters params;
	private final JcSqlCommandsHelper sqlGenerator=new JcSqlCommandsHelper();
	private int commitCounter=0;
	private final int maxCommitCounter;
	private Boolean statusSqlBefore=false;
	
	public JcStreamSqlExecutor(IMappingDependencyRepositoryCyclicAware repository,IBitaSqlSimpleOperations sqlExecutor,JcParameters params,int frequencyOfCommits) {
		this.repo=repository;
		this.sqlExecutor=sqlExecutor;
		this.params=params;
		this.maxCommitCounter=frequencyOfCommits;
	}
	/********************************************************************************************
	 *
	 *Private
	 *
	 ********************************************************************************************/
	private void checkCounterAndCommit(int deltaCommits) {
		this.commitCounter+=deltaCommits;
		if (this.commitCounter>=this.maxCommitCounter) {
			this.sqlExecutor.commit();
			this.commitCounter=0;
		}
	}
	
	private void generateAndExecuteSqlForOneMapping(IOdiMapping mapping) {
		
		String jobId=this.params.generateJobId(mapping.getName());
		
		List<String> sqlJobDefinition=sqlGenerator.generateSqlJobDefinition(params,jobId,mapping.getName(),mapping.getLeadingSource(),mapping.getParameterListOfScenario(params.getScenarioVersion()));
		List<String> sqlAddToGroup=sqlGenerator.generateSqlGroupJobs(params,jobId);		
		
		this.sqlExecutor.executeInCurrentTransaction(sqlJobDefinition as String[]);
		this.sqlExecutor.executeInCurrentTransaction(sqlAddToGroup as String[]);
		
		this.checkCounterAndCommit(sqlJobDefinition.size()+sqlAddToGroup.size());
	}
	/********************************************************************************************
	 *
	 *Public
	 *
	 ********************************************************************************************/
	@Override
	public void processMapping(IOdiMapping mapping) {
		if (!this.statusSqlBefore) throw new BitaMappingAnalyzeException("The SQL statements for group definition were not executed. Please call before mapping analysis the method generateAndExecuteSqlBeforeAnalysis");
		try {
			mapping.identifySources().each{String tblName->
				repo.addTableToMapping(tblName,mapping.getName());
			}
			this.repo.addMappingToTable(mapping.getName(),mapping.identifyTarget());
			this.generateAndExecuteSqlForOneMapping(mapping);
			mapping=null;
		} catch (RuntimeException ex) {
			throw new BitaOdiException("An unexpected exception was encountered when processing the mapping: ${mapping}", ex);
		}
	}
	
	
	public generateAndExecuteSqlBeforeAnalysis() {
		String[] queries=sqlGenerator.generateSqlGroupDefinition(params);
		this.sqlExecutor.executeInCurrentTransaction(queries);
		this.checkCounterAndCommit(queries.length);
		this.statusSqlBefore=true;
	}
	
	public void generateAndExecuteSqlAfterAnalysis() {
		Set<IDependency> dpds=this.repo.getMappingDependenciesAndCheckCyclicDependencies();
		
		 for (IDependency dep:dpds) {
			MappingDependency mp=(MappingDependency) dep;
			
			String whoJobId=this.params.generateJobId(mp.who());
			String onJobId=this.params.generateJobId(mp.on());
			
			List<String> sql=this.sqlGenerator.generateSqlDependency(params,whoJobId,onJobId)
			this.sqlExecutor.executeInCurrentTransaction(sql as String[]);
			this.checkCounterAndCommit(sql.size());
		}
		 
		this.sqlExecutor.commit();
	}
	

}
