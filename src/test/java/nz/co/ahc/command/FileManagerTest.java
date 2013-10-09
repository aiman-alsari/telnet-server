package nz.co.ahc.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileManagerTest {

	private static final String PREFIX = "FileManagerTest";
	private File tempDir;
	private FileManagerPrintStream mockPrinter;
	private FileManager fm;
	
	@Before
	public void setup() throws IOException {
		//Create a temporary directory for all our file manipulation shenanigans.
		File tempFile = File.createTempFile(PREFIX, "");
		tempFile.deleteOnExit();
		tempDir = new File(tempFile.getParentFile().getAbsolutePath() + File.separator + PREFIX);
		tempDir.mkdir();
		
		mockPrinter = mock(FileManagerPrintStream.class);
		fm = new FileManager(mockPrinter, tempDir);
	}
	
	@After
	public void deleteTemporaryFiles() throws IOException {
		FileUtils.deleteDirectory(tempDir);
	}
	
	@Test
	public void testPwd() {
		fm.processCommand("pwd");
		verify(mockPrinter).println(tempDir.getAbsolutePath());
	}

	@Test
	public void testEmptyCommand() {
		fm.processCommand("");
		verifyZeroInteractions(mockPrinter);
	}

	@Test
	public void testMakeDirCommandNoArguments() {
		fm.processCommand("mkdir");
		verify(mockPrinter).println("Usage: mkdir [directory name]");
	}
	
	@Test
	public void testMakeDirCommand() {
		fm.processCommand("mkdir cheese");
		verifyZeroInteractions(mockPrinter);
	}
	
	@Test
	public void testListCommand() {
		fm.processCommand("mkdir cheese");
		fm.processCommand("mkdir brie");
		fm.processCommand("ls");
		
		verify(mockPrinter).println("<D> brie");
		verify(mockPrinter).println("<D> cheese");
	}
	
	@Test
	public void testChangeDirectoryCommand() {
		fm.processCommand("mkdir cheese");
		fm.processCommand("cd cheese");
		fm.processCommand("mkdir brie");
		fm.processCommand("ls");
		
		verify(mockPrinter).println("<D> brie");
		verify(mockPrinter, times(0)).println("<D> cheese");
	}
	
	@Test
	//In dos, this does a pwd, in bash this takes you to your home dir. In this shell, we just ignore and carry on.
	public void testChangeDirectoryCommandNoArguments() {
		fm.processCommand("mkdir cheese");
		fm.processCommand("cd cheese");
		fm.processCommand("mkdir brie");
		fm.processCommand("cd");
		fm.processCommand("ls");
		
		verify(mockPrinter).println("<D> brie");
		verify(mockPrinter, times(0)).println("<D> cheese");
	}
	
	@Test
	//In dos, this errors, in bash this takes the first argument. In this shell, we do what bash does.
	public void testChangeDirectoryCommandTwoArguments() {
		fm.processCommand("mkdir cheese");
		fm.processCommand("cd cheese smellz");
		fm.processCommand("mkdir brie");
		fm.processCommand("ls");
		
		verify(mockPrinter).println("<D> brie");
		verifyNoMoreInteractions(mockPrinter);
	}
	
	@Test
	public void testChangeDirectoryUpADirectory() {
		fm.processCommand("mkdir cheese");
		fm.processCommand("cd cheese");
		fm.processCommand("cd ..");
		fm.processCommand("mkdir brie");
		fm.processCommand("ls");
		fm.processCommand("pwd");
		
		verify(mockPrinter).println("<D> brie");
		verify(mockPrinter).println("<D> cheese");
		verify(mockPrinter).println(tempDir.getAbsolutePath());
	}
	
	@Test
	public void testMakeAbsoluteDirectory() {
		fm.processCommand("mkdir "+ tempDir.getAbsolutePath() + File.separator + "cheese");
		fm.processCommand("ls");
		fm.processCommand("pwd");
		
		verify(mockPrinter).println("<D> cheese");
		verify(mockPrinter).println(tempDir.getAbsolutePath());
		verifyNoMoreInteractions(mockPrinter);
	}
	
	@Test
	public void testMakeTwoDirectories() {
		fm.processCommand("mkdir "+ tempDir.getAbsolutePath() + File.separator + "cheese brie");
		fm.processCommand("ls");
		
		verify(mockPrinter).println("<D> cheese");
		verify(mockPrinter).println("<D> brie");
		verifyNoMoreInteractions(mockPrinter);
	}
	
	@Test
	public void testChangeDirectoryAbsolute() {
		fm.processCommand("mkdir cheese");
		fm.processCommand("cd " + tempDir.getAbsolutePath() + File.separator + "cheese");
		fm.processCommand("pwd");
		
		verify(mockPrinter).println(tempDir.getAbsolutePath()+ File.separator + "cheese");
		verifyNoMoreInteractions(mockPrinter);
	}

	@Test
	public void testListRelative() {
		fm.processCommand("mkdir cheese cheese/brie");
		fm.processCommand("ls cheese");
		
		verify(mockPrinter).println("<D> brie");
		verifyNoMoreInteractions(mockPrinter);
	}
	
	@Test
	public void testListAbsolute() {
		fm.processCommand("mkdir cheese cheese/brie");
		fm.processCommand("ls " + tempDir.getAbsolutePath()+ File.separator + "cheese");
		
		verify(mockPrinter).println("<D> brie");
		verifyNoMoreInteractions(mockPrinter);
	}
	
	@Test
	public void testListIncorrectName() {
		fm.processCommand("mkdir cheese cheese/brie");
		fm.processCommand("ls " + tempDir.getAbsolutePath()+ File.separator + "fruit");
		
		verify(mockPrinter).println("Directory does not exist: " + tempDir.getAbsolutePath()+ File.separator + "fruit");
		verifyNoMoreInteractions(mockPrinter);
	}
	
	@Test
	public void testListFilesAndDirectories() {
		fm.processCommand("mkdir cheese brie edam");
		fm.processCommand("touch fish");
		fm.processCommand("ls ");
		
		verify(mockPrinter).println("<D> brie");
		verify(mockPrinter).println("<D> cheese");
		verify(mockPrinter).println("<D> edam");
		verify(mockPrinter).println("    fish");
		verifyNoMoreInteractions(mockPrinter);
	}
	
	@Test
	public void testChangeDirectoryOnlyLetsYouChangeToDirectories() {
		fm.processCommand("touch fish");
		fm.processCommand("cd fish");
		
		verify(mockPrinter).println("Directory does not exist: fish");
		verifyNoMoreInteractions(mockPrinter);
	}
	
	@Test
	public void testCanNotListFile() {
		fm.processCommand("touch fish");
		fm.processCommand("ls fish");
		
		verify(mockPrinter).println("Directory does not exist: fish");
		verifyNoMoreInteractions(mockPrinter);
	}
	
	@Test
	public void testUnkownCommand() {
		fm.processCommand("fiddle");
		verify(mockPrinter).println("Unknown command: fiddle");
	}
	
}
