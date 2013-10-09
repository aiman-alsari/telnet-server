package nz.co.ahc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import nz.co.ahc.command.FileManager;
import nz.co.ahc.command.FileManagerPrintStream;

/**
 * This class represents a client's connection and is the bare bones shell prompt. It abstracts away all socket related
 * code from the FileManager.
 * @author aimana
 *
 */
public class TelnetSocketShell implements Runnable {

	private final Socket socket;

	public TelnetSocketShell(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			final PrintStream printStream = new PrintStream(socket.getOutputStream(), true);
			final FileManager cli = new FileManager(new FileManagerPrintStream(printStream), new File(Configuration.getHomeDirectory()));
			
			String command;
			printStream.println("> ");
			while((command = reader.readLine()) != null) {
				command = command.trim();
				if(command.equals("exit") || command.equals("quit")) {
					break;
				}
				//Delegate all the work to the FileManager
				cli.processCommand(command);
				printStream.println("> ");
			}
		} catch (IOException e) {
			System.err.println("Error writing to socket streams: " + e.getMessage());
		}
	}
}
