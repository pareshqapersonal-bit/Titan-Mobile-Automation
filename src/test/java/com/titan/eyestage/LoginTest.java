package com.titan.eyestage;

import java.time.Duration;

import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import POM.LoginElements;
import io.appium.java_client.android.AndroidDriver;

public class LoginTest extends Base {
	
	
	
	
	@Test
	public void Steps() throws InterruptedException
	{  

		test = extent.createTest("Login Test");

	    test.info("Launching App");

	    LoginElements le = new LoginElements(driver);

	    le.testexecution();
	    

	    test.pass("Login Successful");
		le.logoutSteps();
		
	}

}
