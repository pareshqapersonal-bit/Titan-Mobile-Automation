package com.titan.eyestage.v2.utils;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

// TestNG resolves a single RetryAnalyzer instance per @Test method and reuses it for every row
// a parallel data provider produces (e.g. testng-parallel-purchase.xml runs with
// data-provider-thread-count="2"), so a plain int `count` field races across concurrent
// invocations and can let one row's retry consume another row's budget. Key the retry count by
// the row's own parameters instead, so each case (device + payment method, or device + login)
// gets its own independent retry budget no matter which thread runs it or how threads get
// reused across invocations.
public class RetryAnalyzer implements IRetryAnalyzer {

	private static final int maxRetryCount = 1;

	private static final ConcurrentHashMap<String, AtomicInteger> retryCounts = new ConcurrentHashMap<>();

	@Override
	public boolean retry(ITestResult result) {

		String key = result.getTestClass().getName()
				+ "#" + result.getMethod().getMethodName()
				+ Arrays.toString(result.getParameters());

		AtomicInteger attempts = retryCounts.computeIfAbsent(key, k -> new AtomicInteger(0));

		if (attempts.get() < maxRetryCount) {

			int current = attempts.incrementAndGet();

			System.out.println(
					"Retrying Test : "
							+ result.getName()
							+ " Retry Count : "
							+ current
			);

			return true;
		}

		return false;
	}

}
