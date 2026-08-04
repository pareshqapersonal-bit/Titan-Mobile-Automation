package POM;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;

public class FrameCart extends CommonUtils {

    private AndroidDriver driver;

    public FrameCart(AndroidDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // Frame Only CTA
    @FindBy(xpath = "//android.widget.TextView[@resource-id='com.titan.eyecare:id/txt_lens_add_dialog_buy']")
    WebElement onlyFrameCTA;

    /**
     * Adds Frame Only product to cart.
     */
    public void addToCart(CartProduct product) {

        test.info("Adding Frame Only product to cart");

        click(onlyFrameCTA);

        test.pass("Frame added to cart successfully");
    }
}