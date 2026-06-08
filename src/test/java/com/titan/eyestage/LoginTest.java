package com.titan.eyestage;

import java.io.IOException;
import java.time.Duration;

import javax.swing.text.Utilities;

import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import POM.LoginElements;
import Utilities.DataProviderUtil;
import io.appium.java_client.android.AndroidDriver;
import Utilities.*;

@Listeners(TestListener.class)
public class LoginTest extends Base {
	
	
	
	
	@Test(retryAnalyzer = RetryAnalyzer.class,   description = "TC_LOGIN_001 - Verify user login",
			dataProvider = "userDatails", dataProviderClass = DataProviderUtil.class)
	public void Steps(String number, String pass) throws InterruptedException, IOException
	{  

		
	    LoginElements le = new LoginElements(driver);

	    le.testexecution( number, pass);
	    
		le.logoutSteps();
		
	}
	
	
	@DataProvider(name="LoginData")
	public Object[][] userData()
	{
		return new Object[][] {
			{"8698294937"},
			{"8779906355"},
			{"9594262573"}
		};
	}

}
