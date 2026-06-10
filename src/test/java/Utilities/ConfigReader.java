package Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigReader {
	 Properties prop;
	 String path = null;

	    public ConfigReader() {

	        try {
				
	        	
	        	//Hardcoded env
				  path = Paths.get( System.getProperty("user.dir"), "Resources",
				  "Config.properties" ).toString();
				 
	        	
	        	
	        	//Dynamic env code
				/*
				 * String env = System.getProperty("env", "stage");
				 * 
				 * String configFile = env.equalsIgnoreCase("live") ? "Config-Live.properties" :
				 * "Config-Stage.properties";
				 * 
				 * path = Paths.get( System.getProperty("user.dir"), "Resources", configFile
				 * ).toString();
				 */
	            FileInputStream fis =
	                new FileInputStream(
	                    path);

	            prop = new Properties();
	            prop.load(fis);
				/*
				 * System.out.println("Environment = " + env);
				 * System.out.println("Config File = " + configFile);
				 * System.out.println("Path = " + path);
				 */

	        } catch (IOException e) {

	            e.printStackTrace();
	        }
	    }

	    public String getProperty(String key) {

	        return prop.getProperty(key);
	    }
	

}
