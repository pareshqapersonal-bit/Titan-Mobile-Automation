package Testcases;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.titan.eyestage.Base;

import POM.CartPageElements;
import POM.PaymentPageElements;
import POM.PurchaseJourneyElements;
import Utilities.CartDataMapper;
import Utilities.DataProviderUtil;
import Utilities.ExcelReader;
import Utilities.TestListener;
import models.CartProduct;

@Listeners(TestListener.class)
public class AddToCartJourney extends Base {
	
	@Test(
		    description = "Complete Place Order Journey",
		    priority = 1,
		    dataProvider = "purchaseData",
		    dataProviderClass = DataProviderUtil.class
		)
		public void steps(
		        String testCaseId,
		        List<CartProduct> products,
		        String paymentMethod,
		        String loginUser)
		        throws IOException, InterruptedException {

		    System.out.println("\n================================");
		    System.out.println("STARTING TEST CASE = " + testCaseId);
		    System.out.println("Payment Method = " + paymentMethod);
		    System.out.println("Login User = " + loginUser);
		    System.out.println("================================");

		    CartPageElements cart =
		            new CartPageElements(driver, loginUser);

		    PurchaseJourneyElements purchase =
		            new PurchaseJourneyElements(driver);

		    PaymentPageElements payment =
		            new PaymentPageElements(driver);

		    cart.addProductsToCart(products);

		    purchase.proceedToCheckout();

		    System.out.println("Proceeding to checkout...");

		    purchase.proceedToPay();

		    payment.selectPaymentMethod(paymentMethod);
		}
	
	
	
	

	/*
	 * @Test( description = "Complete Place Order Journey", priority = 1,
	 * dataProvider = "purchaseData", dataProviderClass = DataProviderUtil.class )
	 * public void steps( String testCaseId, List<CartProduct> products, String
	 * paymentMethod, String loginUser) throws IOException, InterruptedException {
	 * 
	 * ExcelReader excel = new ExcelReader();
	 * 
	 * CartPageElements cart = new CartPageElements(driver); PurchaseJourneyElements
	 * purchase = new PurchaseJourneyElements(driver); PaymentPageElements payment =
	 * new PaymentPageElements(driver);
	 * 
	 * int rowCount = excel.getRowCount("TestData");
	 * 
	 * for(int row=1; row<=rowCount; row++) {
	 * 
	 * 
	 * System.out.println("\n================================");
	 * System.out.println("STARTING EXCEL ROW = " + row);
	 * System.out.println("Payment Method = " +
	 * CartDataMapper.getPaymentMethod(row));
	 * System.out.println("================================");
	 * 
	 * 
	 * 
	 * List<CartProduct> products = CartDataMapper.getProducts(row); String
	 * paymentMethod = CartDataMapper.getPaymentMethod(row);
	 * 
	 * cart.addProductsToCart(products); purchase.proceedToCheckout();
	 * 
	 * System.out.println("Proceeding to checkout...");
	 * 
	 * purchase.proceedToPay();
	 * 
	 * payment.selectPaymentMethod(paymentMethod);
	 * 
	 * // payment.continueToPayment(); // // // purchase.razorPay();
	 * 
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * excel.closeWorkbook(); }
	 */
}