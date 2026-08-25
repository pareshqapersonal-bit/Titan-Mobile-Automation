package com.titan.eyestage.v2;

import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.titan.eyestage.v2.pom.LoginElements;
import com.titan.eyestage.v2.utils.DataProviderUtil;
import com.titan.eyestage.v2.utils.RetryAnalyzer;
import com.titan.eyestage.v2.utils.TestListener;
import io.appium.java_client.android.AndroidDriver;

@Listeners(TestListener.class)
public class LoginTest extends Base {

	@Test(retryAnalyzer = RetryAnalyzer.class, description = "TC_LOGIN_001 - Verify user login",
			dataProvider = "loginDevices", dataProviderClass = DataProviderUtil.class)
	public void Steps(String number, String pass, String deviceName, String osVersion)
			throws InterruptedException, IOException {

		LoginElements le = new LoginElements(driver());

		le.testexecution(number, pass);

		le.logoutSteps();

	}

	@DataProvider(name = "LoginData")
	public Object[][] userData() {
		return new Object[][] {
			{ "8698294937" },
			{ "8779906355" },
			{ "9594262573" }
		};
	}

}
