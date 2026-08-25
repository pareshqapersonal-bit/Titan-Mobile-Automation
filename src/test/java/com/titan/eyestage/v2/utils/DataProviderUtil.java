package com.titan.eyestage.v2.utils;

import org.testng.annotations.DataProvider;

public class DataProviderUtil {

	// One row per enabled device in TestData/DeviceConfig.xlsx:
	// {mobileNumber, password, deviceName, osVersion}. parallel = true lets
	// TestNG run one thread per device/login pair (see
	// data-provider-thread-count in testng-parallel.xml).
	@DataProvider(name = "loginDevices", parallel = true)
	public Object[][] loginDevices() {
		return DeviceReader.getDevices();
	}

}
