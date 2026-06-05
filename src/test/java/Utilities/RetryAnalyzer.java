package Utilities;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
   private int count=0;
   private static final int maxRetryCount = 2;
	@Override
	public boolean retry(ITestResult result) {
		// TODO Auto-generated method stub
		
		if (count < maxRetryCount) {
	        count++;

	        System.out.println(
	            "Retrying Test : "
	            + result.getName()
	            + " Retry Count : "
	            + count
	        );

	        return true;
	    }

		return false;
	}
	

	
}
