package com.titan.eyestage.v2.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    public static ExtentReports extent;

    public static synchronized ExtentReports getInstance() {

        if (extent == null) {

            ExtentSparkReporter spark =
                    new ExtentSparkReporter("Reports/ExtentReport_v2.html");

            extent = new ExtentReports();
            extent.attachReporter(spark);
        }

        return extent;
    }
}
