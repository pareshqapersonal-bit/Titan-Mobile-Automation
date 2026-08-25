package com.titan.eyestage.v2.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
	private int count = 0;
	private static final int maxRetryCount = 1;

	@Override
	public boolean retry(ITestResult result) {

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
