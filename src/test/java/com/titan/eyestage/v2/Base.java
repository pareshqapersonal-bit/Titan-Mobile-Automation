package com.titan.eyestage.v2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import java.lang.reflect.Method;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import com.titan.eyestage.v2.pom.LoginElements;
import com.titan.eyestage.v2.utils.ConfigManager;
import com.titan.eyestage.v2.utils.ExtentManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.util.HashMap;
import java.util.Map;

public class Base {

	// Thread-local so parallel <test> blocks (e.g. two BrowserStack devices
	// running concurrently) never share/overwrite each other's driver or
	// report node.
	private static final ThreadLocal<AndroidDriver> driverHolder = new ThreadLocal<>();
	private static final ThreadLocal<ExtentTest> testHolder = new ThreadLocal<>();

	public static ExtentReports extent;

	protected static AndroidDriver driver() {
		return driverHolder.get();
	}

	protected static void setDriver(AndroidDriver d) {
		driverHolder.set(d);
	}

	protected static void removeDriver() {
		driverHolder.remove();
	}

	public static ExtentTest test() {
		return testHolder.get();
	}

	public static void setTest(ExtentTest t) {
		testHolder.set(t);
	}

	@BeforeSuite
	public synchronized void setupReport() {

		extent = ExtentManager.getInstance();

		System.out.println("Extent Report Started (v2 parallel flow)");
	}

	@BeforeMethod
	public void opn_app(Method testMethod, Object[] testData) throws MalformedURLException {

		// testData is the row TestNG resolved from the @Test's data provider
		// for the run about to start (e.g. loginDevices -> {number, pass,
		// deviceName, osVersion}). Each parallel data-provider thread gets
		// its own row here, which is how each thread targets its own device.
		String bsDeviceName = "";
		String bsOsVersion = "";

		if (testData != null && testData.length >= 4) {
			bsDeviceName = String.valueOf(testData[2]);
			bsOsVersion = String.valueOf(testData[3]);
		}

		UiAutomator2Options options = new UiAutomator2Options();
		ConfigManager config = new ConfigManager();
		String env = config.getProperty("environment");

		String executionMode = config.getProperty("executionMode");

		options.setPlatformName(config.getProperty("platformName"));
		options.setDeviceName(config.getProperty("deviceName")); // change if needed

		if (env.equalsIgnoreCase("stage") && executionMode.equalsIgnoreCase("local")) {

			options.setApp(config.getProperty("apkPath"));

			options.setCapability(
					"appPackage",
					config.getProperty("appPackage"));

			options.setCapability(
					"appActivity",
					config.getProperty("appActivity"));

			options.setCapability("appWaitActivity", "*");

			// Preserve app data, login state and granted permissions
			options.setNoReset(true);

			// But still launch the app when the Appium session starts
			options.setCapability("forceAppLaunch", true);
		} else {
			options.setCapability("appPackage",
					config.getProperty("appPackage"));

			options.setCapability("appActivity",
					config.getProperty("appActivity"));

			options.setNoReset(true);

			options.setCapability("dontStopAppOnReset", true);
		}

		System.out.println("Execution Mode = " + executionMode);
		System.out.println("Driver URL = " + ("browserstack".equalsIgnoreCase(executionMode)
				? "BrowserStack"
				: config.getProperty("appiumURL")));

		AndroidDriver localDriver;

		if ("local".equalsIgnoreCase(executionMode)) {
			options.setAutoGrantPermissions(true);

			options.setAppWaitDuration(Duration.ofSeconds(30));
			options.setCapability("chromedriverAutodownload", true);
			options.setCapability("ensureWebviewsHavePages", true);
			options.setCapability("webviewConnectTimeout", 20000);
			localDriver = new AndroidDriver(
					new URL(config.getProperty("appiumURL")),
					options);
			setDriver(localDriver);

		} else if ("browserstack".equalsIgnoreCase(executionMode)) {

			// A TestNG <parameter> (one per <test> block) lets each parallel
			// thread target a different BrowserStack device; falls back to
			// config-browserstack.properties when no parameter is supplied.
			String deviceName = (bsDeviceName != null && !bsDeviceName.isEmpty())
					? bsDeviceName
					: config.getProperty("browserstack.device");

			String osVersion = (bsOsVersion != null && !bsOsVersion.isEmpty())
					? bsOsVersion
					: config.getProperty("browserstack.osVersion");

			Map<String, Object> bstackOptions = new HashMap<>();

			bstackOptions.put("userName",
					config.getProperty("browserstack.username"));

			bstackOptions.put("accessKey",
					config.getProperty("browserstack.accessKey"));

			bstackOptions.put("projectName",
					config.getProperty("browserstack.project"));

			bstackOptions.put("buildName",
					config.getProperty("browserstack.build"));

			bstackOptions.put("sessionName",
					config.getProperty("browserstack.session") + " - " + deviceName);

			// BrowserStack idle timeout
			bstackOptions.put("idleTimeout", 300);

			options.setCapability("bstack:options", bstackOptions);

			options.setDeviceName(deviceName);

			options.setPlatformVersion(osVersion);

			options.setApp(
					config.getProperty("browserstack.app"));

			localDriver = new AndroidDriver(
					new URL("https://hub-cloud.browserstack.com/wd/hub"),
					options);
			setDriver(localDriver);

			System.out.println("Device = " + deviceName + " | OS = " + osVersion);
			System.out.println("Session ID = " + driver().getSessionId());
		}

		// For live
		if (config.getProperty("environment").equalsIgnoreCase("live")) {
			System.out.println("Environment = " + config.getProperty("environment"));
			System.out.println("App Package = " + config.getProperty("appPackage"));
			System.out.println("Activating LIVE app...");
			driver().activateApp(config.getProperty("appPackage"));
		}

		System.out.println("Session ID = " + driver().getSessionId());

		System.out.println("Package = " + driver().getCurrentPackage());
		System.out.println("Activity = " + driver().currentActivity());
		System.out.println("App launched successfully");

		driver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		LoginElements le = new LoginElements(driver());
		le.permissionPopup();
		System.out.println("Package = " + driver().getCurrentPackage());
		System.out.println("Activity = " + driver().currentActivity());
	}

	// Screenshot function - returns a Base64-encoded PNG string for direct embedding into the Extent report
	public String captureScreenshot(String testName) throws IOException {
		return ((TakesScreenshot) driver()).getScreenshotAs(OutputType.BASE64);
	}

	@AfterMethod
	public void tearDown() {

		if (driver() != null) {
			driver().quit();
			removeDriver();
		}
	}
}
