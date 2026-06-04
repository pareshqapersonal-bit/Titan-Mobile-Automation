package Utilities;

import java.util.Arrays;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.internal.TestResult;

import com.titan.eyestage.Base;

public class TestListener extends Base implements ITestListener {
	
	public void onTestStart(ITestResult result)
	{
		test = extent.createTest(result.getName()+" "+Arrays.toString(result.getParameters()));
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
		test.fail(result.getThrowable());
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
	
	
	


