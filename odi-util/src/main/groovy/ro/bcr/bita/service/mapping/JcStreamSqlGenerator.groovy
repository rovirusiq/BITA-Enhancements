package ro.bcr.bita.service.mapping

import ro.bcr.bita.model.IBitaModelFactory;
import ro.bcr.bita.model.IDependency
import ro.bcr.bita.model.IMappingDependencyRepositoryCyclicAware;
import ro.bcr.bita.model.IOdiMapping;
import ro.bcr.bita.model.MappingDependency;
import ro.bcr.bita.model.MappingDependencyRepository
import ro.bcr.bita.service.IBitaSqlSimpleOperations
import ro.bcr.bita.service.IMappingAnalyzeProcessor;

import groovy.transform.CompileStatic;

@CompileStatic
class JcStreamSqlGenerator implements IMappingAnalyzeProcessor{
	
	private final IMappingDependencyRepositoryCyclicAware repo;
	private final IBitaSqlSimpleOperations sqlExecutor;
	private final JcParameters params;
	private final JcSqlGeneratorHelper sqlGenerator=new JcSqlGeneratorHelper();
	
	public JcStreamSqlGenerator(IMappingDependencyRepositoryCyclicAware repository,IBitaSqlSimpleOperations sqlExecutor,JcParameters params) {
		this.repo=repository;
		this.sqlExecutor=sqlExecutor;
		this.params=params;
		
		this.sqlExecutor.executeInCurrentTransaction(sqlGenerator.generateSqlGroupDefinition(params));
		
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
	}
	/********************************************************************************************
	 *
	 *Public
	 *
	 ********************************************************************************************/
	@Override
	public void processMapping(IOdiMapping mapping) {
		mapping.identifySources().each{String tblName->
			repo.addTableToMapping(tblName,mapping.getName());
		}
		this.repo.addMappingToTable(mapping.getName(),mapping.identifyTarget());
		this.generateAndExecuteSqlForOneMapping(mapping);
		mapping=null;
	}
	
	public void generateAndExecuteSqlForDependencies() {
		Set<IDependency> dpds=this.repo.getMappingDependenciesAndCheckCyclicDependencies();
		
		 for (IDependency dep:dpds) {
			MappingDependency mp=(MappingDependency) dep;
			this.sqlExecutor.executeInCurrentTransaction(this.sqlGenerator.generateSqlDependency(params,mp.who(),mp.on()));
		}
		 
		this.sqlExecutor.commit();
	}
	

}
