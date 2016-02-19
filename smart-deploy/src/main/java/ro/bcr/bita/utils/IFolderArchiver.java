package ro.bcr.bita.utils;

import java.io.File;
import java.util.List;

public interface IFolderArchiver {
	
	public List<File> archiveFolderContent(File directory) throws ArchiveException,IllegalArgumentException; 

}
