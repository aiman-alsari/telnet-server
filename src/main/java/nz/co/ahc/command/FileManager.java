package nz.co.ahc.command;

import java.io.File;

/**
 * This class determines the appropriate implementation of ShellCommand to handle this request and delegates to that class.
 * It keeps track of what the current working directory is.
 * 
 * @author aimana
 *
 */
public class FileManager {

	private final FileManagerPrintStream printer;
	private File workingDirectory;

	public FileManager(FileManagerPrintStream printer, File workingDirectory) {
		this.printer = printer;
		this.workingDirectory = workingDirectory;
	}

	public void processCommand(String command) {
		if(command == null || command.trim().length() == 0) {
			return;
		}
		//Split the string into command and arguments
		final String[] split = command.trim().split(" ", 2);
		
		ShellCommand handler = SupportedCommands.getCommandHandlerInstance(split[0]);
		if(handler == null) {
			printer.println("Unknown command: " + command);
		} else {
			workingDirectory = handler.handle(split.length > 1 ? split[1] : "", printer, workingDirectory);
		}
	}

}
