package com.titan.eyestage.v2;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.pom.CartPageElements;
import com.titan.eyestage.v2.pom.PaymentPageElements;
import com.titan.eyestage.v2.pom.PurchaseJourneyElements;
import com.titan.eyestage.v2.utils.PurchaseDataProviderUtil;
import com.titan.eyestage.v2.utils.RetryAnalyzer;
import com.titan.eyestage.v2.utils.TestListener;

@Listeners(TestListener.class)
public class PurchaseTest extends Base {

	@Test(retryAnalyzer = RetryAnalyzer.class,
			description = "TC_PURCHASE_JOURNEY - Add all category products to cart and complete purchase",
			dataProvider = "purchaseDevices", dataProviderClass = PurchaseDataProviderUtil.class)
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

		CartPageElements cart = new CartPageElements(driver(), mobileNumber, password);
		PurchaseJourneyElements purchase = new PurchaseJourneyElements(driver());
		PaymentPageElements payment = new PaymentPageElements(driver());

		cart.addProductsToCart(products);

		purchase.proceedToCheckout();
		purchase.proceedToPay();

		payment.selectPaymentMethod(paymentMethod);
	}
}
