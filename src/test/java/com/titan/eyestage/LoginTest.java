package com.titan.eyestage;

import java.time.Duration;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import POM.LoginElements;
import io.appium.java_client.android.AndroidDriver;

public class LoginTest extends Demo {
	
	
	
	
	@Test
	public void Steps() throws InterruptedException
	{  

		LoginElements le = new LoginElements(driver);
		le.testexecution();
		le.logoutSteps();
		
	}

}
