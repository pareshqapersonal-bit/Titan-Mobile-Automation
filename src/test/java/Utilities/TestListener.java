package Utilities;

import java.io.IOException;
import java.util.Arrays;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.internal.TestResult;

import com.titan.eyestage.Base;

public class TestListener extends Base implements ITestListener {
	
	/*
	 * public void onTestStart(ITestResult result) { String testName =
	 * result.getMethod().getDescription();
	 * 
	 * if (testName == null || testName.isEmpty()) { testName =
	 * result.getMethod().getMethodName(); } test = extent.createTest(testName +
	 * CommonUtils.getTestData(result));
	 * System.out.println(result.getName()+"Started"); }
	 */
	
	@Override
	public void onTestStart(ITestResult result) {

	    String testName = result.getMethod().getDescription();

	    if (testName == null || testName.isEmpty()) {
	        testName = result.getMethod().getMethodName();
	    }

	    test = extent.createTest(
	            testName + CommonUtils.getTestData(result)
	    );

	    // Purchase Journey DataProvider
	    Object[] parameters = result.getParameters();

	    if (parameters.length == 4
	            && parameters[1] instanceof java.util.List<?>) {

	        String testCaseId =
	                String.valueOf(parameters[0]);

	        String paymentMethod =
	                String.valueOf(parameters[2]);

	        String loginUser =
	                String.valueOf(parameters[3]);

	        @SuppressWarnings("unchecked")
	        java.util.List<models.CartProduct> products =
	                (java.util.List<models.CartProduct>) parameters[1];

	        test.info("Test Case: " + testCaseId);
	        test.info("Payment Method: " + paymentMethod);
	        test.info("Login User: " + loginUser);

	        StringBuilder categories =
	                new StringBuilder("Categories: ");

	        for (models.CartProduct product : products) {

	            categories.append(product.getCategory())
	                      .append(" | ");
	        }

	        test.info(categories.toString());
	    }

	    System.out.println(result.getName() + " Started");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		test.pass("Passed");
		ITestListener.super.onTestSuccess(result);
		System.out.println(
                result.getName() +
                " Passed");
	}

	@Override
	public void onTestFailure(ITestResult result) {

	    test.fail(result.getThrowable());

	    try {

	        if (driver != null && driver.getSessionId() != null) {

	            String screenshotPath =
	                    captureScreenshot(result.getName());

	            test.info(
	                    "<a href='data:image/png;base64," + screenshotPath
	                            + "' data-featherlight='image'><img src='data:image/png;base64,"
	                            + screenshotPath
	                            + "' style='width:200px;height:auto;cursor:pointer;'/></a>");

	            System.out.println(
	                    "Failure screenshot captured: " + screenshotPath);

	        } else {

	            System.out.println(
	                    "Screenshot skipped: Appium session is not available.");

	        }

	    } catch (Exception e) {

	        System.out.println(
	                "Could not capture failure screenshot: "
	                + e.getMessage());
	    }

	    System.out.println(
	            result.getName() + " Failed");

	    ITestListener.super.onTestFailure(result);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		// TODO Auto-generated method stub
		test.skip("Retry Attempt");
		ITestListener.super.onTestSkipped(result);
		  System.out.println(
	                result.getName() +
	                " Skipped");
	}

	@Override
	public void onFinish(ITestContext context) {

	    extent.flush();

	    System.out.println(
	            "Extent Report Generated");
	}
	}
	
	
	


