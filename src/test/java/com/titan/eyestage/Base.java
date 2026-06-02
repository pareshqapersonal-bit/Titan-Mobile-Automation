package com.titan.eyestage;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class Base{
	protected AndroidDriver driver;
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
}