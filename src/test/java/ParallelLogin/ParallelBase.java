package ParallelLogin;

import java.net.MalformedURLException;
import java.time.Duration;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;

public class ParallelBase {

	private static ThreadLocal<AndroidDriver> driver = new ThreadLocal<>();
	
	protected synchronized AndroidDriver getDriver() {
		return driver.get();
	}
	protected synchronized void createDriver() throws MalformedURLException {
		DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability(
                "appium:deviceName",
                "Samsung Galaxy S24"
        );
        capabilities.setCapability(
                "appium:platformVersion",
                "14.0"
        );
        capabilities.setCapability(
                "appium:app",
                "bs://bc1628b54fc74d82e5ef7d7e1ae2f0ddd3d95e06"
        );

        capabilities.setCapability(
                "bstack:options",
                new java.util.HashMap<String, Object>() {{
                    put("userName", System.getenv("BROWSERSTACK_USERNAME"));
                    put("accessKey", System.getenv("BROWSERSTACK_ACCESS_KEY"));
                    put("projectName", "Titan Mobile Automation");
                    put("buildName", "Login Parallel Learning");
                    put("sessionName", "Login Parallel Test");
                }}
        );

        AndroidDriver androidDriver =
                new AndroidDriver(
                        new URL("https://hub-cloud.browserstack.com/wd/hub"),
                        capabilities
                );

        androidDriver.manage()
                .timeouts()
                .implicitlyWait(Duration.ofSeconds(10));

        driver.set(androidDriver);
    }

    protected void quitDriver() {

        AndroidDriver androidDriver = driver.get();

        if (androidDriver != null) {
            androidDriver.quit();
            driver.remove();
        }
    }
}
