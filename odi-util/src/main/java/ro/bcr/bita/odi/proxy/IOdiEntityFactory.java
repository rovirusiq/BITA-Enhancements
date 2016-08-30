package ro.bcr.bita.odi.proxy;

import ro.bcr.bita.odi.template.IOdiCommandContext;
import ro.bcr.bita.odi.template.OdiBasicTemplate;

import java.util.Map;
import java.util.Set;

import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.domain.mapping.finder.IMappingFinder;
import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFolderFinder;
import oracle.odi.domain.topology.finder.IOdiContextFinder;
import oracle.odi.domain.topology.finder.IOdiDataServerFinder;
import oracle.odi.domain.topology.finder.IOdiLogicalSchemaFinder;
import oracle.odi.domain.topology.finder.IOdiPhysicalSchemaFinder;

public interface IOdiEntityFactory {

	/************************************************************************************************************
	 *ODI standard Finders related
	 ************************************************************************************************************/
	public abstract IOdiScenarioFolderFinder createScenarioFolderFinder()  throws BitaOdiException;

	public abstract IOdiScenarioFinder createScenarioFinder()  throws BitaOdiException;

	public abstract IOdiProjectFinder createProjectFinder()  throws BitaOdiException;
	
	public abstract IOdiFolderFinder createProjectFolderFinder()  throws BitaOdiException;

	public abstract IMappingFinder createMappingFinder()  throws BitaOdiException;

	public abstract IOdiPhysicalSchemaFinder createPhysicalSchemaFinder() throws BitaOdiException;

	public abstract IOdiLogicalSchemaFinder createLogicalSchemaFinder() throws BitaOdiException;

	public abstract IOdiContextFinder createContextFinder() throws BitaOdiException;

	public abstract IOdiDataServerFinder createDataServerFinder() throws BitaOdiException;
	/************************************************************************************************************
	 *ODI Transaction Related
	 ************************************************************************************************************/
	public abstract ITransactionStatus createTransaction() throws BitaOdiException;
	/************************************************************************************************************
	 *Bita ODI related
	 ************************************************************************************************************/
	public abstract OdiPathUtil newOdiPathUtil();

	public abstract OdiProjectPaths newOdiProjectPaths(Map<String,Set<String>> includePaths);
	
	public abstract IOdiCommandContext newOdiTemplateCommandContext();
	
	public abstract OdiBasicTemplate newOdiTemplate();
	
	public abstract IOdiFullMappingPath newOdiMappingFullPath(String projectCode,String folderName,String mappingName);
}
