package com.titan.eyestage.v2.pom;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

// Handles adding a Frame-only product to the cart (no lens selection).
public class FrameCartNew extends CommonUtils {

	AndroidDriver driver;
	

	public FrameCartNew(AndroidDriver driver) {

		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);

		
	}


	@AndroidFindBy(accessibility = "Add to cart")
	WebElement addToCartButton;

	@FindBy(xpath = "//android.widget.ImageView[@content-desc=\"Frame Only\n"
			+ "Pick Your Favorite Frame Now And Add Lenses Later At Your Convenience.\"]")
	WebElement onlyFrameCTA;

	
	//
	public void addToCart(CartProduct product) {

		click(addToCartButton);

		

		click(onlyFrameCTA);
	}
}
