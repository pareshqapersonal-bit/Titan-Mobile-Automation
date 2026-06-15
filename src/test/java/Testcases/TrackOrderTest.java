package Testcases;


import java.io.IOException;

import org.testng.annotations.Listeners;

import org.testng.annotations.Test;

import com.titan.eyestage.Base;

import POM.LoginElements;
import POM.TrackOrderElements;
import Utilities.DataProviderUtil;
import Utilities.*;

@Listeners(TestListener.class)
public class TrackOrderTest extends Base {

	@Test(retryAnalyzer = RetryAnalyzer.class,  description = "TC_TRACK_001 - Verify user can track order",
			dataProvider = "userDatails", dataProviderClass = DataProviderUtil.class)
	public void Steps(String user,String pass) throws InterruptedException, IOException
	{
//		LoginElements le = new LoginElements(driver);
//		le.testexecution(user, pass);
		
		TrackOrderElements OE = new TrackOrderElements(driver);
		OE.trackOrderStepsWithNumber();
	}
	
	
	
}
