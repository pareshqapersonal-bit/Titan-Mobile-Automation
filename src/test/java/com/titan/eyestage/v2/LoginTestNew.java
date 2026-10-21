package com.titan.eyestage.v2;

import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.titan.eyestage.v2.pom.LoginElementsNew;
import com.titan.eyestage.v2.utils.RetryAnalyzer;
import com.titan.eyestage.v2.utils.TestListener;

@Listeners(TestListener.class)
public class LoginTestNew extends Base {

	@Test(retryAnalyzer = RetryAnalyzer.class, description = "TC_LOGIN_001 - Verify user login (OTP)",
			dataProvider = "userDetails")
	public void Steps(String number, String otp) throws InterruptedException, IOException {

		LoginElementsNew le = new LoginElementsNew(driver());

		le.testexecution(number, otp);

		le.logoutSteps();

	}

	@DataProvider(name = "userDetails")
	public Object[][] userData() {
		return new Object[][] {
			{ "8698294937", "254265" }
		};
	}

}
