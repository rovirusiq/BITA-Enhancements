package ro.bcr.bita.utils

import groovy.io.FileType;

import java.io.File
import java.nio.file.Files;
import java.nio.file.Path
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern

class LocalFileSystemArchiver implements IFolderArchiver {
	
	/*
	 * Multiple patterns. if one of them is true then it will be archived
	 */
	private static final List<Pattern> FILENAMES_TO_BE_ARCHIVED=[~/.*/];
	
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
	
	
	protected Boolean matchFilename(String filename) {
		
		
		return FILENAMES_TO_BE_ARCHIVED.inject(false){ Boolean acc, Pattern p->
			if (acc) {
				acc;
			} else {
				acc=acc | (p.matcher(filename).asBoolean())
			}
		}		
	}
	
	
	protected List<File> identifyFilesToBeMoved(File folderToBeArchived){
		List<File> filesToBeMoved=[];
		
		
		folderToBeArchived.eachFile(FileType.FILES) { File f->
			if (matchFilename(f.getName())) {
				filesToBeMoved.add(f.getAbsoluteFile());
			}
		}
		
		return filesToBeMoved;
	}
	
	
	@Override
	public List<File> archiveFolderContent(File targetFolder) throws ArchiveException, IllegalArgumentException {
		
		File workFolder;
		File archiveSubFolder;
		
		workFolder=targetFolder.getAbsoluteFile();
		archiveSubFolder=new File(targetFolder.getAbsolutePath()+File.separator+this.archiveSubFolderName);
		
		if (!workFolder.isDirectory()) {
			throw new IllegalArgumentException("The target folder does not exists or is not a directory");
		}
		
		List<File> filesToBeMoved=identifyFilesToBeMoved(workFolder);
		
		
		if (filesToBeMoved.size()==0) return;
		
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
		
		filesToBeMoved.each {File s->
			
			File d=new File(newArchiveFolder.getAbsolutePath()+File.separator+s.getName());
			
			try {
				Files.move(s.toPath(),d.toPath(),StandardCopyOption.ATOMIC_MOVE);
			} catch (Exception ex) {
				thereWereErrors=true;
			}
		}
		
		if (thereWereErrors) throw new ArchiveException("Not all files were archived");
		
		return filesToBeMoved;
	}

}
