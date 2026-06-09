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

	@Test(retryAnalyzer = RetryAnalyzer.class,   description = "TC_HomePage_001 - Verify all banner links",
			dataProvider = "Categories", dataProviderClass = DataProviderUtil.class)
	public void Steps(String category) throws IOException {
		HomePageElements hpe = new HomePageElements(driver);
		hpe.validateAllBanners(category);
	}
}
