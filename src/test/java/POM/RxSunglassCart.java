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
 * Handles adding a powered (prescription) Sunglass product to the cart.
 */
public class RxSunglassCart extends CommonUtils {
	
	AndroidDriver driver;
	   LoginElements le;
	   private final String loginUser;
	public RxSunglassCart(AndroidDriver driver,String loginUser) {
		
		this.driver = driver;
		this.loginUser= loginUser;
		 PageFactory.initElements(driver, this);
		 le = new LoginElements(driver);
	}
	
	 @FindBy(id="com.titan.eyecare:id/txt_btn_title")
	    WebElement buyNowCTA;
	 @FindBy(id="android:id/button1")
	    WebElement okSub;
	
	public void addToCart(CartProduct product) {
		
		test.info("Adding Rx Sunglass product to cart");
		 click(buyNowCTA);

	        if(le.isLoginPageDisplayed()) {

	        	LoginCredentials credentials =
	                    CredentialManager.getCredentials(loginUser);

	            le.shortLogin(credentials);

	            click(buyNowCTA);
	        }
	        
	        click(okSub);

		test.pass("Rx Sunglass added to cart successfully");
	}
	

}
