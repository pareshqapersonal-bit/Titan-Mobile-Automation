package com.titan.eyestage.v2.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.titan.eyestage.v2.Base;

public class TestListener extends Base implements ITestListener {

	@Override
	public void onTestStart(ITestResult result) {

	    String testName = result.getMethod().getDescription();

	    if (testName == null || testName.isEmpty()) {
	        testName = result.getMethod().getMethodName();
	    }

	    setTest(extent.createTest(
	            testName + CommonUtils.getTestData(result)
	    ));

	    System.out.println(result.getName() + " Started");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		test().pass("Passed");
		ITestListener.super.onTestSuccess(result);
		System.out.println(
                result.getName() +
                " Passed");
	}

	@Override
	public void onTestFailure(ITestResult result) {

	    test().fail(result.getThrowable());

	    try {

	        if (driver() != null && driver().getSessionId() != null) {

	            String screenshotPath =
	                    captureScreenshot(result.getName());

	            test().info(
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
		test().skip("Retry Attempt");
		ITestListener.super.onTestSkipped(result);
		  System.out.println(
	                result.getName() +
	                " Skipped");
	}

	@Override
	public synchronized void onFinish(ITestContext context) {

	    extent.flush();

	    System.out.println(
	            "Extent Report Generated (v2 parallel flow)");
	}
}
