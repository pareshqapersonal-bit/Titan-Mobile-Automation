package com.titan.eyestage.v2.pom;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

// Handles adding a powered (prescription) Sunglass product to the cart.
public class RxSunglassCartNew extends CommonUtils {

	AndroidDriver driver;
	

	public RxSunglassCartNew(AndroidDriver driver) {

		this.driver = driver;
		
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		
	}


	@AndroidFindBy(accessibility = "Add to cart")
	WebElement addToCartButton;

	@FindBy(id = "android:id/button1")
	WebElement okSub;

	public void addToCart(CartProduct product) {

		test().info("Adding Rx Sunglass product to cart");
		click(addToCartButton);

		

		//click(okSub);

		test().pass("Rx Sunglass added to cart successfully");
	}
}
