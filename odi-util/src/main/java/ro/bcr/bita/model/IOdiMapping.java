package ro.bcr.bita.model;

import java.util.List;

public interface IOdiMapping {
	
	public Number getInternalId() throws BitaModelException;
	public String getGlobalId() throws BitaModelException;
	public String getName() throws BitaModelException;
	public List<String> identifySources() throws BitaModelException;
	public String identifyTarget() throws BitaModelException;
	public String getLeadingSource() throws BitaModelException;
	public String getFullPathName() throws BitaModelException;

}
