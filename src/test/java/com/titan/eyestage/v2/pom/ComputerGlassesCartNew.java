package com.titan.eyestage.v2.pom;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.utils.CommonUtils;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

// Handles adding a Computer Glasses product to the cart.
public class ComputerGlassesCartNew extends CommonUtils {

	AndroidDriver driver;
	LoginElements le;
ApplyCopounOffersPage applyCopounOffersPage;
List<WebElement> continueButtons;

	@FindBy(xpath = "//android.view.View[@content-desc=\"Continue Shopping\"]")
	WebElement continueShoppingButton;
	

	public ComputerGlassesCartNew(AndroidDriver driver) {

		this.driver = driver;
		
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);

		
	}

	@AndroidFindBy(accessibility = "Add to cart")
	WebElement addToCartButton;

	public void addToCart(String couponCode) {

		click(addToCartButton);

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
		
	}
}
