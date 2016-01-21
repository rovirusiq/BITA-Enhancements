package ro.bcr.bita.smartdeploy;

import java.io.File;

public interface IFolderArchiver {
	
	public void archiveFolderContent(File directory) throws ArchiveException,IllegalArgumentException; 

}
