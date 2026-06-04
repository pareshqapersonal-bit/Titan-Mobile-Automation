package Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
	 Properties prop;

	    public ConfigReader() {

	        try {

	            FileInputStream fis =
	                new FileInputStream(
	                    System.getProperty("user.dir")
	                    + "\\Resources\\Config.properties");

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
