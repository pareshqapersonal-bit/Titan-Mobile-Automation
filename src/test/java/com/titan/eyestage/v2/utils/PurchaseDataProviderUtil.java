package com.titan.eyestage.v2.utils;

import org.testng.annotations.DataProvider;

// Pairs each purchase case (one row per payment method in TestData/PurchaseData-v2.xlsx)
// with a device from the separate TestData/DeviceConfig.xlsx device/login matrix
// (round-robin), so every payment method always runs, spread across whatever devices are
// configured there. Row shape matches Base.opn_app's testData[2]/[3] device-targeting
// (deviceName/osVersion at index 2/3), same as loginDevices, so Base needs no changes.
public class PurchaseDataProviderUtil {

	@DataProvider(name = "purchaseDevices", parallel = true)
	public Object[][] purchaseDevices() {

		Object[][] devices = DeviceReader.getDevices();
		Object[][] purchaseCases = PurchaseDataReader.getPurchaseCases();

		Object[][] rows = new Object[purchaseCases.length][7];

		for (int i = 0; i < purchaseCases.length; i++) {

			Object[] device = devices[i % devices.length];
			Object[] purchaseCase = purchaseCases[i];

			rows[i] = new Object[] {
					device[0], device[1], device[2], device[3],
					purchaseCase[0], purchaseCase[1], purchaseCase[2]
			};
		}

		return rows;
	}
}
