package POM;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;

public class ContactLensCart extends 
CommonUtils {
	
	AndroidDriver driver;
	
	public ContactLensCart(AndroidDriver driver) {
		
		this.driver = driver;
		 PageFactory.initElements(driver, this);
}
	
	//elements for lens add dialog
	@FindBy(id="com.titan.eyecare:id/txt_pdp_right_eye_header")
	WebElement findFieldCTA;
	
	
	public void addToCart(CartProduct product) {
		
		test.info("Adding Contact Lens product to cart");
		
		driver.findElement(
			    AppiumBy.androidUIAutomator(
			        "new UiScrollable(new UiSelector().scrollable(true))" +
			        ".scrollIntoView(new UiSelector().text(\"Right Eye\"))"
			    )
			).click();
		
		selectRightEyeSPH("-0.75");
		test.pass("Contact Lens added to cart successfully");
	}
	
	
	public void selectRightEyeSPH(String value) {

	    driver.findElement(
	            AppiumBy.id("com.titan.eyecare:id/rl_sph_right"))
	            .click();

	    driver.findElement(
	            AppiumBy.androidUIAutomator(
	                    "new UiSelector().text(\"" + value + "\")"))
	            .click();
	}

}
