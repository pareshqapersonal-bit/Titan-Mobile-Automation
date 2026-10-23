package com.titan.eyestage.v2;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.pom.CartPageElementsNew;
import com.titan.eyestage.v2.pom.PaymentPageElementsNew;
import com.titan.eyestage.v2.pom.PurchaseJourneyElementsNew;
import com.titan.eyestage.v2.utils.PurchaseDataReader;
import com.titan.eyestage.v2.utils.RetryAnalyzer;
import com.titan.eyestage.v2.utils.TestListener;

// New-UI (Flutter) add-to-cart flow. Starts straight from SKU search - the local run uses
// noReset, so the user is expected to already be logged in on the device (run LoginTestNew
// first if not). Rows come from TestData/AddToCartNew.xlsx (copy of AddToCart.xlsx).
@Listeners(TestListener.class)
public class PurchaseTestNew extends Base {

	@Test(retryAnalyzer = RetryAnalyzer.class,
			description = "TC_ADD_TO_CART_NEW - Search SKU and add product to cart (new UI)",
			dataProvider = "addToCartCases")
	public void Steps(String testCaseId, List<CartProduct> products, String paymentMethod)
			throws InterruptedException, IOException {

		System.out.println("AddToCart TestCaseID = " + testCaseId + " | Products = " + products);

		test().info("TestCase=" + testCaseId + ", Products=" + products);

		CartPageElementsNew cart = new CartPageElementsNew(driver());

		cart.addProductsToCart(products);
		
		PurchaseJourneyElementsNew purchaseJourney = new PurchaseJourneyElementsNew(driver());
		purchaseJourney.proceedToCheckout();
		purchaseJourney.proceedToPay();
		
		PaymentPageElementsNew paymentPage = new PaymentPageElementsNew(driver());
		paymentPage.selectPaymentMethod(paymentMethod);
	}

	// {testCaseId, products, paymentMethod} - paymentMethod is unused here, it's only
	// carried so the sheet stays identical to AddToCart.xlsx.
	@DataProvider(name = "addToCartCases")
	public Object[][] addToCartCases() {
		return PurchaseDataReader.getPurchaseCases("AddToCartNew.xlsx");
	}
}
