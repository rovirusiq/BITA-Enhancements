package ro.bcr.bita.mapping.dependency.jc

import ro.bcr.bita.mapping.analyze.BitaMappingAnalyzeException;
import ro.bcr.bita.mapping.analyze.IMappingAnalyzeProcessor;
import ro.bcr.bita.model.IBitaModelFactory;
import ro.bcr.bita.model.IDependency
import ro.bcr.bita.model.IMappingDependencyRepositoryCyclicAware;
import ro.bcr.bita.model.IOdiMapping;
import ro.bcr.bita.model.MappingDependency;
import ro.bcr.bita.model.MappingDependencyRepository
import ro.bcr.bita.sql.IBitaSqlSimpleOperations;

import groovy.transform.CompileStatic;

@CompileStatic
class JcStreamSqlExecutor implements IMappingAnalyzeProcessor{
	
	private final IMappingDependencyRepositoryCyclicAware repo;
	private final IBitaSqlSimpleOperations sqlExecutor;
	private final JcParameters params;
	private final JcSqlGeneratorHelper sqlGenerator=new JcSqlGeneratorHelper();
	private final Boolean intermediaryCommit;
	private Boolean statusSqlBefore=false;
	
	public JcStreamSqlExecutor(IMappingDependencyRepositoryCyclicAware repository,IBitaSqlSimpleOperations sqlExecutor,JcParameters params,Boolean applyCommits) {
		this.repo=repository;
		this.sqlExecutor=sqlExecutor;
		this.params=params;
		this.intermediaryCommit=applyCommits;	
	}
	/********************************************************************************************
	 *
	 *Private
	 *
	 ********************************************************************************************/
	private void generateAndExecuteSqlForOneMapping(IOdiMapping mapping) {
		this.sqlExecutor.executeInCurrentTransaction(sqlGenerator.generateSqlDepCleanup(params,mapping.getName()));
		this.sqlExecutor.executeInCurrentTransaction(sqlGenerator.generateSqlJobDefinition(params,mapping.getName()));
		this.sqlExecutor.executeInCurrentTransaction(sqlGenerator.generateSqlJobParameters(params,mapping.getName(),mapping.getLeadingSource()));
		this.sqlExecutor.executeInCurrentTransaction(sqlGenerator.generateSqlGroupJobs(params,mapping.getName()));
		if (this.intermediaryCommit) this.sqlExecutor.commit();
	}
	/********************************************************************************************
	 *
	 *Public
	 *
	 ********************************************************************************************/
	@Override
	public void processMapping(IOdiMapping mapping) {
		if (!this.statusSqlBefore) throw new BitaMappingAnalyzeException("The SQL statements for group definition were not executed. Please call before ampping analysis the method generateAndExecuteSqlBeforeAnalysis");
		mapping.identifySources().each{String tblName->
			repo.addTableToMapping(tblName,mapping.getName());
		}
		this.repo.addMappingToTable(mapping.getName(),mapping.identifyTarget());
		this.generateAndExecuteSqlForOneMapping(mapping);
		mapping=null;
	}
	
	
	public generateAndExecuteSqlBeforeAnalysis() {
		this.sqlExecutor.executeInCurrentTransaction(sqlGenerator.generateSqlGroupDefinition(params));
		if (this.intermediaryCommit) this.sqlExecutor.commit();
		this.statusSqlBefore=true;
	}
	
	public void generateAndExecuteSqlAfterAnalysis() {
		Set<IDependency> dpds=this.repo.getMappingDependenciesAndCheckCyclicDependencies();
		
		 for (IDependency dep:dpds) {
			MappingDependency mp=(MappingDependency) dep;
			this.sqlExecutor.executeInCurrentTransaction(this.sqlGenerator.generateSqlDependency(params,mp.who(),mp.on()));
		}
		 
		this.sqlExecutor.commit();
	}
	

}
