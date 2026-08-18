package Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigManager {

    private static final String DEFAULT_ENVIRONMENT = "stage";
    private static final String DEFAULT_EXECUTION_MODE = "local";

    private final Properties properties;

    public ConfigManager() {
        properties = new Properties();
        loadConfiguration();
    }

    private void loadConfiguration() {

        try {
            // 1. Load common configuration
            Path baseConfigPath = Paths.get(
                    System.getProperty("user.dir"),
                    "Resources",
                    "Config.properties"
            );

            loadProperties(baseConfigPath);

            // 2. Resolve environment
            String environment = getConfigurationValue(
                    "environment",
                    "ENVIRONMENT",
                    DEFAULT_ENVIRONMENT
            );

            // 3. Load environment-specific configuration
            Path environmentConfigPath = Paths.get(
                    System.getProperty("user.dir"),
                    "Resources",
                    "config-" + environment.toLowerCase() + ".properties"
            );
            properties.setProperty("environment", environment);
            loadProperties(environmentConfigPath);

            // 4. Resolve execution mode
            String executionMode = getConfigurationValue(
                    "executionMode",
                    "EXECUTION_MODE",
                    DEFAULT_EXECUTION_MODE
            );
            properties.setProperty("executionMode", executionMode);
            // 5. Load BrowserStack configuration when required
            if ("browserstack".equalsIgnoreCase(executionMode)) {

                Path browserStackConfigPath = Paths.get(
                        System.getProperty("user.dir"),
                        "Resources",
                        "config-browserstack.properties"
                );

                loadProperties(browserStackConfigPath);
                
                if ("browserstack".equalsIgnoreCase(executionMode)) {

                    String browserStackUsername =
                            getSecret("browserstack.username", "BROWSERSTACK_USERNAME");

                    String browserStackAccessKey =
                            getSecret("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");

                    properties.setProperty(
                            "browserstack.username",
                            browserStackUsername);

                    properties.setProperty(
                            "browserstack.accessKey",
                            browserStackAccessKey);
                }
            }

            System.out.println("Environment    = " + environment);
            System.out.println("Execution Mode = " + executionMode);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load configuration files.",
                    e
            );
        }
    }

    private void loadProperties(Path path) throws IOException {

        try (FileInputStream inputStream =
                     new FileInputStream(path.toFile())) {

            properties.load(inputStream);
        }
    }

    private String getConfigurationValue(
            String propertyKey,
            String environmentVariable,
            String defaultValue) {

        // 1. JVM system property
        String systemProperty = System.getProperty(propertyKey);

        if (systemProperty != null &&
                !systemProperty.trim().isEmpty()) {

            return systemProperty.trim();
        }

        // 2. OS environment variable
        String environmentValue =
                System.getenv(environmentVariable);

        if (environmentValue != null &&
                !environmentValue.trim().isEmpty()) {

            return environmentValue.trim();
        }

        // 3. Properties file
        String propertyValue =
                properties.getProperty(propertyKey);

        if (propertyValue != null &&
                !propertyValue.trim().isEmpty()) {

            return propertyValue.trim();
        }

        // 4. Default
        return defaultValue;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    
    private String getSecret(
            String propertyKey,
            String environmentVariable) {

        String systemProperty = System.getProperty(propertyKey);

        if (systemProperty != null &&
                !systemProperty.trim().isEmpty()) {

            return systemProperty.trim();
        }

        String environmentValue =
                System.getenv(environmentVariable);

        if (environmentValue != null &&
                !environmentValue.trim().isEmpty()) {

            return environmentValue.trim();
        }

        throw new IllegalStateException(
                "Required secret is not configured: "
                        + environmentVariable);
    }
}