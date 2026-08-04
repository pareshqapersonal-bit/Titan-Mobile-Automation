package POM;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;

public class CartPageElements extends CommonUtils {

    private AndroidDriver driver;

    private FrameCart frameCart;
    private EyeglassCart eyeglassCart;
    // private SunglassCart sunglassCart;
    // private ContactLensCart contactLensCart;

    public CartPageElements(AndroidDriver driver) {

        this.driver = driver;
        PageFactory.initElements(driver, this);

        frameCart = new FrameCart(driver);
        eyeglassCart = new EyeglassCart(driver);
        // sunglassCart = new SunglassCart(driver);
        // contactLensCart = new ContactLensCart(driver);
    }

    // Search

    @FindBy(id = "com.titan.eyecare:id/rl_toolbar_search")
    WebElement searchClick;

    @FindBy(id = "com.titan.eyecare:id/edt_search")
    WebElement searchField;

    @FindBy(id = "com.titan.eyecare:id/txt_product_name")
    WebElement productSelection;

    @FindBy(id = "com.titan.eyecare:id/txt_btn_title")
    WebElement buyNowCTA;

    /**
     * Search product by SKU.
     */
    public void searchProduct(String sku) {

        test.info("Searching SKU : " + sku);

        click(searchClick);
        sendKeys(searchField, sku);

        click(productSelection);

        click(buyNowCTA);

        test.info("Product opened successfully");
    }

    /**
     * Entry point.
     */
    public void addProductsToCart(List<CartProduct> products) {

        for (CartProduct product : products) {

            searchProduct(product.getSku());

            dispatchProduct(product);
        }
    }

    /**
     * Category Dispatcher.
     */
    private void dispatchProduct(CartProduct product) {

        switch (product.getCategory()) {

        case "Frame":

            frameCart.addToCart(product);
            break;

        case "Eyeglass":

            eyeglassCart.addToCart(product);
            break;

        /*
        case "Sunglass":

            sunglassCart.addToCart(product);
            break;

        case "ContactLens":

            contactLensCart.addToCart(product);
            break;
        */

        default:

            throw new RuntimeException(
                    "Unknown Category : " + product.getCategory());
        }
    }
}