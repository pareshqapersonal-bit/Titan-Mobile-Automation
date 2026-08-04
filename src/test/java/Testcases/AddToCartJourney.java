package Testcases;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.titan.eyestage.Base;

import POM.CartPageElements;
import POM.PurchaseJourneyElements;
import Utilities.CartDataMapper;
import Utilities.ExcelReader;
import Utilities.TestListener;
import models.CartProduct;

@Listeners(TestListener.class)
public class AddToCartJourney extends Base {

    @Test
    public void steps() throws IOException {

        ExcelReader excel = new ExcelReader();

        CartPageElements cart = new CartPageElements(driver);
        PurchaseJourneyElements purchase = new PurchaseJourneyElements(driver);

        int rowCount = excel.getRowCount("TestData");

        System.out.println("Total Test Cases : " + rowCount);

        for (int row = 1; row <= rowCount; row++) {

            System.out.println("==================================");
            System.out.println("Executing Test Case : " + row);
            System.out.println("==================================");

            List<CartProduct> products = CartDataMapper.getProducts(row);

            // Add all products for this test case
            cart.addProductsToCart(products);

            // Complete purchase
            purchase.proceedToCheckout();
            purchase.proceedToPay();
            purchase.payment();

            System.out.println("Test Case Completed");
        }

        excel.closeWorkbook();
    }
}