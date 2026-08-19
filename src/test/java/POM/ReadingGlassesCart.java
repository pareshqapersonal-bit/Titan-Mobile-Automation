package POM;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import Utilities.CredentialManager;
import io.appium.java_client.android.AndroidDriver;
import models.LoginCredentials;

public class ReadingGlassesCart extends CommonUtils {

	AndroidDriver driver;
	private final String loginUser;
	LoginElements le;

	public ReadingGlassesCart(AndroidDriver driver, String loginUser) {

		this.driver = driver;
		this.loginUser = loginUser;
		PageFactory.initElements(driver, this);

		le = new LoginElements(driver);
	}

	//Elements
	@FindBy(xpath = "//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_lens_power\" and @text=\"+1.25\"]")
	WebElement Power;
	@FindBy(id = "com.titan.eyecare:id/txt_btn_title")
    WebElement buyNowCTA;

	public void addToCart() {
		click(Power);
		click(buyNowCTA);
		 if(le.isLoginPageDisplayed()) {

			 LoginCredentials credentials =
			            CredentialManager.getCredentials(loginUser);

			    le.shortLogin(credentials);
	            click(Power);
	            click(buyNowCTA);
	        }
	}
}
