package nz.co.ahc.command;

import java.io.PrintStream;

/**
 * Wraps a print stream to ease mocking, as a print stream has far too many methods.
 * @author aimana
 *
 */
public class FileManagerPrintStream {

	private PrintStream stream;
	
	public FileManagerPrintStream(PrintStream stream) {
		this.stream = stream;
	}
	
	public void println(String string) {
		stream.println(string);
	}
	
}
