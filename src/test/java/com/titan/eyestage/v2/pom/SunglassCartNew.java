package com.titan.eyestage.v2.pom;

import java.util.List;

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
	
	List<WebElement> continueButtons;
	ApplyCopounOffersPage applyCopounOffersPage;
	
	@FindBy(xpath = "//android.view.View[@content-desc=\"Continue Shopping\"]")
	WebElement continueShoppingButton;
	
	
	public SunglassCartNew(AndroidDriver driver) {

		this.driver = driver;
		
		// AppiumFieldDecorator so @AndroidFindBy fields get resolved (plain PageFactory ignores them)
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);

		
	}

	@AndroidFindBy(accessibility = "Add to cart")
	WebElement addToCartButton;


	public void addToCart(CartProduct product, String couponCode,String offer) {

		test().info("Adding Sunglass product to cart");
System.out.println("coupon is " + couponCode);
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
		
		 continueButtons = driver.findElements(
					AppiumBy.xpath(
							"//android.view.View[@content-desc=\"Continue Shopping\"]"));

			System.out.println("Continue Shopping button count = " + continueButtons.size());

			if (!continueButtons.isEmpty()) {

				System.out.println("Continue Shopping button found");

				visibilityOf(continueShoppingButton);

				System.out.println(
						"Continue Shopping CTA is visible and text is: "
								+ continueShoppingButton.getText());

				continueShoppingButton.click();

				System.out.println("Clicked Continue Shopping");
			}
			//String couponCode = "Flat65";
			
			applyCopounOffersPage = new ApplyCopounOffersPage(driver);
			if (couponCode != null && !couponCode.isEmpty()) {
				applyCopounOffersPage.applyCoupon(couponCode);
			}
			
			if (offer != null && !offer.isEmpty()) {
				applyCopounOffersPage.applyOffer(offer);
			}

	}
	
	
}
