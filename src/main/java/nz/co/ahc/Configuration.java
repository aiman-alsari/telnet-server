package nz.co.ahc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
/**
 * Configuration class to represent the properties file, server.cfg
 * 
 * The cfg file is a standard key-value pair properties file.
 * The available properties are:
 * 
 * port=[The port to run the server on, default 8080]
 * host=[The host name or IP address that we are binding this socket to, defaults to 0.0.0.0, falls back to the loopback interface]
 * max_clients=[The maximum number of clients and threads that are run before the server starts blocking, default 10]
 * home_dir=[The directory that the file manager starts off at, default is the directory that the Server is located in]
 * 
 * @author aimana
 *
 */
public class Configuration {

	private static Properties properties;

	public static void load() {
		properties = new Properties();
		try {
			properties.load(new FileInputStream(new File("server.cfg")));
		} catch (FileNotFoundException e) {
			System.out.println("Warning: server.cfg not found, using default settings.");
		} catch (IOException e) {
			System.err.println("Error attempting to load properties: " + e.getMessage());
		}
	}
	
	public static int getMaximumNumberOfClients() {
		return getIntProperty("max_clients", 10);
	}
	
	public static int getPort() {
		return getIntProperty("port", 8080);
	}
	
	public static String getHomeDirectory() {
		return properties.getProperty("home_dir", System.getProperty("user.dir"));
	}
	
	private static int getIntProperty(String key, int defValue) {
		try{
			return Integer.parseInt(properties.getProperty(key, "" + defValue));
		} catch (NumberFormatException e) {
			System.err.println("Error: "+key+" in server.cfg needs to be an integer.");
			return defValue;
		}
	}

	public static String getAddressToBind() {
		return properties.getProperty("host");
	}
	
}
