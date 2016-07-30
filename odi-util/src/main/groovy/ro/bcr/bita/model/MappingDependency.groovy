package ro.bcr.bita.model

import groovy.transform.TypeChecked;

@TypeChecked
class MappingDependency implements IDependency<String, String>,ICyclicDependencyAware{
	
	private String dependentMapping;
	private String dependencyMapping;
	private Integer internalHashCode=null;
	private Integer reversalHashCode=null;
	private String cyclicIdentifier=null;
	
	

	/**
	 * @param dependentMapping
	 * @param dependencyMapping
	 */
	protected MappingDependency(String dependentMapping, String dependencyMapping) {
		super();
		this.dependentMapping = dependentMapping;
		this.dependencyMapping = dependencyMapping;
	}
	
	
	/********************************************************************************************
	 *
	 * Private
	 *
	 ********************************************************************************************/
	
	private void calculateHashCodes() {
		if (this.internalHashCode==null) {
			final int prime = 31;
			int result1 = 1;
			int result2 = 1;
			
			int d1=((dependencyMapping == null) ? 0 : dependencyMapping.hashCode());
			int d2=((dependentMapping == null) ? 0 : dependentMapping.hashCode());
			result1 = prime* result1+d1;
			result1 = prime* result1+d2;
			
			result2 = prime* result2+d2;
			result2 = prime* result2+d1;
			
			this.internalHashCode=result1;
			this.reversalHashCode=result2;
			
			String partial=(result1<result2)? result1+":"+result2:result2+":"+result1;
			
			int actorHashCode=DependencyActor.MAPPING.value().hashCode();
			
			this.cyclicIdentifier=actorHashCode+":"+actorHashCode+":"+partial;
		}
	}

	@Override
	public String who() {
		return dependentMapping;
	}

	@Override
	public String on() {
		return dependencyMapping;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		this.calculateHashCodes();
		return this.internalHashCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this.is(obj)) return false;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MappingDependency other = (MappingDependency) obj;
		if (dependencyMapping == null) {
			if (other.dependencyMapping != null)
				return false;
		} else if (!dependencyMapping.equals(other.dependencyMapping))
			return false;
		if (dependentMapping == null) {
			if (other.dependentMapping != null)
				return false;
		} else if (!dependentMapping.equals(other.dependentMapping))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MappingDependency [who:" + dependentMapping+ ", on:" + dependencyMapping + "]";
	}

	@Override
	public String cyclicIdentifier() {
		this.calculateHashCodes();
		return this.cyclicIdentifier;
	}
	
	
	
	

}
