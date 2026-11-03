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

// Status icon at the left of the card's header row ("Prescription Upload Required" etc.).
@FindBy(xpath = "(//android.widget.ScrollView//android.view.View[contains(@content-desc,'Order ID:')])[1]/android.widget.ImageView[1]")
	private WebElement selectOrderHeaderIcon;

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
private void openFirstOrder() {
	visibilityOf(selectOrder);

	Rectangle card = selectOrder.getRect();
	Rectangle icon = selectOrderHeaderIcon.getRect();

	int x = card.getX() + card.getWidth() / 2;
	int y = icon.getY() + icon.getHeight() / 2;

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
