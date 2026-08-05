package POM;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;

public class EyeglassCart extends CommonUtils {
  AndroidDriver driver;
	public EyeglassCart(AndroidDriver driver) {
		
		this.driver = driver;
		 PageFactory.initElements(driver, this);
	}
	
	
	//elements for lens add dialog
	@FindBy(xpath="//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_btn_title\"]")
	WebElement lensAddCTA;
	@FindBy(id="com.titan.eyecare:id/txt_btn_title")
	WebElement buyLensCTA;
	@FindBy(xpath="//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_skip\"]")
	WebElement skipCTA;

	@FindBy(xpath="//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_skip\"]")
	WebElement buyNowCTA;
	
	 /**
     * Adds Frame Only product to cart.
     */
    public void addToCart(CartProduct product) {

        test.info("Adding Frame Only product to cart");

        click(lensAddCTA);
        click(buyLensCTA);
        //click(skipCTA);
        click(buyNowCTA);
        test.pass("Frame added to cart successfully");
    }
	
	}


