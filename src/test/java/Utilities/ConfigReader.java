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
	        	path = Paths.get(
	        		    System.getProperty("user.dir"),
	        		    "Resources",
	        		    "Config.properties"
	        		).toString();

	            FileInputStream fis =
	                new FileInputStream(
	                    path);

	            prop = new Properties();
	            prop.load(fis);

	        } catch (IOException e) {

	            e.printStackTrace();
	        }
	    }

	    public String getProperty(String key) {

	        return prop.getProperty(key);
	    }
	

}
