package POM;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;

public class RxSunglassCart extends CommonUtils {
	
	AndroidDriver driver;
	   LoginElements le;
	public RxSunglassCart(AndroidDriver driver) {
		
		this.driver = driver;
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

	            le.shortLogin("8586565656","254265");

	            click(buyNowCTA);
	        }
	        
	        click(okSub);

		
		//click(buyNowCTA);
		test.pass("Rx Sunglass added to cart successfully");
	}
	

}
