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
	private final JcSqlGeneratorHelper sqlGenerator=new JcSqlGeneratorHelper();
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
		this.sqlExecutor.executeInCurrentTransaction(sqlGenerator.generateSqlDepCleanup(params,mapping.getName()));
		this.sqlExecutor.executeInCurrentTransaction(sqlGenerator.generateSqlJobDefinition(params,mapping.getName()));
		this.sqlExecutor.executeInCurrentTransaction(sqlGenerator.generateSqlJobParameters(params,mapping.getName(),mapping.getLeadingSource()));
		this.sqlExecutor.executeInCurrentTransaction(sqlGenerator.generateSqlGroupJobs(params,mapping.getName()));
		this.checkCounterAndCommit(4);
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
		this.sqlExecutor.executeInCurrentTransaction(sqlGenerator.generateSqlGroupDefinition(params));
		this.checkCounterAndCommit(1);
		this.statusSqlBefore=true;
	}
	
	public void generateAndExecuteSqlAfterAnalysis() {
		Set<IDependency> dpds=this.repo.getMappingDependenciesAndCheckCyclicDependencies();
		
		 for (IDependency dep:dpds) {
			MappingDependency mp=(MappingDependency) dep;
			this.sqlExecutor.executeInCurrentTransaction(this.sqlGenerator.generateSqlDependency(params,mp.who(),mp.on()));
			this.checkCounterAndCommit(1);
		}
		 
		this.sqlExecutor.commit();
	}
	

}
