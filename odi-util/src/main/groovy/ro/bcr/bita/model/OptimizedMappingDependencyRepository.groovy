package ro.bcr.bita.model

import ro.bcr.bita.model.MappingDependencyRepository.MDREntry;

import java.util.HashMap;
import java.util.Set

class OptimizedMappingDependencyRepository implements
		IMappingDependencyRepositoryCyclicAware {
			
			
	
	private IBitaModelFactory bitaModelFactory=new BitaModelFactory();

	/********************************************************************************************
	 *
	 *
	 *
	 ********************************************************************************************/
	private class NamedObject{
		private final String name;
		
		public NamedObject(String name) {
			this.name=name;
		}
		
		public String getName() {
			return name;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this.is(obj))
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NamedObject other = (NamedObject) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
		
	}		
			
	private class MDREntry{
		private NamedObject table;//name of the table
		private Set<NamedObject> sourceFor=[];//mapping names
		private Set<NamedObject> targetFor=[];//mapping names
	}
	
	/********************************************************************************************
	 *
	 *
	 *
	 ********************************************************************************************/
	
	private HashMap<NamedObject,MDREntry> repo=[:];
	private TreeMap<String,NamedObject> allNamedObjects=[];
	
	
	private NamedObject getNamedObject(String key) {
		NamedObject rsp=allNamedObjects.get(key)
		if (rsp==null) rsp=new NamedObject(key);
		return rsp;
	}
	
	private MDREntry getByKey(NamedObject table) {
		MDREntry v=this.repo.get(table);
		if (v==null) {
			v=new MDREntry(table:table);
			this.repo.put(table,v);
		}
		return v;
	}
	
	private MDREntry getByKeyString(String tableName) {
		NamedObject table=this.getNamedObject(tableName);
		MDREntry v=this.repo.get(table);
		if (v==null) {
			v=new MDREntry(table:table);
			this.repo.put(table,v);
		}
		return v;
	}
			
		
	@Override
	public void addTableToMapping(String tableName, String mappingName)
			throws BitaModelException {
		MDREntry v=this.getByKeyString(tableName);
		v.sourceFor << getNamedObject(mappingName);

	}

	@Override
	public void addMappingToTable(String mappingName, String tableName)
			throws BitaModelException {
		MDREntry v=this.getByKeyString(tableName);
		v.targetFor << getNamedObject(mappingName);

	}

	@Override
	public Set<IDependency<String, String>> getMappingDependencies()
			throws BitaModelException {
			Set deps=[];
			for (NamedObject table:this.repo.keySet()) {
				MDREntry v=this.repo.get(table);
				
				for (NamedObject consumerMapping:v.sourceFor) {
					for (NamedObject producerMapping:v.targetFor) {
						deps << bitaModelFactory.newMappingDependency(consumerMapping.getName(),producerMapping.getName());
					}
				}
		
			}
			return deps;
	}

	//TODO - de implementat si detectarea de dependente ciclice
	@Override
	public Set<IDependency<String, String>> getMappingDependenciesAndCheckCyclicDependencies()
			throws BitaModelException {
		return this.getMappingDependencies();
	}

}
