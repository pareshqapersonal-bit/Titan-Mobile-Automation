package com.titan.eyestage.v2;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.pom.CartPageElementsNew;
import com.titan.eyestage.v2.pom.LoginElementsNew;
import com.titan.eyestage.v2.pom.OrderDetailsPage;
import com.titan.eyestage.v2.pom.PaymentPageElementsNew;
import com.titan.eyestage.v2.pom.PurchaseJourneyElementsNew;
import com.titan.eyestage.v2.utils.DeviceReader;
import com.titan.eyestage.v2.utils.PurchaseDataReader;
import com.titan.eyestage.v2.utils.RetryAnalyzer;
import com.titan.eyestage.v2.utils.TestListener;

// New-UI (Flutter) add-to-cart flow. Starts straight from SKU search - each @BeforeMethod
// launch now wipes app/login state (noReset=false), so this logs in on every invocation
// using the Execute=Y row from TestData/DeviceConfig.xlsx. DeviceConfig's Password column
// doubles as the OTP for this app's stage login (same fixed value either way). Rows come
// from TestData/AddToCartNew.xlsx (copy of AddToCart.xlsx).
@Listeners(TestListener.class)
public class PurchaseTestNew extends Base {

	@Test(retryAnalyzer = RetryAnalyzer.class,
			description = "TC_ADD_TO_CART_NEW - Search SKU and add product to cart (new UI)",
			dataProvider = "addToCartCases")
	public void Steps(String testCaseId, List<CartProduct> products, String paymentMethod,
			boolean cancelOrder) throws InterruptedException, IOException {

		System.out.println("AddToCart TestCaseID = " + testCaseId + " | Products = " + products
				+ " | CancelOrder = " + cancelOrder);

		test().info("TestCase=" + testCaseId + ", Products=" + products + ", CancelOrder=" + cancelOrder);

		Object[] device = DeviceReader.getDevices()[0];
		String mobileNumber = (String) device[0];
		String otp = (String) device[1];
		
		System.out.println("Using device: " + mobileNumber + " | OTP=" + otp);

		LoginElementsNew login = new LoginElementsNew(driver());
		login.testexecution(mobileNumber, otp);
		
	

		CartPageElementsNew cart = new CartPageElementsNew(driver());

		cart.addProductsToCart(products);
		
		PurchaseJourneyElementsNew purchaseJourney = new PurchaseJourneyElementsNew(driver());
		purchaseJourney.proceedToCheckout();
		purchaseJourney.proceedToPay();
		
		PaymentPageElementsNew paymentPage = new PaymentPageElementsNew(driver());
		paymentPage.selectPaymentMethod(paymentMethod);

		// Starts from the "Order Confirmed." screen selectPaymentMethod() ends on.
		if (cancelOrder) {
			test().info("CancelOrder=Yes - cancelling the order just placed");
			new OrderDetailsPage(driver()).cancelOrder();
			test().pass("Order cancelled");
		}
	}

	// {testCaseId, products, paymentMethod, cancelOrder} - cancelOrder comes from the
	// CancelOrder (Yes/No) column; missing column or blank cell = No.
	@DataProvider(name = "addToCartCases")
	public Object[][] addToCartCases() {
		return PurchaseDataReader.getPurchaseCasesWithCancel("AddToCartNew.xlsx");
	}
}
