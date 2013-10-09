package nz.co.ahc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Telnet server that runs on a port specified in server.cfg. The default port is 8080.
 * The server allows multiple concurrent clients to browse the file system.
 * 
 * Supported commands:
 * ls
 * pwd
 * mkdir
 * cd
 * touch
 *
 * @author Aiman Alsari
 */

public class TelnetServer {

	private ServerSocket serverSocket;

	public static void main( String[] args ) {
		Configuration.load();
		TelnetServer server = new TelnetServer();
		server.start();
	}

	public void start() {
		ExecutorService executor = Executors.newFixedThreadPool(Configuration.getMaximumNumberOfClients());
		InetAddress address; //The address that we want to bind our socket to.
		
		//First try and bind to whatever is configured in the config file. If that is not specified,
		//then we'll try to bind to any IP, if not possible for whatever reason, then we'll fall back to the loopback address
		try {
			if(Configuration.getAddressToBind() != null) {
				address = InetAddress.getByName(Configuration.getAddressToBind());
			} else {
				address = InetAddress.getByName("0.0.0.0"); 
			}
		} catch (UnknownHostException e) {
			System.out.println("WARNING: Unable to get the address of the host to bind to "
						+Configuration.getAddressToBind() == null ? "" : Configuration.getAddressToBind()+", " +
								"falling back to loopback address");
			address = InetAddress.getLoopbackAddress();
		}

		//Create the socket
		try {
			serverSocket = new ServerSocket(Configuration.getPort(), Configuration.getMaximumNumberOfClients(), address);
		} catch (IOException e) {
			System.err.println("Error binding to address: " + e.getMessage());
			//No need for finally here, no sockets to close.
			return;
		}

		//Start accepting connections and fire them off to the FileManagerCLI class
		try {
			System.out.println("Telnet Server started and accepting connections.");
			while(true && !serverSocket.isClosed()) {
				final Socket socket = serverSocket.accept();
				executor.execute(new TelnetSocketShell(socket));
			}
		} catch (IOException e) {
			System.out.println("Server shutdown initiated...");
		} finally {
			shutdown();
		}
	}

	public void shutdown() {
		if(serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) { /*Only happens if its already closed, in which case our job is complete */ }
		}
	}

}
