package ro.bcr.bita.smartdeploy
import static groovy.test.GroovyAssert.*;
import groovy.io.FileType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

class LocalFileSystemArchiverTest {
	
	private LocalFileSystemArchiver subject;
	private String pathToTestDirectory="src/test/resources/archiver-test";
	private String pathToDummyFile="src/test/resources/archiver-file-test.txt";
	private String archiveFolderName="archive";
	private String archiveFolderPrefix=(new Date()).format('yyyyMMdd')+"-";
	private String FORMAT_NUMBER='%03d';
	
	private File archiveFolder;
	private File targetFolder;
	private dummyFile;
	
	
	
	private void createNObjectsWithPrefix (FileType requiredType,File locationFolder,String prefix, Integer n) {
		
		if (![FileType.DIRECTORIES,FileType.FILES].contains(requiredType)) {
			throw new IllegalArgumentException("Be specific! File or Directories? FileType.ANY is not an allowed value");
		}
		
		if (n>=1000) {
			throw new IllegalArgumentException("Too manu files/folders, you gready bastard!");
		}
		
		
		if (!locationFolder.exists()) locationFolder.mkdirs();
		
		n.times {nr->
			File newFolder=new File(locationFolder.getAbsolutePath()+File.separator+prefix+String.format('%03d',(nr+1)));
			
			if (FileType.DIRECTORIES==requiredType){ 
				newFolder.mkdir();
			} else {
				newFolder.createNewFile();
			}
		}
	}
	
	private void createNFoldersWithPrefix(String prefix, Integer n) {
		this.createNObjectsWithPrefix(FileType.DIRECTORIES,archiveFolder,prefix,n);
	}
	
	private void createNFilesWithPrefix(String prefix, Integer n) {
		this.createNObjectsWithPrefix(FileType.FILES,targetFolder,prefix,n);
	}
	
	
	
	@Before
	public void beforeTest() {
		subject=new LocalFileSystemArchiver();
		targetFolder=new File(pathToTestDirectory);
		archiveFolder=new File(pathToTestDirectory+File.separator+archiveFolderName);
		dummyFile=new File(pathToDummyFile);
		
		//out the folders everytime in the same state
		if (archiveFolder.exists()) {
			archiveFolder.deleteDir();
		}
		if (targetFolder.exists()) {
			targetFolder.deleteDir();
		}
		
		targetFolder.mkdir();
		
	}
	
	@Test
	public void whenParameterThatIsNotAFolderShoudThrowIllegalArgumentException() {
		shouldFail IllegalArgumentException,{
			subject.archiveFolderContent(dummyFile);
		}
	}
	
	@Test
	public void whenArchiveFolderDoesNotExistsThenItMustBeCreated() {
		subject.archiveFolderContent(targetFolder);	
		assert archiveFolder.exists();
	}
	
	@Test
	public void whenFolderNeedsToBeCreatedTheNameIsIdentifiedCorrectly_1() {
		
		Integer x=3;
		
		this.createNFoldersWithPrefix(archiveFolderPrefix,x);
		this.createNFoldersWithPrefix("altPrefix",5);
		
		String rsp=subject.createNewName(targetFolder,archiveFolder)
		
		assert 	archiveFolderPrefix+String.format(FORMAT_NUMBER,++x)==rsp;
		
	}
	
	@Test
	public void whenFolderNeedsToBeCreatedTheNameIsIdentifiedCorrectly_2() {
		this.createNFoldersWithPrefix("altPrefix",5);
		
		String rsp=subject.createNewName(targetFolder,archiveFolder)
		
		assert 	archiveFolderPrefix+String.format(FORMAT_NUMBER,1)==rsp;
		
	}

	@Test
	public void whenFolderNeedsToBeCreatedTheNameIsIdentifiedCorrectly_3() {
		
		Integer x=999;
		
		this.createNFoldersWithPrefix(archiveFolderPrefix,x);
		shouldFail ArchiveException,{			
			subject.archiveFolderContent(targetFolder);
		}
		
	}
	
	@Test
	public void whenFilesAreArchivedTheyShouldBeMovedIntheArchiveFolder_1() {
		
		
		subject.archiveFolderContent(targetFolder);
		
		Integer countToArchive=0;
		Integer countArchived=archiveFolder.listFiles()[0].list().length;
		
		assert countToArchive==countArchived;
	}
	
	
	@Test
	public void whenFilesAreArchivedTheyShouldBeMovedIntheArchiveFolder_2() {
		
		Integer x=7;
		
		this.createNFilesWithPrefix("cici-",x);
		
		subject.archiveFolderContent(targetFolder);
		
		Integer countToArchive=x;
		Integer countArchived=archiveFolder.listFiles()[0].list().length;
		
		assert countToArchive==countArchived;
	}

}
