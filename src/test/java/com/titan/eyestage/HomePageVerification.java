package com.titan.eyestage;

import java.io.IOException;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import POM.HomePageElements;
import Utilities.DataProviderUtil;
import Utilities.RetryAnalyzer;
import Utilities.TestListener;


@Listeners(TestListener.class)
public class HomePageVerification extends Base {

	@Test(priority = 1, retryAnalyzer = RetryAnalyzer.class,   description = "TC_HomePage_001 - Verify all banner links",
			dataProvider = "Categories", dataProviderClass = DataProviderUtil.class)
	public void Steps(String category) throws IOException {
		HomePageElements hpe = new HomePageElements(driver);
		hpe.validateAllBanners(category);
	}
	
	@Test(priority = 2, retryAnalyzer = RetryAnalyzer.class,   description = "TC_HomePage_002 - Verify VTO buttons")
	public void steps() throws IOException, InterruptedException
	{
		HomePageElements hpe = new HomePageElements(driver);
		hpe.clickVTO();
	}
	
	@Test(priority=3, retryAnalyzer = RetryAnalyzer.class, description = "TC_HomePage_003 - Verify Find My Fit section", 
			dataProvider = "findMYFit", dataProviderClass = DataProviderUtil.class)
	public void stepsthirdcase(String categorytext,String lensText,String bridgeText,String templeText) throws IOException
	{
		HomePageElements he= new HomePageElements(driver);
		he.stepsofFineMyFit(categorytext,lensText,bridgeText,templeText);
	}
	
	@Test(priority =4,retryAnalyzer = RetryAnalyzer.class, description = "TC_HomePage_004 - Verify 'Have Trouble Deciding' section")
	public void stepsfour() throws IOException
	{
		HomePageElements hp = new HomePageElements(driver);
		hp.haveTrobuleSection();
	}
	
	@Test(priority = 5, retryAnalyzer = RetryAnalyzer.class, description = "TC_HomePage_005 - Verify the Contact section")
	public void verifyContact() throws IOException
	{
		HomePageElements he= new HomePageElements(driver);
		he.contactUS();
		
	}
	
	
}
