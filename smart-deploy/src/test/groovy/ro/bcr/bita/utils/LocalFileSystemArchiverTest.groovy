package ro.bcr.bita.utils
import static groovy.test.GroovyAssert.*;

import ro.bcr.bita.utils.LocalFileSystemArchiver;

import groovy.io.FileType;

import java.nio.file.Path
import java.nio.file.Paths;

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
	
	
	private List<File> createExpectedList(String prefix,Integer x){
		List<Path> expected=[];
		
		x.times{v->
			expected.add(new File(targetFolder.getAbsolutePath()+File.separator+(prefix+String.format(FORMAT_NUMBER,(v+1)))));
		}
		return expected
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
	public void filenamesShouldBeMatched(){
		
		assert true==subject.matchFilename("whatever");
		assert true==subject.matchFilename("whatever.something");
	}
	
	
	@Test
	public void filenamesToBeArchivedShouldBeIdentifed_1(){
		
		List<File> list=subject.identifyFilesToBeMoved(targetFolder);
		
		assert 0==list.size();
	}
	
	
	
	@Test
	public void filenamesToBeArchivedShouldBeIdentifed_2(){
		
		Integer x=3;
		String prefix="cici-";
		
		this.createNFilesWithPrefix(prefix,x);
		
		List<File> expected=this.createExpectedList(prefix,x);
		
		List<File> list=subject.identifyFilesToBeMoved(targetFolder);
		
		assert expected==list;
	}
	
	@Test
	public void folderToBeArchivedIsEmptyShouldResultInNoOperation() {

		subject.archiveFolderContent(targetFolder);
		assert false==archiveFolder.exists();
	}
	
	@Test
	public void whenParameterThatIsNotAFolderShoudThrowIllegalArgumentException() {
		shouldFail IllegalArgumentException,{
			subject.archiveFolderContent(dummyFile);
		}
	}
	
	@Test
	public void whenArchiveFolderDoesNotExistsThenItMustBeCreated() {
		
		String prefix="cici-";
		Integer x=1;
		//place some content in the folder
		this.createNFilesWithPrefix(prefix,x);
		List<File> list=subject.archiveFolderContent(targetFolder);	
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
		
		//place some content if the target folder
		this.createNFilesWithPrefix("grrr-",1);
		
		this.createNFoldersWithPrefix(archiveFolderPrefix,x);
		
		shouldFail ArchiveException,{			
			subject.archiveFolderContent(targetFolder);
		}
		
	}
	
	
	@Test
	public void whenFilesAreArchivedTheyShouldBeMovedIntheArchiveFolder_2() {
		
		Integer x=7;
		String prefix="cici-";
		
		//place content in the folder
		this.createNFilesWithPrefix(prefix,x);
		List<File> expected=this.createExpectedList(prefix,x);
		
		List<File> filesMoved=subject.archiveFolderContent(targetFolder);
		
		Integer countToArchive=x;
		Integer countArchived=archiveFolder.listFiles()[0].list().length;
		
		assert countToArchive==countArchived;
		//check the list of files that have been moved
		assert expected==filesMoved;
		//the files have been moved. they are no longer present in the target folder
		assert []==subject.identifyFilesToBeMoved(targetFolder);
	}

}
