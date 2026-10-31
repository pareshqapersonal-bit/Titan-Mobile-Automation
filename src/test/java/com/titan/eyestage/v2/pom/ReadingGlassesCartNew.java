package com.titan.eyestage.v2.pom;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

// Handles adding a Reading Glasses product to the cart, including the
// power selection step this category requires before Buy Now.
public class ReadingGlassesCartNew extends CommonUtils {

	AndroidDriver driver;
	

	public ReadingGlassesCartNew(AndroidDriver driver) {

		this.driver = driver;
		
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);


		
	}

	
	// Preferred power; used when it's in stock.
	private static final String PREFERRED_POWER = "+2.25";

	// LENS POWER chips. Out-of-stock powers are still listed but greyed out and report
	// clickable=false - tapping one (as happened with "+1" for RR21) silently does nothing,
	// and the following Add to cart is then ignored too, so the item never reaches the cart.
	private static final By SELECTABLE_POWERS =
			AppiumBy.xpath("//*[(starts-with(@content-desc,'+') or starts-with(@content-desc,'-')) and @clickable='true']");

	// Every chip, in stock or not; used only to tell when the chip row is on screen.
	private static final By ALL_POWERS =
			AppiumBy.xpath("//*[starts-with(@content-desc,'+') or starts-with(@content-desc,'-')]");

	@AndroidFindBy(accessibility = "Add to cart")
	WebElement addToCartButton;

	public void addToCart() {

		click(selectablePower());
		click(addToCartButton);

	}

	// Chips below the fold aren't in the hierarchy until they're on screen, and they can also sit
	// behind the sticky Add to cart bar (seen on a BrowserStack Galaxy S24), so swipe until the
	// lowest chip is clear of the bar.
	private void scrollPowersIntoView() {

		int maxBottom = (int) (driver.manage().window().getSize().height * 0.80);

		for (int attempt = 0; attempt < 6; attempt++) {
			if (isElementPresent(ALL_POWERS)) {
				int lowestBottom = 0;
				for (WebElement power : driver.findElements(ALL_POWERS)) {
					Rectangle r = power.getRect();
					lowestBottom = Math.max(lowestBottom, r.y + r.height);
				}
				if (lowestBottom < maxBottom) {
					return;
				}
			}
			smallSwipeUp();
		}
	}

	private void smallSwipeUp() {

		Dimension size = driver.manage().window().getSize();

		int x = size.width / 2;
		int startY = (int) (size.height * 0.65);
		int endY = (int) (size.height * 0.50);

		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence swipe = new Sequence(finger, 1);

		swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, startY));
		swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		swipe.addAction(finger.createPointerMove(Duration.ofMillis(250), PointerInput.Origin.viewport(), x, endY));
		swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

		driver.perform(List.of(swipe));
	}

	private WebElement selectablePower() {

		scrollPowersIntoView();

		List<WebElement> powers = driver.findElements(SELECTABLE_POWERS);

		if (powers.isEmpty()) {
			throw new RuntimeException("No selectable LENS POWER after scrolling - all powers out of stock, or the chips never appeared");
		}

		for (WebElement power : powers) {
			if (PREFERRED_POWER.equals(power.getAttribute("contentDescription"))) {
				return power;
			}
		}

		WebElement fallback = powers.get(0);
		String chosen = fallback.getAttribute("contentDescription");
		System.out.println("Power " + PREFERRED_POWER + " not selectable - using " + chosen);
		test().info("Reading Glass power " + PREFERRED_POWER + " unavailable - selected " + chosen);
		return fallback;
	}
}
