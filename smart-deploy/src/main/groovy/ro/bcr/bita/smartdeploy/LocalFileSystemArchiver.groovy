package ro.bcr.bita.smartdeploy

import groovy.io.FileType;

import java.io.File
import java.nio.file.Files;
import java.nio.file.Path
import java.nio.file.Paths;
import java.util.regex.Pattern

class LocalFileSystemArchiver implements IFolderArchiver {
	
	
	private final String archiveSubFolderName;
	private final String FORMAT_NUMBER='%03d';
	
	
	public LocalFileSystemArchiver(String archiveFolderName="archive") {
		archiveSubFolderName=archiveFolderName;
		
	}


	protected String createNewName(File targetFolder,File archiveSubFolder) throws ArchiveException{
		
		String prefix=(new Date()).format('yyyyMMdd')+"-";
		
		Pattern p= ~"^$prefix\\d*";
		
		Integer max=0;
		
		archiveSubFolder.eachDirMatch(p){File a->
			Integer nr=a.getName()[-3..-1] as Integer;
			max= (max>=nr)? max:nr;
		}
		
		++max;
		
		if (max>=1000) throw new ArchiveException("The number of subfolders for today in archives is over the alowed limit[999]");
		
		return prefix+String.format(FORMAT_NUMBER,max);
	}
	
	
	@Override
	public void archiveFolderContent(File targetFolder) throws ArchiveException, IllegalArgumentException {
		
		File workFolder;
		File archiveSubFolder;
		
		workFolder=targetFolder.getAbsoluteFile();
		archiveSubFolder=new File(targetFolder.getAbsolutePath()+File.separator+this.archiveSubFolderName);
		
		
		if (!workFolder.isDirectory()) {
			throw new IllegalArgumentException("The target folder does not exists or is not a directory");
		}
		
		if (!archiveSubFolder.exists()) {
			if (!archiveSubFolder.mkdir()) throw new ArchiveException("""
						The folder[${archiveSubFolder.getAbsolutePath()}] cannot be created.
						""");
		}
		
		String rsp=this.createNewName(workFolder,archiveSubFolder);
		File newArchiveFolder=new File(archiveSubFolder.getAbsolutePath()+File.separator+rsp);
		
	
		if (!newArchiveFolder.mkdir()) throw new ArchiveException("""
						The folder[${newArchiveFolder.getAbsolutePath()}] cannot be created.
						""");
		
		Boolean thereWereErrors=false;
		workFolder.eachFile(FileType.FILES) { File f->
			Path s=Paths.get(f.getAbsolutePath());
			Path d=Paths.get(newArchiveFolder.getAbsolutePath(),f.getName());
			try {
				Files.copy(s,d);
			} catch (Exception ex) {
				thereWereErrors=true;
			}
		}
		
		if (thereWereErrors) throw new ArchiveException("Not all files were copied");
	}

}
