package POM;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import Utilities.CredentialManager;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;
import models.LoginCredentials;

public class FrameCart extends CommonUtils {

    AndroidDriver driver;
   private final String loginUser;
    LoginElements le;

    public FrameCart(AndroidDriver driver, String loginUser) {

        this.driver = driver;
          this.loginUser = loginUser;
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

        	LoginCredentials credentials =
                    CredentialManager.getCredentials(loginUser);

            le.shortLogin(credentials);

            click(buyNowCTA);
        }

        click(onlyFrameCTA);
    }
}