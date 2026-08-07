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
    public void steps() throws IOException, InterruptedException {

        ExcelReader excel = new ExcelReader();

        CartPageElements cart = new CartPageElements(driver);
        PurchaseJourneyElements purchase = new PurchaseJourneyElements(driver);

        int rowCount = excel.getRowCount("TestData");

        for(int row=1; row<=rowCount; row++) {

            List<CartProduct> products = CartDataMapper.getProducts(row);

            cart.addProductsToCart(products);

            purchase.proceedToCheckout();
            purchase.proceedToPay();
            purchase.payment();
            purchase.razorPay();
        }

        excel.closeWorkbook();
    }
}