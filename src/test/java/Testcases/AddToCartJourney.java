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
import Utilities.ExcelReader;
import Utilities.TestListener;
import models.CartProduct;

@Listeners(TestListener.class)
public class AddToCartJourney extends Base {

    @Test(description = "Complete Place Order Journey", priority = 1)
    public void steps() throws IOException, InterruptedException {

        ExcelReader excel = new ExcelReader();

        CartPageElements cart = new CartPageElements(driver);
        PurchaseJourneyElements purchase = new PurchaseJourneyElements(driver);
        PaymentPageElements payment = new PaymentPageElements(driver);

        int rowCount = excel.getRowCount("TestData");

        for(int row=1; row<=rowCount; row++) {

            List<CartProduct> products = CartDataMapper.getProducts(row);
            String paymentMethod = CartDataMapper.getPaymentMethod(row);

            cart.addProductsToCart(products);
            purchase.proceedToCheckout();

            System.out.println("Proceeding to checkout...");

            purchase.proceedToPay();

            payment.selectPaymentMethod(paymentMethod);

            payment.continueToPayment();

            purchase.razorPay();

            
        }
        
       

        excel.closeWorkbook();
    }
}