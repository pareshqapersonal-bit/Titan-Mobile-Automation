package com.titan.eyestage.v2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.pom.CartPageElements;
import com.titan.eyestage.v2.pom.PaymentPageElements;
import com.titan.eyestage.v2.pom.PurchaseJourneyElements;
import com.titan.eyestage.v2.utils.DeviceReader;
import com.titan.eyestage.v2.utils.PaymentMethodMapper.PaymentMethod;
import com.titan.eyestage.v2.utils.PurchaseDataReader;
import com.titan.eyestage.v2.utils.RetryAnalyzer;
import com.titan.eyestage.v2.utils.TestListener;

// Sibling to PurchaseTest: same single-device journey as the old singular flow (one fixed
// device, taken as the Execute=Y row of TestData/DeviceConfig.xlsx), but splits the
// payment-method rows from TestData/PurchaseData-v2.xlsx across the device's logins instead
// of running them all one at a time on one login. Kept as its own class/suite rather than
// folded into the existing singular flow so that flow's behavior stays untouched.
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

	// Wallet and Google Pay run on the first login; every other payment method runs on the
	// second. Cash on Delivery is deliberately kept off the Wallet login: the app blocks it
	// ("COD not available when credits are used") whenever a wallet redemption is still
	// applied, which a failed Wallet row can leave behind. The cart (and any wallet redemption on it) lives on the account server-side,
	// so two rows on the same login at once end up sharing one cart - seen when rows were
	// handed out by row index under a parallel data provider: the Wallet row and the 8-product
	// Google Pay row both landed on the first login concurrently and Wallet's cart picked up
	// Google Pay's products.
	private static final Set<PaymentMethod> FIRST_LOGIN_METHODS =
			EnumSet.of(PaymentMethod.WALLET, PaymentMethod.GOOGLE_PAY);

	// One instance per login (created below by createInstances()). Each instance runs its own
	// rows one after another, so a login is never used by two rows at the same time.
	private final String mobileNumber;
	private final String password;
	private final String deviceName;
	private final String osVersion;
	private final List<Object[]> purchaseCases;

	private ParallelRowsPurchaseTest(String mobileNumber, String password, String deviceName,
			String osVersion, List<Object[]> purchaseCases) {
		this.mobileNumber = mobileNumber;
		this.password = password;
		this.deviceName = deviceName;
		this.osVersion = osVersion;
		this.purchaseCases = purchaseCases;
	}

	// Runs with testng-parallel-purchase-rows.xml's parallel="instances" so the two logins
	// created here execute on their own threads, side by side.
	@Factory
	public static Object[] createInstances() {

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

		// MobileNumber/Password plus MobileNumber2/Password2 from DeviceConfig.xlsx. With only
		// one login filled in, both groups fall back to it and run one after another on a
		// single instance.
		List<String[]> credentials = DeviceReader.getCredentialPool(deviceName);

		List<List<Object[]>> casesPerLogin = new ArrayList<>();

		for (int i = 0; i < Math.min(credentials.size(), 2); i++) {
			casesPerLogin.add(new ArrayList<>());
		}

		for (Object[] purchaseCase : PurchaseDataReader.getPurchaseCases()) {

			PaymentMethod method = PaymentMethod.fromExcel((String) purchaseCase[2]);
			int login = FIRST_LOGIN_METHODS.contains(method) ? 0 : casesPerLogin.size() - 1;

			casesPerLogin.get(login).add(purchaseCase);
		}

		List<Object> instances = new ArrayList<>();

		for (int i = 0; i < casesPerLogin.size(); i++) {

			if (casesPerLogin.get(i).isEmpty()) {
				continue;
			}

			String[] credential = credentials.get(i);

			instances.add(new ParallelRowsPurchaseTest(
					credential[0], credential[1], deviceName, osVersion, casesPerLogin.get(i)));
		}

		return instances.toArray();
	}

	// This instance's own login paired with the payment-method rows assigned to it. Row shape
	// matches PurchaseTest.purchaseCasesForDevice exactly, so TestListener needs no changes to
	// report against this class too.
	@DataProvider(name = "purchaseCasesForLogin")
	public Object[][] purchaseCasesForLogin() {

		Object[][] rows = new Object[purchaseCases.size()][7];

		for (int i = 0; i < purchaseCases.size(); i++) {

			Object[] purchaseCase = purchaseCases.get(i);

			rows[i] = new Object[] {
					mobileNumber, password, deviceName, osVersion,
					purchaseCase[0], purchaseCase[1], purchaseCase[2]
			};
		}

		return rows;
	}

	@Test(retryAnalyzer = RetryAnalyzer.class,
			description = "TC_PURCHASE_JOURNEY - Add all category products to cart and complete purchase",
			dataProvider = "purchaseCasesForLogin")
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
