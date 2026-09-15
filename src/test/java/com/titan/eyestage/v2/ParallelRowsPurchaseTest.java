package com.titan.eyestage.v2;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.pom.CartPageElements;
import com.titan.eyestage.v2.pom.PaymentPageElements;
import com.titan.eyestage.v2.pom.PurchaseJourneyElements;
import com.titan.eyestage.v2.utils.DeviceReader;
import com.titan.eyestage.v2.utils.PurchaseDataReader;
import com.titan.eyestage.v2.utils.RetryAnalyzer;
import com.titan.eyestage.v2.utils.TestListener;

// Sibling to PurchaseTest: same single-device journey as the old singular flow (one fixed
// device/login, taken as the first Execute=Y row of TestData/DeviceConfig.xlsx), but splits
// the payment-method rows from TestData/PurchaseData-v2.xlsx across threads instead of
// running them one at a time. Kept as its own class/suite rather than folded into the
// existing singular flow so that flow's behavior stays untouched.
//
// Safe to parallelize here only because Base (com.titan.eyestage.v2.Base) already keeps
// driver/test/session-id in ThreadLocal storage - the same reason PurchaseTest's per-device
// instances can run concurrently. The old singular flow's Base (com.titan.eyestage.Base)
// still uses plain static fields, so it can't take this same change without that rewrite.
@Listeners(TestListener.class)
public class ParallelRowsPurchaseTest extends Base {

	// Fixed target device for this single-device flow - Samsung Galaxy S24 to match the
	// singular flow's own default (config-browserstack.properties' browserstack.device),
	// regardless of row order in DeviceConfig.xlsx.
	private static final String TARGET_DEVICE = "Samsung Galaxy S24";

	// parallel = true hands out these rows across data-provider-thread-count threads (set in
	// testng-parallel-purchase-rows.xml) instead of running them one at a time on a single
	// thread. Row shape matches PurchaseTest.purchaseCasesForDevice exactly, so TestListener
	// needs no changes to report against this class too.
	@DataProvider(name = "purchaseCasesParallel", parallel = true)
	public Object[][] purchaseCasesParallel() {

		Object[] device = null;

		for (Object[] candidate : DeviceReader.getDevices()) {
			if (TARGET_DEVICE.equalsIgnoreCase(String.valueOf(candidate[2]))) {
				device = candidate;
				break;
			}
		}

		if (device == null) {
			throw new RuntimeException(
					"No Execute=Y row for '" + TARGET_DEVICE + "' found in TestData/DeviceConfig.xlsx");
		}

		String deviceName = (String) device[2];
		String osVersion = (String) device[3];

		// Multiple rows for the same device end up on the same thread pool concurrently
		// (data-provider-thread-count in testng-parallel-purchase-rows.xml), which used to mean
		// every thread logged into the same MobileNumber/Password at once and stepped on each
		// other's session. Cycling through DeviceReader's credential pool (MobileNumber/Password
		// plus MobileNumber2/Password2 from DeviceConfig.xlsx) by row index instead gives
		// consecutive rows - and therefore concurrently running threads - distinct logins.
		List<String[]> credentials = DeviceReader.getCredentialPool(deviceName);

		Object[][] purchaseCases = PurchaseDataReader.getPurchaseCases();

		Object[][] rows = new Object[purchaseCases.length][7];

		for (int i = 0; i < purchaseCases.length; i++) {

			Object[] purchaseCase = purchaseCases[i];
			String[] credential = credentials.get(i % credentials.size());

			rows[i] = new Object[] {
					credential[0], credential[1], deviceName, osVersion,
					purchaseCase[0], purchaseCase[1], purchaseCase[2]
			};
		}

		return rows;
	}

	@Test(retryAnalyzer = RetryAnalyzer.class,
			description = "TC_PURCHASE_JOURNEY - Add all category products to cart and complete purchase",
			dataProvider = "purchaseCasesParallel")
	public void Steps(
			String mobileNumber,
			String password,
			String deviceName,
			String osVersion,
			String testCaseId,
			List<CartProduct> products,
			String paymentMethod)
			throws InterruptedException, IOException {

		System.out.println("Purchase TestCaseID = " + testCaseId
				+ " | Payment Method = " + paymentMethod
				+ " | Device = " + deviceName + " (" + osVersion + ")");

		test().info("TestCase=" + testCaseId + ", PaymentMethod=" + paymentMethod
				+ ", Products=" + products);

		CartPageElements cart = new CartPageElements(driver(), mobileNumber, password);
		PurchaseJourneyElements purchase = new PurchaseJourneyElements(driver());
		PaymentPageElements payment = new PaymentPageElements(driver());

		cart.addProductsToCart(products);

		purchase.proceedToCheckout();
		purchase.proceedToPay();

		payment.selectPaymentMethod(paymentMethod);
	}
}
