package com.titan.eyestage.v2;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
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

@Listeners(TestListener.class)
public class PurchaseTest extends Base {

	// One PurchaseTest instance per device (created below by createInstances()), so each
	// device's payment-method rows belong to their own instance instead of all devices
	// sharing a single instance across parallel threads. TestNG's @BeforeMethod
	// failure-tracking is scoped per instance - under the old shared-instance design
	// (one Steps(...) method fed by a data provider cross-joining every payment method with
	// every device, data-provider-thread-count="2"), one device hitting a launch failure
	// caused TestNG to skip every other queued row for BOTH devices, including rows for the
	// other, perfectly healthy device that just hadn't been picked up by a worker thread
	// yet. Per-instance isolation confines a launch failure to its own device's remaining
	// payment methods.
	private final String mobileNumber;
	private final String password;
	private final String deviceName;
	private final String osVersion;

	private PurchaseTest(String mobileNumber, String password, String deviceName, String osVersion) {
		this.mobileNumber = mobileNumber;
		this.password = password;
		this.deviceName = deviceName;
		this.osVersion = osVersion;
	}

	// Runs with testng-parallel-purchase.xml's parallel="instances" so each device created
	// here executes on its own thread, isolated from the others.
	@Factory
	public static Object[] createInstances() {

		Object[][] devices = DeviceReader.getDevices();
		PurchaseTest[] instances = new PurchaseTest[devices.length];

		for (int i = 0; i < devices.length; i++) {

			Object[] device = devices[i];

			instances[i] = new PurchaseTest(
					(String) device[0],
					(String) device[1],
					(String) device[2],
					(String) device[3]);
		}

		return instances;
	}

	// This instance's own device paired with every payment-method row from
	// PurchaseData-v2.xlsx - unlike the old cross-joined provider, every row this produces
	// targets the same device, since that's now fixed per instance.
	@DataProvider(name = "purchaseCasesForDevice")
	public Object[][] purchaseCasesForDevice() {

		Object[][] purchaseCases = PurchaseDataReader.getPurchaseCases();
		Object[][] rows = new Object[purchaseCases.length][7];

		for (int i = 0; i < purchaseCases.length; i++) {

			Object[] purchaseCase = purchaseCases[i];

			rows[i] = new Object[] {
					mobileNumber, password, deviceName, osVersion,
					purchaseCase[0], purchaseCase[1], purchaseCase[2]
			};
		}

		return rows;
	}

	@Test(retryAnalyzer = RetryAnalyzer.class,
			description = "TC_PURCHASE_JOURNEY - Add all category products to cart and complete purchase",
			dataProvider = "purchaseCasesForDevice")
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

		// Full per-category/SKU breakdown as a report step, not in the ExtentTest node name -
		// the name only carries the item count (see CommonUtils.getTestData) to keep the
		// report's test list readable.
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
