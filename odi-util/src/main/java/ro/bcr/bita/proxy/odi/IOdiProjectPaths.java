package ro.bcr.bita.proxy.odi;

import java.util.Set;

public interface IOdiProjectPaths {
	
	public Set<String> getProjects();
	public Set<String> getFoldersForProject(String projectCode);
}
