package ro.bcr.bita.odi.proxy

class OdiFullMappingPath implements IOdiFullMappingPath {
	
	private final String projectCode;
	private final String folderName;
	private final String mappingName;

		

	/**
	 * @param projectCode
	 * @param folderName
	 * @param mappingName
	 */
	protected OdiFullMappingPath(String projectCode, String folderName,
			String mappingName) {
		super();
		this.projectCode = projectCode;
		this.folderName = folderName;
		this.mappingName = mappingName;
	}

	@Override
	public String getProjectCode() {
		return this.projectCode;
	}

	@Override
	public String getFolderName() {
		return this.folderName;
	}

	@Override
	public String getMappingName() {
		return this.mappingName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((folderName == null) ? 0 : folderName.hashCode());
		result = prime * result
				+ ((mappingName == null) ? 0 : mappingName.hashCode());
		result = prime * result
				+ ((projectCode == null) ? 0 : projectCode.hashCode());
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
		OdiFullMappingPath other = (OdiFullMappingPath) obj;
		if (folderName == null) {
			if (other.folderName != null)
				return false;
		} else if (!folderName.equals(other.folderName))
			return false;
		if (mappingName == null) {
			if (other.mappingName != null)
				return false;
		} else if (!mappingName.equals(other.mappingName))
			return false;
		if (projectCode == null) {
			if (other.projectCode != null)
				return false;
		} else if (!projectCode.equals(other.projectCode))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OdiFullMappingPath [${this.projectCode}-${this.folderName}-${this.mappingName}]";
	}
	
	

}
