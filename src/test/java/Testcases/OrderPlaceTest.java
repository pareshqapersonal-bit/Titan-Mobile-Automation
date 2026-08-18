package Testcases;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.titan.eyestage.Base;

import POM.CartPageElements;
import POM.PaymentPageElements;
import POM.PurchaseJourneyElements;
import models.CartProduct;

@Listeners(Utilities.TestListener.class)
public class OrderPlaceTest extends Base {
	
	@Test(
		    description = "Complete Place Order Journey",
		    dataProvider = "purchaseData" , dataProviderClass = Utilities.DataProviderUtil.class
		)
		public void steps(List<CartProduct> products, String paymentMethod)
		        throws IOException, InterruptedException {

		    CartPageElements cart = new CartPageElements(driver,"");
		    PurchaseJourneyElements purchase =
		            new PurchaseJourneyElements(driver);
		    PaymentPageElements payment =
		            new PaymentPageElements(driver);

		    cart.addProductsToCart(products);

		    purchase.proceedToCheckout();

		    purchase.proceedToPay();

		    payment.selectPaymentMethod(paymentMethod);
		}

}
