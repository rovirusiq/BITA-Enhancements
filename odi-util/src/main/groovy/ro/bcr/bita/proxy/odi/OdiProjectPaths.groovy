package ro.bcr.bita.proxy.odi

import java.util.Set;

//TODO se poate optimiza
class OdiProjectPaths implements IOdiProjectPaths{
	
	
	private final Map<String,Set<String>> internalStruct;
	
	protected OdiProjectPaths( Map<String,Set<String>>internalStruct) {
		this.internalStruct=internalStruct;
	}
	
	
	@Override
	public Set<String> getProjects() {
		return this.internalStruct.keySet();
	}

	@Override
	public Set<String> getFoldersForProject(String projectCode) {
		Set rsp=this.internalStruct.get(projectCode);
		return (rsp==null)? []:rsp;
	}

}
