package Testcases;

import org.testng.annotations.Test;

import com.titan.eyestage.Base;

import POM.LoginElements;
import POM.TrackOrderElements;

public class TrackOrderTest extends Base {

	
	@Test
	public void Steps() throws InterruptedException
	{
		LoginElements le = new LoginElements(driver);
		le.testexecution();
		
		TrackOrderElements OE = new TrackOrderElements(driver);
		OE.trackOrderStepsWithNumber();
	}
	
	
	
}
