package POM;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;

public class CartPageElements extends CommonUtils {

    AndroidDriver driver;

    LoginElements le;
    FrameCart frameCart;
    EyeglassCart eyeglassCart;
    ContactLensCart contactLensCart;
    RxSunglassCart rxSunglassCart;
    SunglassCart sunglassCart;
    public CartPageElements(AndroidDriver driver) {

        this.driver = driver;

        PageFactory.initElements(driver, this);

        le = new LoginElements(driver);
        frameCart = new FrameCart(driver);
        eyeglassCart = new EyeglassCart(driver);
        contactLensCart = new ContactLensCart(driver);
        rxSunglassCart = new RxSunglassCart(driver);
        sunglassCart = new SunglassCart(driver);
    }

    @FindBy(id="com.titan.eyecare:id/rl_toolbar_search")
    WebElement searchClick;

    @FindBy(id="com.titan.eyecare:id/edt_search")
    WebElement searchField;

    @FindBy(id="com.titan.eyecare:id/txt_product_name")
    WebElement productSelection;

    @FindBy(id="com.titan.eyecare:id/txt_btn_title")
    WebElement buyNowCTA;

    @FindBy(xpath="//android.widget.ImageView[@resource-id='com.titan.eyecare:id/img_back']")
    WebElement backButton;

    public void addProductsToCart(List<CartProduct> products) {

        System.out.println("Total Products = " + products.size());

        for (int i = 0; i < products.size(); i++) {

            System.out.println("Iteration = " + i);

            CartProduct product = products.get(i);

            System.out.println("Searching SKU = " + product.getSku());

            searchProduct(product.getSku());

            System.out.println("Search completed");

            dispatchProduct(product);

            System.out.println("Dispatch completed");

            buyNow();

            System.out.println("Buy Now completed");

            if (i < products.size() - 1) {
                backToSearch();
            }
        }
    }

    public void searchProduct(String sku) {

        click(searchClick);
        sendKeys(searchField, sku);

        click(productSelection);

		/*
		 * click(buyNowCTA);
		 * 
		 * if(le.isLoginPageDisplayed()) {
		 * 
		 * le.shortLogin("8586565656","254265");
		 * 
		 * click(buyNowCTA); }
		 */
    }

    private void dispatchProduct(CartProduct product) {

    	System.out.println("Category = " + product.getCategory());
    	System.out.println("SKU = " + product.getSku());
        switch(product.getCategory()) {

        case "Frame":
            frameCart.addToCart(product);
            break;

        case "Eyeglass":
            eyeglassCart.addToCart(product);
            break;
        case "ContactLens":
        	System.out.println("Adding Contact Lens product to cart");
            contactLensCart.addToCart(product);
            break;
        case "PoweredSunglass":
        	System.out.println("Adding RX sunglass product to cart");
			rxSunglassCart.addToCart(product);
			break;
        case "Sunglass":
			System.out.println("Adding RX sunglass product to cart");
			sunglassCart.addToCart();
		     break;
			

        default:
            throw new RuntimeException("Unknown Category : "+product.getCategory());
        }
    }

    public void backToSearch() {

        driver.navigate().back();
        driver.navigate().back();

        click(backButton);
    }
    
    public void buyNow() {

        click(buyNowCTA);

        if (le.isLoginPageDisplayed()) {

            le.shortLogin("8586565656", "254265");

            click(buyNowCTA);
        }
    }

}