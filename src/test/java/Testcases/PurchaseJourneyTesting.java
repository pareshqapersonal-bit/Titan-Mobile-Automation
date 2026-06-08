package Testcases;

import java.io.IOException;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.titan.eyestage.Base;

import POM.LoginElements;
import POM.PurchaseJourneyElements;
import Utilities.DataProviderUtil;
import Utilities.TestListener;

@Listeners(TestListener.class)
public class PurchaseJourneyTesting extends Base {
	
	@Test(description = "TC_Purchase_Journey_001 - Verify Purchase journey flow", dataProvider = "userDatails",
			dataProviderClass = DataProviderUtil.class)
	public void Steps(String number, String pass) throws InterruptedException, IOException
	{  

		
	   LoginElements le = new LoginElements(driver);

	  //  le.testexecution( number, pass);
	    
	    PurchaseJourneyElements pj = new PurchaseJourneyElements(driver);
	    pj.searchSteps();
	    pj.cartFlow(number,pass);
		pj.proceedToCheckout();
		pj.proceedToPay();
		pj.payment();
		
		
	
	}

}
