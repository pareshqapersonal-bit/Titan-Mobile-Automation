package com.titan.eyestage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import POM.LoginElements;
import Utilities.*;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class Base{
	protected static AndroidDriver driver;
	  public static ExtentReports extent;
	  public static ExtentTest test;
	  
	@BeforeSuite
    public void setupReport() {

        extent = ExtentManager.getInstance();

        System.out.println("Extent Report Started");
    }
	
    @BeforeMethod
    public void opn_app() throws MalformedURLException {

        UiAutomator2Options options = new UiAutomator2Options();
        ConfigReader config = new ConfigReader();
        String env = config.getProperty("environment");
        
        options.setPlatformName(config.getProperty("platformName"));
        options.setDeviceName(config.getProperty("deviceName")); // change if needed

		/*
		 * String apkPath = config.getProperty("apkPath"); options.setApp(apkPath);
		 * options.setCapability( "appPackage", config.getProperty("appPackage"));
		 * options.setCapability( "appActivity", config.getProperty("appActivity"));
		 */
        
        if(env.equalsIgnoreCase("stage"))
        {
        	 options.setApp(config.getProperty("apkPath"));

        	    options.setCapability("appPackage",
        	            config.getProperty("appPackage"));

        	    options.setCapability("appActivity",
        	            config.getProperty("appActivity"));

        	    options.setCapability("appWaitActivity", "*");

        	    options.setNoReset(false);
        }
        else
        {
        	options.setCapability("appPackage",
                    config.getProperty("appPackage"));

            options.setCapability("appActivity",
                    config.getProperty("appActivity"));

            options.setNoReset(true);

            options.setCapability("dontStopAppOnReset", true);
        }
        options.setAutoGrantPermissions(true);
       
        options.setAppWaitDuration(Duration.ofSeconds(30));
        options.setCapability("chromedriverAutodownload", true);
        options.setCapability("ensureWebviewsHavePages", true);
        options.setCapability("webviewConnectTimeout", 20000);
//
//        AppiumDriver driver = new AppiumDriver(
//                new URL("http://127.0.0.1:4723"),
//                options);
        driver = new AndroidDriver(new URL(config.getProperty("appiumURL")), options);
        //For live
        if (config.getProperty("environment").equalsIgnoreCase("live")) {
        	System.out.println("Environment = " + config.getProperty("environment"));
        	System.out.println("App Package = " + config.getProperty("appPackage"));
            System.out.println("Activating LIVE app...");
            driver.activateApp(config.getProperty("appPackage"));
        }

       
    
        System.out.println("Package = " + driver.getCurrentPackage());
        System.out.println("Activity = " + driver.currentActivity());
        System.out.println("App launched successfully");
       // driver.findElement(By.xpath("//android.widget.RelativeLayout[@resource-id=\"com.titan.eyecare:id/rl_toolbar_app\"]")).click();
        
  driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
  LoginElements le = new LoginElements(driver);
  le.permissionPopup();
    }
    
  
    
    @AfterSuite
    public void tearDownReport() {

        extent.flush();

        System.out.println("Extent Report Generated");
    }
    
    //Screenshot function
    public String captureScreenshot(String testName) throws IOException {
    	File folder = new File("Screenshots");
    	if (!folder.exists()) {
    	    folder.mkdirs();
    	}
        File src = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.FILE);

        String path = System.getProperty("user.dir")
                + "\\Screenshots\\"
                + testName + ".png";

        File dest = new File(path);

        Files.copy(src.toPath(), dest.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        return path;
    }
    
    //after method
   
    /*public void tearDown(ITestResult result) throws IOException {

        if(result.getStatus() == ITestResult.FAILURE) {

            String screenshotPath =
                    captureScreenshot(result.getName());

            test.fail(result.getThrowable());

            test.addScreenCaptureFromPath(screenshotPath);
        }

        else if(result.getStatus() == ITestResult.SUCCESS) {

            test.pass("Test Passed");
        }

        else {

            test.skip("Test Skipped");
        }*/
    
    @AfterMethod
    public void tearDown() {

        if(driver != null) {
            driver.quit();
        }
    }
    }
    
    

    
