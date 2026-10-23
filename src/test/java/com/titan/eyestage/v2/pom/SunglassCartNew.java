package com.titan.eyestage.v2.pom;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

// Handles adding a (non-powered) Sunglass product to the cart.
public class SunglassCartNew extends CommonUtils {

	AndroidDriver driver;
	


	public SunglassCartNew(AndroidDriver driver) {

		this.driver = driver;
		
		// AppiumFieldDecorator so @AndroidFindBy fields get resolved (plain PageFactory ignores them)
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);

		
	}

	@AndroidFindBy(accessibility = "Add to cart")
	WebElement addToCartButton;


	public void addToCart(CartProduct product) {

		test().info("Adding Sunglass product to cart");

		click(addToCartButton);

		

		test().pass("Sunglass added to cart successfully");
	}
}
