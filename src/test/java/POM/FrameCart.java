package POM;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;

public class FrameCart extends CommonUtils {

    AndroidDriver driver;

    LoginElements le;

    public FrameCart(AndroidDriver driver) {

        this.driver = driver;

        PageFactory.initElements(driver, this);

        le = new LoginElements(driver);
    }

    @FindBy(id="com.titan.eyecare:id/txt_btn_title")
    WebElement buyNowCTA;

    @FindBy(id="com.titan.eyecare:id/txt_lens_add_dialog_buy")
    WebElement onlyFrameCTA;

    public void addToCart(CartProduct product) {

        click(buyNowCTA);

        if(le.isLoginPageDisplayed()) {

            le.shortLogin("8586565656","254265");

            click(buyNowCTA);
        }

        click(onlyFrameCTA);
    }
}