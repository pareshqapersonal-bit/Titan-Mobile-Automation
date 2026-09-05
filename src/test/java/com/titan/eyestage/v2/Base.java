package com.titan.eyestage.v2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import java.lang.reflect.Method;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
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

	// Captured once at session start, independent of the driver object's health, so the
	// BrowserStack REST API fallback in setSessionStatus() can still identify the session even if
	// the driver has already died mid-test (e.g. SO_TIMEOUT) by the time status is reported.
	private static final ThreadLocal<String> sessionIdHolder = new ThreadLocal<>();

	public static ExtentReports extent;

	protected static AndroidDriver driver() {
		return driverHolder.get();
	}

	protected static void setDriver(AndroidDriver d) {
		driverHolder.set(d);
	}

	protected static void removeDriver() {
		driverHolder.remove();
		sessionIdHolder.remove();
	}

	protected static String sessionId() {
		return sessionIdHolder.get();
	}

	protected static void setSessionId(String id) {
		sessionIdHolder.set(id);
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

			// Same webview-stabilization capabilities as the local branch above -
			// without these, a WebView getting torn down mid-flow (e.g. the Razorpay
			// payment webview) can leave the driver's webview context bookkeeping
			// stale on BrowserStack's shared real devices.
			options.setCapability("chromedriverAutodownload", true);
			options.setCapability("ensureWebviewsHavePages", true);
			options.setCapability("webviewConnectTimeout", 20000);

			localDriver = new AndroidDriver(
					new URL("https://hub-cloud.browserstack.com/wd/hub"),
					options);
			setDriver(localDriver);

			setSessionId(driver().getSessionId().toString());

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

	// TestNG runs @AfterMethod BEFORE notifying ITestListener.onTestSuccess/onTestFailure/
	// onTestSkipped - so reporting the BrowserStack session status from those listener callbacks
	// (as TestListener used to) always ran too late: this same tearDown() had already quit the
	// driver and cleared the ThreadLocal session id, leaving setSessionStatus() with nothing to
	// report against. Every session therefore sat on BrowserStack as "Unknown" regardless of
	// pass/fail, except where BrowserStack's own crash/ANR auto-detection happened to flag one as
	// failed on its own. Reporting status here, before driver().quit(), is what makes it work.
	@AfterMethod
	public void tearDown(ITestResult result) {

		reportSessionStatus(result);

		try {
			if (driver() != null) {
				driver().quit();
			}
		} catch (Exception e) {
			// quit() throws whenever the BrowserStack session already died mid-test (session
			// termination, device disconnect, etc). Selenium nulls the driver's local session id
			// before that exception propagates, so the object left in the ThreadLocal is already
			// dead either way - swallow this so removeDriver() below still runs, otherwise a
			// later invocation reusing this pooled thread inherits a driver that throws
			// NoSuchSessionException on its very first command.
			System.out.println("Driver quit failed (session likely already terminated): " + e.getMessage());
		} finally {
			removeDriver();
		}
	}

	private void reportSessionStatus(ITestResult result) {

		if (result.getStatus() == ITestResult.SUCCESS) {
			setSessionStatus("passed", "Test passed");
			return;
		}

		// A retried invocation is reclassified SKIP (not FAILURE) by TestNG, but the original
		// throwable stays attached to it - without checking for that here, a retried case's
		// first (failed) attempt would be reported to BrowserStack as a plain skip instead of the
		// real error that triggered the retry. A genuine skip (no retry involved) has no
		// throwable, so it still falls back to a plain skip reason.
		if (result.getStatus() == ITestResult.SKIP && result.getThrowable() == null) {
			setSessionStatus("failed", "Test skipped");
			return;
		}

		// FAILURE, or SKIP-with-throwable (a first attempt about to be retried on a fresh
		// session) - both ran a real BrowserStack session that hit an error. BrowserStack only
		// accepts passed/failed, so both get marked failed here, or the session is left stuck on
		// "Unknown" same as a real failure would be.
		attachFailureScreenshot(result);
		setSessionStatus("failed", failureReason(result));
	}

	private static final HttpClient BS_REST_CLIENT = HttpClient.newBuilder()
	        .connectTimeout(Duration.ofSeconds(10))
	        .build();

	// Vanilla Appium (no BrowserStack SDK) never reports pass/fail back to BrowserStack on its
	// own - a plain driver.quit() just ends the session, which is why App Automate shows
	// "Unknown" instead of Passed/Failed. The documented workaround without the SDK is the
	// browserstack_executor JS call, made here (before driver().quit() below on the same
	// invocation) so it targets this invocation's own session.
	//
	// The JS route needs a live WebDriver connection. If the session already died mid-test
	// (e.g. SO_TIMEOUT) the executeScript call fails the same way any other driver command
	// would, so this falls back to the BrowserStack REST API using the session ID captured
	// at session start in opn_app() - that ID is a plain field, not a network call, so
	// it's still available even when the driver connection itself is gone.
	private void setSessionStatus(String status, String reason) {

	    String capturedSessionId = sessionId();

	    try {

	        if (driver() != null && driver().getSessionId() != null) {

	            String liveSessionId = driver().getSessionId().toString();

	            String script = "browserstack_executor: {\"action\": \"setSessionStatus\", "
	                    + "\"arguments\": {\"status\":\"" + status + "\", \"reason\": \""
	                    + sanitizeForJson(reason) + "\"}}";

	            System.out.println("[BS-DIAG] sessionId=" + liveSessionId
	                    + " status=" + status + " reason=" + reason);

	            Object result = driver().executeScript(script);

	            System.out.println("BrowserStack setSessionStatus executeScript SUCCESS");
	            System.out.println("[BS-DIAG] executeScript returned: " + result);

	        } else if (capturedSessionId != null) {

	            System.out.println("[BS-DIAG] driver/session unavailable, using REST fallback for sessionId="
	                    + capturedSessionId);
	            restMarkSessionStatus(capturedSessionId, status, reason);
	        }

	    } catch (Exception e) {
	        System.out.println("[BS-DIAG] executeScript threw " + e.getClass().getName()
	                + ": " + e.getMessage());

	        if (capturedSessionId != null) {
	            System.out.println("[BS-DIAG] falling back to REST API for sessionId=" + capturedSessionId);
	            restMarkSessionStatus(capturedSessionId, status, reason);
	        }
	    }
	}

	// REST fallback for when the JS executor path is unusable (dead/unreachable session).
	// Same BrowserStack credentials opn_app() already uses to start the session.
	private void restMarkSessionStatus(String sessionId, String status, String reason) {

	    try {

	        ConfigManager config = new ConfigManager();

	        String username = config.getProperty("browserstack.username");
	        String accessKey = config.getProperty("browserstack.accessKey");

	        String credentials = Base64.getEncoder().encodeToString(
	                (username + ":" + accessKey).getBytes(StandardCharsets.UTF_8));

	        String body = "{\"status\":\"" + status + "\", \"reason\": \""
	                + sanitizeForJson(reason) + "\"}";

	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create("https://api.browserstack.com/app-automate/sessions/" + sessionId + ".json"))
	                .header("Authorization", "Basic " + credentials)
	                .header("Content-Type", "application/json")
	                .timeout(Duration.ofSeconds(15))
	                .PUT(HttpRequest.BodyPublishers.ofString(body))
	                .build();

	        HttpResponse<String> response =
	                BS_REST_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

	        System.out.println("[BS-DIAG] REST setSessionStatus sessionId=" + sessionId
	                + " status=" + status + " httpStatus=" + response.statusCode());
	        System.out.println("[BS-DIAG] REST setSessionStatus response body: " + response.body());

	    } catch (Exception e) {
	        System.out.println("[BS-DIAG] REST setSessionStatus threw " + e.getClass().getName()
	                + ": " + e.getMessage());
	    }
	}

	private static String failureReason(ITestResult result) {

	    Throwable t = result.getThrowable();

	    if (t == null) {
	        return "Test failed";
	    }

	    String message = t.getMessage();

	    return message == null || message.isEmpty()
	            ? t.getClass().getSimpleName()
	            : message;
	}

	private static String sanitizeForJson(String value) {

	    if (value == null) {
	        return "";
	    }

	    return value
	            .replace("\\", "\\\\")
	            .replace("\"", "\\\"")
	            .replace("\n", " ")
	            .replace("\r", " ")
	            .replace("\t", " ");
	}

	private void attachFailureScreenshot(ITestResult result) {

	    try {

	        if (driver() != null && driver().getSessionId() != null) {

	            String screenshotPath =
	                    captureScreenshot(result.getName());

	            test().info(
	                    "<a href='data:image/png;base64," + screenshotPath
	                            + "' data-featherlight='image'><img src='data:image/png;base64,"
	                            + screenshotPath
	                            + "' style='width:200px;height:auto;cursor:pointer;'/></a>");

	            System.out.println(
	                    "Failure screenshot captured: " + screenshotPath);

	        } else {

	            System.out.println(
	                    "Screenshot skipped: Appium session is not available.");

	        }

	    } catch (Exception e) {

	        System.out.println(
	                "Could not capture failure screenshot: "
	                + e.getMessage());
	    }
	}
}
