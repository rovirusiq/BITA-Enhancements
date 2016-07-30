package ro.bcr.bita.odi.proxy

import ro.bcr.bita.odi.proxy.IOdiProjectPaths;

import groovy.transform.CompileStatic;

import java.util.Set;

@CompileStatic
class OdiProjectPaths implements IOdiProjectPaths{
	
	
	private final Map<String,Set<String>> internalStruct;
	
	public OdiProjectPaths( Map<String,Set<String>>internalStruct) {
		this.internalStruct=internalStruct;
	}
	
	
	@Override
	public Set<String> getProjects() {
		return this.internalStruct.keySet();
	}

	@Override
	public Set<String> getFoldersForProject(String projectCode) {
		Set rsp=this.internalStruct.get(projectCode);
		return (rsp==null)? [] as Set:rsp;
	}

}
