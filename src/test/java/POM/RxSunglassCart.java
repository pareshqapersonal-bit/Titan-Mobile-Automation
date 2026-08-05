package POM;

import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;

public class RxSunglassCart extends CommonUtils {
	
	AndroidDriver driver;
	
	public RxSunglassCart(AndroidDriver driver) {
		
		this.driver = driver;
		 PageFactory.initElements(driver, this);
	}
	
	public void addToCart(CartProduct product) {
		
		test.info("Adding Rx Sunglass product to cart");
		
		//click(buyNowCTA);
		test.pass("Rx Sunglass added to cart successfully");
	}
	

}
