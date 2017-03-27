package ro.bcr.bita.mapping.analyze

import ro.bcr.bita.mapping.analyze.IMappingAnalyzeProcessor
import ro.bcr.bita.model.IMappingDependencyRepositoryCyclicAware
import ro.bcr.bita.model.IMappingRepository
import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.model.MappingDependency
import ro.bcr.bita.odi.proxy.BitaOdiException

import groovy.transform.CompileStatic

@CompileStatic
public class MappingDependencyAnalyzerProcessor implements IMappingAnalyzeProcessor{
	
	private final IMappingRepository repo;
	private final Set<IOdiMapping> allMappings=[];
	private final Boolean retainOdiMapping; 
	
	public MappingDependencyAnalyzerProcessor(IMappingRepository repository,Boolean retainOdiMapping=true) {
		this.repo=repository;
		this.retainOdiMapping=retainOdiMapping;
	}

	@Override
	public void processMapping(IOdiMapping mapping) {
		try {
			mapping.identifySources().each{String tblName->
				repo.addTableToMapping(tblName,mapping.getName());
			}
			mapping.identifyTargets().each{String tblName->
				repo.addMappingToTable(mapping.getName(),tblName);
			}
			if (this.retainOdiMapping) {
				this.allMappings << mapping;
			}
		} catch (Exception ex) {
			throw new BitaOdiException("An unexpected exception was encountered when processing the mapping: ${mapping}", ex);
		}
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
	
	public IMappingRepository getMappingRepository() {
		return this.repo;
	}

}
