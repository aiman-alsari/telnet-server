package nz.co.ahc.command;

import java.io.File;
import java.io.IOException;

public class ChangeDirectoryCommand implements ShellCommand {

	public File handle(String arguments, FileManagerPrintStream printer, File workingDirectory) {
		String firstArgument = arguments.trim().split(" ")[0];
		File f;
		
		//Deal with absolute paths
		if(firstArgument.startsWith(File.separator)) {
			f = new File(firstArgument);
		} else {
			f = new File(workingDirectory + File.separator + firstArgument);
		}
		
		//Validate that it is a directory
		if(f.exists() && f.isDirectory()) {
			try {
				return f.getCanonicalFile();
			} catch (IOException e) {
				printer.println("Error changing to directory: " + firstArgument);
			}
		} else {
			printer.println("Directory does not exist: " + firstArgument);
		}
		return workingDirectory;
	}

}
