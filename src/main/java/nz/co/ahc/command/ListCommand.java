package nz.co.ahc.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListCommand implements ShellCommand {

	public File handle(String arguments, FileManagerPrintStream printer, File workingDirectory) {
		String prefix = "";
		if(!arguments.isEmpty()) {
			prefix = arguments.split(" ")[0];
		}
		final String locationToCheck;
		if(prefix.startsWith(File.separator)) {
			locationToCheck = prefix;
		} else {
			locationToCheck = workingDirectory.getAbsolutePath() + File.separator + prefix;
		}
		
		File fileToCheck = new File(locationToCheck);
		if(!fileToCheck.exists() || !fileToCheck.isDirectory()) {
			printer.println("Directory does not exist: " + arguments);
			return workingDirectory;
		}
		
		final List<String> list = Arrays.asList(fileToCheck.list());
		Collections.sort(list);
		
		//The list is ordered alphabetically, but is mixed up with directories and files, let's print out all the dirs first, 
		//and keep the ordinary files on the side
		List<String> orderedFiles = new ArrayList<String>();
		for(String file : list) {
			if(new File(fileToCheck.getAbsolutePath() + File.separator + file).isDirectory()) {
				printer.println("<D> " + file);
			} else {
				orderedFiles.add(file);
			}
		}
		
		//Now lets print out the ordinary files
		for(String file : orderedFiles) {
			printer.println("    " + file);
		}
		
		return workingDirectory;
	}

}
