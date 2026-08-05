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

    FrameCart frameCart;
    EyeglassCart eyeglassCart;
    ContactLensCart contactLensCart;
    RxSunglassCart rxSunglassCart;
    SunglassCart sunglassCart;

    public CartPageElements(AndroidDriver driver) {

        this.driver = driver;

        PageFactory.initElements(driver, this);

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

    @FindBy(xpath="//android.widget.ImageView[@resource-id='com.titan.eyecare:id/img_back']")
    WebElement backButton;

    public void addProductsToCart(List<CartProduct> products) {

        for(int i=0;i<products.size();i++) {

            CartProduct product = products.get(i);

            searchProduct(product.getSku());

            dispatchProduct(product);

            if(i < products.size()-1) {
                backToSearch();
            }
        }
    }

    public void searchProduct(String sku) {

        click(searchClick);
        sendKeys(searchField, sku);
        click(productSelection);
    }

    private void dispatchProduct(CartProduct product) {

        switch(product.getCategory()) {

        case "Frame":
            frameCart.addToCart(product);
            break;

        case "Eyeglass":
            eyeglassCart.addToCart(product);
            break;

        case "ContactLens":
            contactLensCart.addToCart(product);
            break;

        case "PoweredSunglass":
            rxSunglassCart.addToCart(product);
            break;

        case "Sunglass":
            sunglassCart.addToCart(product);
            break;

        default:
            throw new RuntimeException("Unknown Category : " + product.getCategory());
        }
    }

    public void backToSearch() {

        driver.navigate().back();
        driver.navigate().back();
        click(backButton);
    }
}