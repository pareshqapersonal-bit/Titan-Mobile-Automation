package POM;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import io.appium.java_client.android.AndroidDriver;

public class ReadingGlasssesCart extends CommonUtils {

	AndroidDriver driver;
	LoginElements le;

	public ReadingGlasssesCart(AndroidDriver driver) {

		this.driver = driver;
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

	            le.shortLogin("8586565656","254265");

	            click(buyNowCTA);
	        }
	}
}
