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

public class ApplyCopounOffersPage extends CommonUtils {

	AndroidDriver driver;

	// Coupon input box: the first EditText after the "PRODUCT OFFERS" section header
	@AndroidFindBy(xpath = "//android.view.View[@content-desc=\"PRODUCT OFFERS\"]/following::android.widget.EditText[1]")
	WebElement couponCodeInput;
	
	@AndroidFindBy(accessibility = "Apply")
	WebElement applyButton;


	public ApplyCopounOffersPage(AndroidDriver driver) {
		this.driver = driver;

		// AppiumFieldDecorator so @AndroidFindBy fields get resolved (plain PageFactory ignores them)
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	
	public void applyCoupon(String couponCode) {
		test().info("Applying coupon code: " + couponCode);
		// The coupon box sits below all the cart items. With several products in the cart it
		// is off screen, and the ScrollView only renders rows near the viewport, so the box
		// isn't in the hierarchy at all until it's scrolled to.
		scrollCouponInputIntoView();
		sendKeys(couponCodeInput, couponCode);
		click(applyButton);

		// A valid coupon opens a "Congratulations! Coupon: ... APPLIED!" bottom sheet with a
		// "Continue Shopping" button that covers the cart - anything tapped next (e.g. + for
		// BoGo) lands on the overlay instead. An invalid coupon shows an inline error and no
		// sheet, so only wait a few seconds for it.
		dismissCouponAppliedPopup(5000);
	}

	private static final By COUPON_INPUT_LOCATOR = AppiumBy.xpath(
			"//android.view.View[@content-desc=\"PRODUCT OFFERS\"]/following::android.widget.EditText[1]");

	// Scrolls until the "PRODUCT OFFERS" header is on screen, then nudges the list up while the
	// coupon box is missing or still in the bottom quarter, where the sticky "Proceed to
	// checkout" bar covers it (a tap on Apply there would land on the bar instead).
	private void scrollCouponInputIntoView() {
		scrollToLastCartItem();
		int screenHeight = driver.manage().window().getSize().getHeight();
		for (int i = 0; i < 3; i++) {
			List<WebElement> inputs = driver.findElements(COUPON_INPUT_LOCATOR);
			if (!inputs.isEmpty()) {
				org.openqa.selenium.Rectangle r = inputs.get(0).getRect();
				if (r.getY() + r.getHeight() < screenHeight * 0.75) {
					return;
				}
			}
			WebElement scrollView = driver.findElement(AppiumBy.className("android.widget.ScrollView"));
			driver.executeScript("mobile: scrollGesture", java.util.Map.of(
					"elementId", ((org.openqa.selenium.remote.RemoteWebElement) scrollView).getId(),
					"direction", "down",
					"percent", 0.4));
		}
	}

	private static final By CONTINUE_SHOPPING_LOCATOR =
			AppiumBy.xpath("//android.view.View[@content-desc=\"Continue Shopping\"]");

	// Polls for the coupon-applied sheet for up to timeoutMs and closes it via its
	// "Continue Shopping" button. Returns quietly if it never shows up.
	private void dismissCouponAppliedPopup(long timeoutMs) {
		long deadline = System.currentTimeMillis() + timeoutMs;
		do {
			List<WebElement> buttons = driver.findElements(CONTINUE_SHOPPING_LOCATOR);
			if (!buttons.isEmpty()) {
				buttons.get(0).click();
				System.out.println("Closed coupon-applied popup (Continue Shopping)");
				test().info("Closed coupon-applied popup");
				return;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		} while (System.currentTimeMillis() < deadline);
	}

	// Each cart item card is a clickable View directly under the ScrollView whose content-desc
	// starts with the quantity ("1\nFASTRACK\n..."). The per-item X (remove) icon is a sibling
	// of the card, not inside it, so it never matches here.
	// Other clickable, labelled Views also sit directly under the ScrollView (e.g. the
	// "View All Offers" banner), so the content-desc must also start with a digit - only
	// product cards begin with their quantity.
	private static final String ITEM_CARD_XPATH =
			"//android.widget.ScrollView/android.view.View[@clickable='true'"
			+ " and string-length(@content-desc) > 0"
			+ " and contains('0123456789', substring(@content-desc, 1, 1))]";

	// The + icon is the clickable View inside the card (no content-desc). At qty 1 the "-" is
	// disabled (not clickable), so + is the only match; once qty > 1 "-" becomes clickable
	// too, which is why this takes the last clickable child - + sits to the right of "-".
	// Confirmed against the live cart: one match per item, at the + icon's bounds.
	private static final By PLUS_ICON_LOCATOR = AppiumBy.xpath(
			ITEM_CARD_XPATH + "/android.view.View[@clickable='true'][last()]");

	private static final By ITEM_CARD_LOCATOR = AppiumBy.xpath(ITEM_CARD_XPATH);

	// Card content-desc is "<qty>\n<brand>\n<name>...", so the quantity is the first line.
	private int parseQuantity(WebElement card) {
		String desc = card.getAttribute("content-desc");
		String firstLine = desc.split("\n")[0].trim();
		try {
			return Integer.parseInt(firstLine);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Could not read quantity from cart card content-desc: \"" + desc + "\"", e);
		}
	}

	// A newly added product is placed at the bottom of the cart. The ScrollView only renders
	// rows near the viewport, so in a long cart the last card may not exist in the hierarchy
	// yet. "PRODUCT OFFERS" sits directly below the last card, so scrolling it into view
	// guarantees the last card (and its + icon) is rendered and on screen.
	private void scrollToLastCartItem() {
		driver.findElement(AppiumBy.androidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true))"
				+ ".scrollIntoView(new UiSelector().description(\"PRODUCT OFFERS\"))"));
	}

	// Toolbar header, e.g. content-desc "Cart\n(2 products)". It stays fixed at the top, so
	// unlike the cards (only rendered near the viewport) it always gives the real line count.
	private static final By CART_HEADER_LOCATOR = AppiumBy.xpath(
			"//android.view.View[starts-with(@content-desc,'Cart') and contains(@content-desc,'product')]");

	private int readCartProductCount() {
		List<WebElement> headers = driver.findElements(CART_HEADER_LOCATOR);
		if (headers.isEmpty()) {
			return -1;
		}
		java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)\\s*product")
				.matcher(headers.get(0).getAttribute("content-desc"));
		return m.find() ? Integer.parseInt(m.group(1)) : -1;
	}

	private int readLastItemQuantity() {
		List<WebElement> cards = driver.findElements(ITEM_CARD_LOCATOR);
		if (cards.isEmpty()) {
			throw new RuntimeException("No cart items found");
		}
		return parseQuantity(cards.get(cards.size() - 1));
	}

	/**
	 * Taps + once on the last product in the cart - the one just added, since new products
	 * go to the bottom - and waits until its quantity has gone up by 1.
	 */
	public void increaseQuantityOfLastItem() {
		// After a coupon is typed, the keyboard stays open with focus in the coupon box. The
		// next tap then only closes the keyboard and never reaches +, so the qty stays 1.
		// Close it first; hideKeyboard() throws when no keyboard is shown, which is fine.
		try {
			driver.hideKeyboard();
		} catch (Exception e) {
			// No keyboard open
		}
		// Safety net: if the coupon-applied sheet is still covering the cart, + can't be tapped.
		dismissCouponAppliedPopup(0);
		scrollToLastCartItem();
		int before = readLastItemQuantity();
		int cardsBefore = readCartProductCount();

		List<WebElement> plusIcons = driver.findElements(PLUS_ICON_LOCATOR);
		if (plusIcons.isEmpty()) {
			throw new RuntimeException("+ icon not found for the last cart item");
		}
		plusIcons.get(plusIcons.size() - 1).click();

		// The quantity is updated server-side, so the card keeps showing the old value for a
		// moment after the tap - reading it straight away gave "expected 2 but was 1" while
		// the cart actually went to 2.
		// For products with a lens (eyeglasses), + does not bump the counter: the cart adds a
		// separate line item instead ("Cart (2 products)", two cards each at qty 1). Both
		// outcomes mean the extra unit was added.
		waitForQuantityIncrease(before + 1, cardsBefore + 1);
	}

	private static final long QTY_UPDATE_TIMEOUT_MS = 15000;

	private void waitForQuantityIncrease(int expectedQty, int expectedCards) {
		long deadline = System.currentTimeMillis() + QTY_UPDATE_TIMEOUT_MS;
		int actual = -1;
		int cards = -1;
		while (System.currentTimeMillis() < deadline) {
			try {
				// No scrolling in here: the header count is always on screen, and the last card
				// was already scrolled into view before + was tapped.
				cards = readCartProductCount();
				// expectedCards <= 0 means the header couldn't be read before the tap - skip this check.
				if (expectedCards > 0 && cards >= expectedCards) {
					test().info("Extra unit added as a separate cart line (" + cards + " products in cart)");
					return;
				}
				actual = readLastItemQuantity();
				if (actual == expectedQty) {
					test().info("Increased qty of last cart item to " + expectedQty);
					return;
				}
			} catch (org.openqa.selenium.StaleElementReferenceException e) {
				// Card re-rendered between find and read - just poll again.
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		throw new RuntimeException("Expected last cart item qty " + expectedQty + " or " + expectedCards
				+ " cart items, but qty was " + actual + " with " + cards + " items after waiting "
				+ QTY_UPDATE_TIMEOUT_MS / 1000 + "s");
	}

	/** BoGo: one extra unit of the product just added (the last one in the cart). */
	public void applyOffer(String offer) {
		if (offer != null && offer.trim().equalsIgnoreCase("BoGo")) {
			test().info("Applying offer: " + offer);
			increaseQuantityOfLastItem();
		}
	}
}
