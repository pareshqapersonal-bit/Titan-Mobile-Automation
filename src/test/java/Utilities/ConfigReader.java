package Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigReader {

    private Properties prop;
    private String path;

    public ConfigReader() {

        try {

            // Step 1: Load Config.properties
            path = Paths.get(
                    System.getProperty("user.dir"),
                    "Resources",
                    "Config.properties")
                    .toString();

            FileInputStream fis = new FileInputStream(path);

            prop = new Properties();
            prop.load(fis);

            // Step 2: Read environment from Config.properties
            String env = prop.getProperty("environment", "stage");

            // Step 3: Decide which config file to load
            String configFile = env.equalsIgnoreCase("live")
                    ? "config-live.properties"
                    : "config-stage.properties";

            // Step 4: Load environment-specific properties
            path = Paths.get(
                    System.getProperty("user.dir"),
                    "Resources",
                    configFile)
                    .toString();

            fis = new FileInputStream(path);

            prop = new Properties();
            prop.load(fis);

            System.out.println("Environment = " + env);
            System.out.println("Config File = " + configFile);
            System.out.println("Path = " + path);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}