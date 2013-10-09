package nz.co.ahc;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//The best way to test this is to use the telnet client itself!
public class TelnetServerAcceptanceTest {

	private TelnetServer server;
	private TelnetTestClient client;

	@Before
	public void setup() {
		Configuration.load();
		server = new TelnetServer();
		client = new TelnetTestClient();
	}

	private void startServer() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				server.start();
			}
		});
		t.start();
	}
	
	@Test
	public void testDefaultPortAcceptsConnectionsAndDisplaysPrompt() throws IOException {
		//Sanity check it first, make sure nothing is there before starting.
		client.connect();
		assertEquals(
				"Trying 127.0.0.1...\n" + 
				"telnet: connect to address 127.0.0.1: Connection refused\n", client.readAndClear());

		startServer();
		waitForServerReady();
		
		client.connect();
		assertEquals(
				"Trying 127.0.0.1...\n" +
				"Connected to localhost.\n" + 
				"Escape character is '^]'.\n" +
				"> \n", client.readAndClear());
	}
	
	@Test
	public void testMultipleClientConnectionsAreAccepted() throws IOException {
		startServer();
		waitForServerReady();
		
		client.connect();
		assertEquals(
				"Trying 127.0.0.1...\n" +
				"Connected to localhost.\n" + 
				"Escape character is '^]'.\n" +
				"> \n", client.readAndClear());
		
		TelnetTestClient client2 = new TelnetTestClient();
		client2.connect();
		
		assertEquals(
				"Trying 127.0.0.1...\n" +
				"Connected to localhost.\n" + 
				"Escape character is '^]'.\n" +
				"> \n", client2.readAndClear());
		client2.shutdown();
	}
	
	@Test
	public void testPwdCommandDefaultsToLaunchDirectory() throws IOException {
		startServer();
		waitForServerReady();
		
		client.connect();
		client.sendLine("pwd");
		assertEquals(
				"Trying 127.0.0.1...\n" + 
				"Connected to localhost.\n" + 
				"Escape character is '^]'.\n" + 
				"> \n" + 
				Configuration.getHomeDirectory() + "\n" + 
				"> \n"
				, client.readAndClear());
	}
	
	private void waitForServerReady() {
		//TODO More sleeping nonsense to get rid of.
		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
	}

	@After
	public void destroy() {
		client.shutdown();
		server.shutdown();
	}

}
