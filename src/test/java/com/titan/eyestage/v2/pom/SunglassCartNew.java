package com.titan.eyestage.v2.pom;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.AppiumBy;
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

		// Diagnostic: accessibility-id lookups scan the whole view hierarchy rather than
		// using an indexed id lookup, so if "Add to cart" isn't unique on screen (e.g. a
		// cross-sell carousel further down the PDP reusing the same label), resolving which
		// node is clickable gets far more expensive. Logging the match count here confirms
		// whether that's what's behind the slow click() below.
		System.out.println(
				"'Add to cart' accessibility-id match count on screen = "
						+ driver.findElements(AppiumBy.accessibilityId("Add to cart")).size());

		click(addToCartButton);

		

		test().pass("Sunglass added to cart successfully");
	}
}
