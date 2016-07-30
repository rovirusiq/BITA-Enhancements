package ro.bcr.bita.service.mapping

import ro.bcr.bita.model.IBitaModelFactory;
import ro.bcr.bita.model.IDependency
import ro.bcr.bita.model.IMappingDependencyRepositoryCyclicAware;
import ro.bcr.bita.model.IOdiMapping;
import ro.bcr.bita.model.MappingDependency;
import ro.bcr.bita.model.MappingDependencyRepository
import ro.bcr.bita.service.IMappingAnalyzeProcessor;

import groovy.transform.CompileStatic;

@CompileStatic
class JcAnalyzeProcessor implements IMappingAnalyzeProcessor{
	
	private final IMappingDependencyRepositoryCyclicAware repo;
	private final Set<IOdiMapping> allMappings=[];
	
	public JcAnalyzeProcessor(IMappingDependencyRepositoryCyclicAware repository) {
		this.repo=repository;
	}

	@Override
	public void processMapping(IOdiMapping mapping) {
		mapping.identifySources().each{String tblName->
			repo.addTableToMapping(tblName,mapping.getName());
		}
		this.repo.addMappingToTable(mapping.getName(),mapping.identifyTarget());
		this.allMappings << mapping;
	}
	
	public Set<MappingDependency> getDependencies(){
		return repo.getMappingDependenciesAndCheckCyclicDependencies();
	}
	
	public Set<String> getAllMappingNames(){
		Set<String> stuff=[];
		stuff.addAll(this.allMappings.collect{IOdiMapping aM->aM.getName()});
		return stuff;
	}
	
	public Set<IOdiMapping> getAllMappings(){
		Set<IOdiMapping> stuff=[];
		stuff.addAll(this.allMappings);
		return stuff;
	}

}
