package nz.co.ahc.command;

import java.io.File;

public interface ShellCommand {

	File handle(String arguments, FileManagerPrintStream printer, File workingDirectory);

}
