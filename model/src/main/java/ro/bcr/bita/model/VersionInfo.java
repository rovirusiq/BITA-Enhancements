package ro.bcr.bita.model;

public class VersionInfo {
	
	private String release;
	private String version;
	private String loadUserName;
	/**
	 * @return the release
	 */
	public String getRelease() {
		return release;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @return the loadUser
	 */
	public String getLoadUserName() {
		return loadUserName;
	}
	/**
	 * @param release the release to set
	 */
	public void setRelease(String release) {
		this.release = release;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @param loadUser the loadUser to set
	 */
	public void setLoadUserName(String loadUser) {
		this.loadUserName = loadUser;
	}
	
	

}
