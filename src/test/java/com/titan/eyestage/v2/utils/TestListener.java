package com.titan.eyestage.v2.utils;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.titan.eyestage.v2.Base;

public class TestListener extends Base implements ITestListener {

	// Counts invocations per logical case (class#method#params) so a retried case gets a
	// distinct, labeled node in the report ("Retry Attempt 1") instead of two identically
	// named entries that are impossible to tell apart.
	private static final ConcurrentHashMap<String, AtomicInteger> attemptCounts = new ConcurrentHashMap<>();

	// Tracks which case's node currently sits in Base's ThreadLocal ExtentTest, so
	// onTestSkipped/onTestFailure can tell "this result already has its own node from
	// onTestStart" apart from "onTestStart never ran for this result". When a @BeforeMethod
	// (e.g. opn_app) fails, TestNG skips the @Test method invocation entirely and calls
	// onTestSkipped() straight away, WITHOUT ever calling onTestStart() first - so without this
	// check, test() would either be null (NPE, nothing logged) or still hold the previous,
	// already-finished row's node on this thread (the skip note silently lands on the wrong,
	// already-passed entry instead of getting its own). Comparing case keys tells the two
	// situations apart: a retry-pending skip has the SAME case key as the started attempt it
	// belongs to, while a config-failure skip never had a start for its own case key at all.
	private static final ThreadLocal<String> startedCaseKeyHolder = new ThreadLocal<>();

	private static String caseKey(ITestResult result) {
		return result.getTestClass().getName()
				+ "#" + result.getMethod().getMethodName()
				+ Arrays.toString(result.getParameters());
	}

	private ExtentTest createTestNode(ITestResult result) {

		String testName = result.getMethod().getDescription();

		if (testName == null || testName.isEmpty()) {
			testName = result.getMethod().getMethodName();
		}

		String key = caseKey(result);

		int attempt = attemptCounts
				.computeIfAbsent(key, k -> new AtomicInteger(0))
				.incrementAndGet();

		String attemptLabel = attempt > 1 ? " [Retry Attempt " + (attempt - 1) + "]" : "";

		ExtentTest node = extent.createTest(
				testName + CommonUtils.getTestData(result) + attemptLabel
		);

		setTest(node);
		startedCaseKeyHolder.set(key);

		return node;
	}

	// Returns this result's own node, creating one on the spot if onTestStart never ran for it
	// (the @BeforeMethod-failure case above) instead of reusing whatever's left over on this
	// thread from a different, already-finished case.
	private ExtentTest testFor(ITestResult result) {

		if (caseKey(result).equals(startedCaseKeyHolder.get())) {
			return test();
		}

		return createTestNode(result);
	}

	@Override
	public void onTestStart(ITestResult result) {

		createTestNode(result);

		System.out.println(result.getName() + " Started");
	}

	// BrowserStack session status and failure screenshots are handled in Base.tearDown() now,
	// not here - TestNG runs @AfterMethod before notifying ITestListener, so by the time these
	// callbacks fire the driver has already quit and its session id is gone. These callbacks
	// only build the Extent report entries, which don't need a live driver.
	@Override
	public void onTestSuccess(ITestResult result) {
		testFor(result).pass("Passed");
		ITestListener.super.onTestSuccess(result);
		System.out.println(
                result.getName() +
                " Passed");
	}

	@Override
	public void onTestFailure(ITestResult result) {

	    testFor(result).fail(result.getThrowable());

	    System.out.println(
                result.getName() + " Failed");

	    ITestListener.super.onTestFailure(result);
	}

	@Override
	public void onTestSkipped(ITestResult result) {

		ExtentTest node = testFor(result);

		// TestNG reclassifies a failed-but-about-to-be-retried invocation as SKIP rather than
		// FAIL, but the original exception stays attached to the result - without logging it
		// here, the actual reason for the retry is silently lost from the report (it used to
		// just say "Retry Attempt" with no detail). A genuine skip (no retry involved) has no
		// throwable, so it still falls back to a plain skip note.
		if (result.getThrowable() != null) {
			node.skip(result.getThrowable());
		} else {
			node.skip("Skipped");
		}

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
