package com.titan.eyestage.v2.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    public static ExtentReports extent;

    public static synchronized ExtentReports getInstance() {

        if (extent == null) {

            ExtentSparkReporter spark =
                    new ExtentSparkReporter("Reports/ExtentReport_v2.html");

            // TestListener names each report entry "<description> | Device=..., OS=...,
            // TestCase=..., Products=..., PaymentMethod=..." (plus a retry-attempt suffix),
            // which is long enough on real data that the Spark theme's fixed-height sidebar rows
            // (div.test-item, whose title is p.name) let the wrapped text spill into the next
            // row instead of growing the row to fit. Forcing the row height to fit its wrapped
            // content fixes the overlap regardless of how long any individual name ends up.
            spark.config().setCss(
                    ".test-item { height: auto !important; min-height: 45px; }"
                            + " .test-item .name { white-space: normal !important;"
                            + " word-break: break-word !important; }");

            extent = new ExtentReports();
            extent.attachReporter(spark);
        }

        return extent;
    }
}
