package com.titan.eyestage.v2.utils;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.titan.eyestage.v2.Base;

public class TestListener extends Base implements ITestListener {

	// Counts invocations per logical case (class#method#params) so a retried case gets a
	// distinct, labeled node in the report ("Retry Attempt 1") instead of two identically
	// named entries that are impossible to tell apart.
	private static final ConcurrentHashMap<String, AtomicInteger> attemptCounts = new ConcurrentHashMap<>();

	private static String caseKey(ITestResult result) {
		return result.getTestClass().getName()
				+ "#" + result.getMethod().getMethodName()
				+ Arrays.toString(result.getParameters());
	}

	@Override
	public void onTestStart(ITestResult result) {

		String testName = result.getMethod().getDescription();

		if (testName == null || testName.isEmpty()) {
			testName = result.getMethod().getMethodName();
		}

		int attempt = attemptCounts
				.computeIfAbsent(caseKey(result), k -> new AtomicInteger(0))
				.incrementAndGet();

		String attemptLabel = attempt > 1 ? " [Retry Attempt " + (attempt - 1) + "]" : "";

		setTest(extent.createTest(
				testName + CommonUtils.getTestData(result) + attemptLabel
		));

		System.out.println(result.getName() + " Started" + attemptLabel);
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
	    attachFailureScreenshot(result);

	    System.out.println(
                result.getName() + " Failed");

	    ITestListener.super.onTestFailure(result);
	}

	@Override
	public void onTestSkipped(ITestResult result) {

		// TestNG reclassifies a failed-but-about-to-be-retried invocation as SKIP rather than
		// FAIL, but the original exception stays attached to the result - without logging it
		// here, the actual reason for the retry is silently lost from the report (it used to
		// just say "Retry Attempt" with no detail). A genuine skip (no retry involved) has no
		// throwable, so it still falls back to a plain skip note.
		if (result.getThrowable() != null) {
			test().skip(result.getThrowable());
			attachFailureScreenshot(result);
		} else {
			test().skip("Skipped");
		}

		ITestListener.super.onTestSkipped(result);
		System.out.println(
	                result.getName() +
	                " Skipped");
	}

	private void attachFailureScreenshot(ITestResult result) {

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
	}

	@Override
	public synchronized void onFinish(ITestContext context) {

	    extent.flush();

	    System.out.println(
	            "Extent Report Generated (v2 parallel flow)");
	}
}
