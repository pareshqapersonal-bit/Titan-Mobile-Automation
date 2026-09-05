package com.titan.eyestage.v2.utils;

import org.testng.annotations.DataProvider;

// Cross-products every purchase case (one row per payment method in
// TestData/PurchaseData-v2.xlsx) with every device from the separate
// TestData/DeviceConfig.xlsx device/login matrix, so each payment method runs on
// every configured device (paymentMethods x devices total rows). Row shape matches
// Base.opn_app's testData[2]/[3] device-targeting (deviceName/osVersion at index 2/3),
// same as loginDevices, so Base needs no changes.
public class PurchaseDataProviderUtil {

	@DataProvider(name = "purchaseDevices", parallel = true)
	public Object[][] purchaseDevices() {

		Object[][] devices = DeviceReader.getDevices();
		Object[][] purchaseCases = PurchaseDataReader.getPurchaseCases();

		Object[][] rows = new Object[purchaseCases.length * devices.length][7];

		int rowIndex = 0;

		for (Object[] purchaseCase : purchaseCases) {
			for (Object[] device : devices) {

				rows[rowIndex++] = new Object[] {
						device[0], device[1], device[2], device[3],
						purchaseCase[0], purchaseCase[1], purchaseCase[2]
				};
			}
		}

		return rows;
	}
}
