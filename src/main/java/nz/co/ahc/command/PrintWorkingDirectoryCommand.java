package nz.co.ahc.command;

import java.io.File;

public class PrintWorkingDirectoryCommand implements ShellCommand {

	public File handle(String arguments, FileManagerPrintStream printer, File workingDirectory) {
		printer.println(workingDirectory.getAbsolutePath());
		return workingDirectory;
	}

}
