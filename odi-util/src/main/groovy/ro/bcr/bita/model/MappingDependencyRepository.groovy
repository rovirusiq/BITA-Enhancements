package ro.bcr.bita.model


//TODO more detailed exception about Cyclic dependencies detected
class MappingDependencyRepository implements IMappingDependencyRepositoryCyclicAware<String,String> {
	
	
	private IBitaModelFactory bitaModelFactory=new BitaModelFactory();
	
	private class MDREntry{
		private String name;//name of the table
		private Set<String> sourceFor=[];//mapping names
		private Set<String> targetFor=[];//mapping names 
	}
	
	
	private HashMap<String,MDREntry> repo=[:];
	
	
	private MDREntry getByKey(String tableName) {
		MDREntry v=this.repo.get(tableName);
		if (v==null) {
			v=new MDREntry(name:tableName);
			this.repo.put(tableName,v);
		}
		return v;
	}
	
	
	@Override
	public void addTableToMapping(String tableName, String mappingName) throws BitaModelException {
		MDREntry v=this.getByKey(tableName);
		v.sourceFor << mappingName;

	}

	@Override
	public void addMappingToTable(String mappingName, String tableName) throws BitaModelException{
		MDREntry v=this.getByKey(tableName);
		v.targetFor << mappingName;
	}

	@Override
	public Set<IDependency<String, String>> getMappingDependencies() throws BitaModelException{
		Set deps=[];
		for (String tableName:this.repo.keySet()) {
			MDREntry v=this.repo.get(tableName);
			
			for (String consumerMapping:v.sourceFor) {
				for (String producerMapping:v.targetFor) {
					deps << bitaModelFactory.newMappingDependency(consumerMapping,producerMapping);
				}
			}

		}
		return deps;
	}


	@Override
	public Set<IDependency<String, String>> getMappingDependenciesAndCheckCyclicDependencies() throws BitaModelException{
		Set deps=this.getMappingDependencies();
		Map lstCyclic=[:];
		List<IDependency<String, String>> faulty=[];
		deps.each{ICyclicDependencyAware it->
			String key=it.cyclicIdentifier();
			if (lstCyclic.get(key)==null) {
				lstCyclic.put(key,it);
			} else {
				faulty << it;
			}
			
		};
		if (faulty.size()>0) throw new BitaCyclicDependencyException("Cyclic Mapping Dependency detected",faulty);
		return deps; 
	}

}
