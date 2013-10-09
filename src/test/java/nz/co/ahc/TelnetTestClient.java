package nz.co.ahc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Simple wrapper around the telnet client executable that can send and receive text.
 * @author Aiman Alsari
 *
 */
public class TelnetTestClient {

	private final String host;
	private final int port;
	private Process telnetProcess;

	public TelnetTestClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Connect to a server on the localhost address using the default port defined in <code>TelnetServer</code>.
	 */
	public TelnetTestClient() {
		this.host = "localhost";
		this.port = Configuration.getPort();
	}

	public void connect() throws IOException {
		telnetProcess = Runtime.getRuntime().exec("telnet " + host + " " + port);
	}
	
	public void shutdown() {
		if(telnetProcess != null) {
			telnetProcess.destroy();
		}
	}
	
	public String readAndClear() throws IOException {
		String output = getStreamContents(telnetProcess.getInputStream());
		output += getStreamContents(telnetProcess.getErrorStream());
		return output;
	}

	//TODO do this in a better way (NIO), as it currently has a sleep (blergh) and synchronisation issues. But for now it will suffice.
	private String getStreamContents(final InputStream stream) throws IOException {
		final StringBuffer output = new StringBuffer();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		//Have to do this in a new thread as it will sit and block waiting for input.
		Thread t = new Thread(new Runnable() {
			
			public void run() {
				String line;
				try {
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
						output.append(line+"\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
			
		});
		t.start();

		//Adding a bit of fragility and test feedback latency here, but 100ms for a telnet client to respond before it starts 
		//blocking and waiting for input should be sufficient time even on the slowest machines. Famous last words.
		try {Thread.sleep(100);} catch (InterruptedException e) {/*Doesnt matter*/}
		
		return output.toString();
	}

	public void sendLine(String line) throws IOException {
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(telnetProcess.getOutputStream()), true);
		writer.println(line);
	}
	
}
