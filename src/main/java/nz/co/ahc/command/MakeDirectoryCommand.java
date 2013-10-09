package nz.co.ahc.command;

import java.io.File;

public class MakeDirectoryCommand implements ShellCommand {

	public File handle(String arguments, FileManagerPrintStream printer, File workingDirectory) {
		if(arguments == null || arguments.length() == 0) {
			printer.println("Usage: mkdir [directory name]");
		}
		String[] names = arguments.split(" ");
		for(String name : names) {
			File f;
			if(name.startsWith(File.separator)) {
				f = new File(name);
			} else {
				f = new File(workingDirectory.getAbsolutePath() + File.separator + name);
			}
			f.mkdir();
		}
		return workingDirectory;
	}

}
