package POM;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import Utilities.CredentialManager;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;
import models.LoginCredentials;

/**
 * Handles adding a (non-powered) Sunglass product to the cart.
 */
public class SunglassCart extends  CommonUtils {

	AndroidDriver driver;
	private final String loginUser;
	   LoginElements le;
	public SunglassCart(AndroidDriver driver, String loginUser) {
		
		 this.driver = driver;
         this.loginUser = loginUser;
	        PageFactory.initElements(driver, this);

	        le = new LoginElements(driver);
	}
	
	
	 @FindBy(id="com.titan.eyecare:id/txt_btn_title")
	    WebElement buyNowCTA;

	public void addToCart(CartProduct product) {
		
		test.info("Adding Sunglass product to cart");

		   click(buyNowCTA);

		   if (le.isLoginPageDisplayed()) {

			    LoginCredentials credentials =
			            CredentialManager.getCredentials(loginUser);

			    le.shortLogin(credentials);

			    click(buyNowCTA);
			}

		test.pass("Sunglass added to cart successfully");
	}
}
