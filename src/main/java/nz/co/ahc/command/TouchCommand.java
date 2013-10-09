package nz.co.ahc.command;

import java.io.File;
import java.io.IOException;

public class TouchCommand implements ShellCommand {

	public File handle(String arguments, FileManagerPrintStream printer, File workingDirectory) {
		if(arguments == null || arguments.length() == 0) {
			printer.println("Usage: touch [file name]");
		}
		String[] names = arguments.split(" ");
		for(String name : names) {
			File f;
			if(name.startsWith(File.separator)) {
				f = new File(name);
			} else {
				f = new File(workingDirectory.getAbsolutePath() + File.separator + name);
			}
			try {
				f.createNewFile();
			} catch (IOException e) {
				printer.println("Error touching file " + name);
			}
		}
		return workingDirectory;
	}

}
