package Utilities;

import java.io.IOException;
import java.util.Arrays;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.internal.TestResult;

import com.titan.eyestage.Base;

public class TestListener extends Base implements ITestListener {
	
	public void onTestStart(ITestResult result)
	{
		String testName = result.getMethod().getDescription();

		if (testName == null || testName.isEmpty()) {
		    testName = result.getMethod().getMethodName();
		}
		test = extent.createTest(testName + CommonUtils.getTestData(result));	
		System.out.println(result.getName()+"Started");
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
		String screenshotPath = null;
		try {
			screenshotPath = captureScreenshot(result.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		test.fail(result.getThrowable());
		test.addScreenCaptureFromPath(screenshotPath);
		ITestListener.super.onTestFailure(result);
		System.out.println(
                result.getName() +
                " Failed");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		// TODO Auto-generated method stub
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
	
	
	


