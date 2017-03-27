package ro.bcr.bita.model;

import ro.bcr.bita.app.IBitaJdbcConnectionProvider;
import ro.bcr.bita.mapping.analyze.IMappingAnalyzerService;
import ro.bcr.bita.mapping.analyze.JcRequestContext;
import ro.bcr.bita.mapping.analyze.JcSqlCommandsGenerator;
import ro.bcr.bita.mapping.analyze.JcSqlExecutor;
import ro.bcr.bita.mapping.analyze.MappingDependencyAnalyzerProcessor;
import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.proxy.IOdiOperationsService;
import ro.bcr.bita.odi.template.IOdiCommandContext;
import ro.bcr.bita.odi.template.OdiBasicTemplate;
import ro.bcr.bita.sql.IBitaSqlExecutor;
import ro.bcr.bita.sql.IBitaSqlStatementExecutor;

import oracle.odi.domain.mapping.Mapping;

public interface IBitaDomainFactory extends IBitaModelFactory,IOdiEntityFactory{
	
	
	public abstract IOdiMapping newOdiMapping(Mapping odiObject) throws BitaModelException;
	public abstract IOdiScenario newOdiScenario(oracle.odi.domain.runtime.scenario.OdiScenario  odiObject);
	public abstract IOdiOperationsService newOdiOperationsService();
	public abstract IOdiCommandContext newOdiTemplateCommandContext();
	public abstract OdiBasicTemplate newOdiTemplate();
	
	public abstract IMappingAnalyzerService newMappingAnalyzerService();
	public abstract MappingDependencyAnalyzerProcessor newMappingDependencyAnalyzerProcessor();
	public abstract IMappingRepository createMappingDependencyRepository();
	public abstract JcSqlCommandsGenerator newJcSqlCommandGenerator();
	public abstract IBitaSqlStatementExecutor newBitaSqlStatementExecutor(IBitaJdbcConnectionProvider jdbcProvider);
	public abstract JcSqlExecutor newJcSqlExecutor(JcRequestContext params,IBitaSqlExecutor bitaSqlExecutor);

}
