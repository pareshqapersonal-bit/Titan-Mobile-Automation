package com.titan.eyestage.v2.pom;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.Base;
import com.titan.eyestage.v2.utils.CommonUtils;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class RemoveProductsFromCartPAge extends CommonUtils{
	AndroidDriver driver;

	// Manually confirmed in Appium Inspector: matches exactly the per-item cross/delete icons
	// (3 matched on a 5-item cart at one scroll position - the other 2 items weren't rendered
	// yet, since the ScrollView virtualizes off-screen rows). Direct child of the ScrollView,
	// clickable, with no content-desc at all - not(@content-desc), since the attribute is
	// omitted entirely rather than serialized as "" when there's no label. That excludes a
	// different empty-content-desc clickable nested *inside* each item card (a qty control),
	// since that one's a descendant, not a direct ScrollView child.
	//
	// Out-of-stock rows use a differently-styled icon that, unlike the in-stock one, does carry
	// a label - content-desc="Remove" - so the second half of this union picks those up too.
	// Both halves stay scoped to "direct child of the ScrollView" so this never matches the
	// confirm popup's own button below, which also happens to be labeled "Remove" but lives
	// outside the ScrollView.
	//
	// Kept as a locator (not a WebElement field) so removeProductFromCart() below can capture
	// the live list fresh every iteration and use list.isEmpty() itself as the "cart is empty"
	// signal - no separate empty-cart check needed, and no risk of acting on a stale reference
	// to an item that's already been removed and re-rendered away.
	private static final By REMOVE_ICON_LOCATOR = AppiumBy.xpath(
			"//android.widget.ScrollView/android.view.View[@clickable=\"true\" and not(@content-desc)]"
			+ " | //android.widget.ScrollView/android.view.View[@content-desc=\"Remove\"]");

	// It's an ImageView, not a View - confirmed via Appium Inspector. That tag difference from
	// the out-of-stock row icon above (android.view.View) is what keeps the two from colliding
	// even though both carry the same "Remove" content-desc; not(ancestor::ScrollView) is kept
	// as a second guard in case a future layout ever reuses ImageView inside the row too.
	@AndroidFindBy(xpath = "//android.widget.ImageView[@content-desc=\"Remove\" and not(ancestor::android.widget.ScrollView)]")
	WebElement removeButton;

	@FindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.widget.ImageView[5]")
	WebElement cartIcon;

	// This is the offer-popup's own "Continue Shopping" (content-desc, Flutter semantics node) -
	// NOT the native txt_btn_title button CartPageElementsNew uses after adding a product.
	// Using that native locator here was the actual bug: findElements() below correctly
	// detected this popup by content-desc, but click() was then searching for a completely
	// different element that doesn't exist on this screen, polling 30s for nothing while the
	// popup closed and backgrounded the app on its own in the meantime.
	@AndroidFindBy(accessibility = "Continue Shopping")
	WebElement continueShoppingButton;



	public RemoveProductsFromCartPAge(AndroidDriver driver) {
		this.driver = driver;

		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}


	// Capped rather than an unbounded while - if removal stops actually working (e.g. a
	// locator mismatch or a stuck dialog), this fails loudly instead of hanging the whole
	// suite.
	private static final int MAX_REMOVE_ATTEMPTS = 20;

	public void removeProductFromCart() {

		click(cartIcon);
		dismissContinueShoppingPopupIfPresent();

		List<WebElement> removeIcons = driver.findElements(REMOVE_ICON_LOCATOR);

		for (int attempt = 1; !removeIcons.isEmpty(); attempt++) {

			if (attempt > MAX_REMOVE_ATTEMPTS) {
				throw new RuntimeException(
						"Cart still has items after " + MAX_REMOVE_ATTEMPTS + " remove attempts");
			}

			// Always the current first match - re-captured below on every iteration, so this
			// is never a reference to an item that's already been removed.
			removeIcons.get(0).click();
			click(removeButton);
			dismissContinueShoppingPopupIfPresent();

			removeIcons = driver.findElements(REMOVE_ICON_LOCATOR);
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
