package com.titan.eyestage.v2.pom;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.titan.eyestage.v2.utils.CommonUtils;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class OrderDetailsPage extends CommonUtils {
	AndroidDriver driver;

	public OrderDetailsPage(AndroidDriver driver) {
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

@AndroidFindBy(accessibility = "View order details")
	private WebElement viewOrderDetailsButton;

// First order in the list (most recent).
@FindBy(xpath = "(//android.widget.ScrollView//android.view.View[contains(@content-desc,'Order ID:')])[1]")
	private WebElement selectOrder;

@FindBy(xpath = "//android.widget.Button")
private WebElement selectReasonTab;

@FindBy(xpath = "//android.widget.ScrollView[1]")
private WebElement selectReason;

@AndroidFindBy(accessibility = "Cancel Order")
	private WebElement cancelOrderButton;



public void cancelOrder() {
	click(viewOrderDetailsButton);
	openFirstOrder();
	scrollToCancelOrder();
	// Fails here, rather than on some later step, if the tap opened the PDP again.
	visibilityOf(cancelOrderButton);
	click(cancelOrderButton);
	click(selectReasonTab);
	click(selectReason);
	click(cancelOrderButton);
	
}

// The order card is one clickable View spanning the whole card, and click() taps its centre -
// which lands on the product image row (its own clickable child) and opens the PDP instead of
// the order. Tapping the card's header row instead hits only the card itself. Confirmed on a
// Vivo V25: centre tap at y=1021 opened the PDP, header-row tap at y=725 opened Orders Details.
//
// The top of the card opens Order Details for both card types: orders with an eyeglass show an
// "Upload Prescription" strip there (tapping it also opens Order Details), orders without one
// start with the Order ID header row. Product thumbnails sit below that and open the PDP. The
// old anchor, ImageView[1], is the status icon only on some cards - otherwise it's the first
// thumbnail (opened the PDP on the Other UPI row, 05-Nov-2026). So tap just inside the top edge,
// capped in pixels so a tall multi-item card doesn't push the point down into the thumbnails;
// the old icon tap is only a last resort. After each tap, if the PDP opened, go back and try
// the next point.
private static final By FIRST_ORDER_CARD = By.xpath(
		"(//android.widget.ScrollView//android.view.View[contains(@content-desc,'Order ID:')])[1]");
private static final By FIRST_ORDER_HEADER_ICON = By.xpath(
		"(//android.widget.ScrollView//android.view.View[contains(@content-desc,'Order ID:')])[1]/android.widget.ImageView[1]");
private static final By PDP_ADD_TO_CART = AppiumBy.accessibilityId("Add to cart");

private void openFirstOrder() {
	visibilityOf(selectOrder);

	Rectangle card = driver.findElement(FIRST_ORDER_CARD).getRect();
	int x = card.getX() + card.getWidth() / 2;

	List<Integer> tapYs = new ArrayList<>();
	tapYs.add(card.getY() + Math.min(50, Math.max(20, card.getHeight() / 20)));
	tapYs.add(card.getY() + 10);
	if (isElementPresent(FIRST_ORDER_HEADER_ICON)) {
		Rectangle icon = driver.findElement(FIRST_ORDER_HEADER_ICON).getRect();
		tapYs.add(icon.getY() + icon.getHeight() / 2);
	}

	for (int i = 0; i < tapYs.size(); i++) {
		int y = tapYs.get(i);
		System.out.println("Opening first order: tap " + (i + 1) + "/" + tapYs.size()
				+ " at (" + x + "," + y + "), card=" + card);
		tapAt(x, y);

		if (!pdpOpened()) {
			return;
		}

		System.out.println("Order card tap at y=" + y + " opened the PDP - going back to the order list");
		driver.navigate().back();
		visibilityOf(selectOrder);
		card = driver.findElement(FIRST_ORDER_CARD).getRect();
	}

	throw new AssertionError("Tapping the first order card kept opening the PDP instead of "
			+ "Order Details (tried y=" + tapYs + " on card " + card + ")");
}

// The PDP shows "Add to cart"; Order Details never does. Give it a few seconds to render.
private boolean pdpOpened() {
	try {
		new WebDriverWait(driver, Duration.ofSeconds(6))
				.until(d -> isElementPresent(PDP_ADD_TO_CART));
		return true;
	} catch (TimeoutException e) {
		return false;
	}
}

private void tapAt(int x, int y) {
	PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
	Sequence tap = new Sequence(finger, 1);

	tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
	tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
	tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

	driver.perform(List.of(tap));
}

// Cancel Order sits below the item list and isn't in the hierarchy until it's on screen. On a
// 7-item order (Vivo V25) the first screen ends partway through the item cards, so the button
// was never found without scrolling.
private void scrollToCancelOrder() {
	By cancelOrder = AppiumBy.accessibilityId("Cancel Order");

	Dimension size = driver.manage().window().getSize();
	int x = size.width / 2;

	for (int attempt = 0; attempt < 10 && !isElementPresent(cancelOrder); attempt++) {

		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence swipe = new Sequence(finger, 1);

		swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, (int) (size.height * 0.70)));
		swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		swipe.addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), x, (int) (size.height * 0.30)));
		swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

		driver.perform(List.of(swipe));
	}
}
}
