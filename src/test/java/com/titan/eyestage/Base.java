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

import Utilities.ExtentManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class Base{
	protected AndroidDriver driver;
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

        options.setPlatformName("Android");
        options.setDeviceName("Vivo V25"); // change if needed

        String apkPath = System.getProperty("user.home")
                + "\\Downloads\\TEP_STAGE.apk";
        options.setApp(apkPath);
        options.setAutoGrantPermissions(true);
        options.setNoReset(false);
        options.setAppWaitDuration(Duration.ofSeconds(30));
//
//        AppiumDriver driver = new AppiumDriver(
//                new URL("http://127.0.0.1:4723"),
//                options);
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
    

        System.out.println("App launched successfully");
       // driver.findElement(By.xpath("//android.widget.RelativeLayout[@resource-id=\"com.titan.eyecare:id/rl_toolbar_app\"]")).click();
  driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }
    
    
    @AfterMethod
    public void endThesession()
    {
    	
    }
    
    @AfterSuite
    public void tearDownReport() {

        extent.flush();

        System.out.println("Extent Report Generated");
    }
    
    //Screenshot function
    public String captureScreenshot(String testName) throws IOException {

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
    @AfterMethod
    public void tearDown(ITestResult result) throws IOException {

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
        }
    }
    
    

    
}