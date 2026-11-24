package com.titan.eyestage.v2.pom;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.utils.CommonUtils;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class QuantityUpdatePage extends CommonUtils {

	private static final int MAX_REMOVE_ATTEMPTS = 20;
	


	
	AndroidDriver driver;
	public QuantityUpdatePage(AndroidDriver driver) {
	this.driver = driver;
		
		// AppiumFieldDecorator so @AndroidFindBy fields get resolved (plain PageFactory ignores them)
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}
	
	private static final By REMOVE_ICON_LOCATOR = AppiumBy.xpath(
			"//android.widget.ScrollView/android.view.View[@clickable=\"true\" and not(@content-desc)]"
			+ " | //android.widget.ScrollView/android.view.View[@content-desc=\"Remove\"]");
	
	@AndroidFindBy(accessibility = "Continue Shopping")
	WebElement continueShoppingButton;
	
	
	public void updateQuantity(String offer) {
		test().info("Updating quantity");
		if(offer.equals("BoGo")) {
			List<WebElement> removeIcons = driver.findElements(REMOVE_ICON_LOCATOR);

			for (int attempt = 1; !removeIcons.isEmpty(); attempt++) {

				if (attempt > MAX_REMOVE_ATTEMPTS) {
					throw new RuntimeException(
							"Cart still has items after " + MAX_REMOVE_ATTEMPTS + " remove attempts");
				}

				// Always the current first match - re-captured below on every iteration, so this
				// is never a reference to an item that's already been removed.
				removeIcons.get(0).click();
				
				dismissContinueShoppingPopupIfPresent();

				removeIcons = driver.findElements(REMOVE_ICON_LOCATOR);
			}
		}
	}
	
	private void dismissContinueShoppingPopupIfPresent() {

		List<WebElement> continueButtons = driver.findElements(
				AppiumBy.xpath(
						"//android.view.View[@content-desc=\"Continue Shopping\"]"));

		System.out.println("Continue Shopping popup present = " + !continueButtons.isEmpty());

		if (!continueButtons.isEmpty()) {
			continueShoppingButton.click();
		//	boolean clicked = clickIfPresent(continueShoppingButton, 5);
			System.out.println("Continue Shopping popup dismiss clicked = " );
		}
	}

}
