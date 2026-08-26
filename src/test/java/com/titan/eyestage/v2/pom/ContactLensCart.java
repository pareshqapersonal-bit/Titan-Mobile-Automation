package com.titan.eyestage.v2.pom;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

// Handles adding a Contact Lens product to the cart, including right-eye
// power (SPH/CYL/AXIS) and quantity selection.
public class ContactLensCart extends CommonUtils {

	AndroidDriver driver;
	private final String mobileNumber;
	private final String password;
	LoginElements le;

	public ContactLensCart(AndroidDriver driver, String mobileNumber, String password) {

		this.driver = driver;
		this.mobileNumber = mobileNumber;
		this.password = password;
		PageFactory.initElements(driver, this);
		le = new LoginElements(driver);
	}

	// Right Eye
	@FindBy(id = "com.titan.eyecare:id/edt_sph_right")
	WebElement rightSPH;

	@FindBy(id = "com.titan.eyecare:id/edt_cyl_right")
	WebElement rightCYL;

	@FindBy(id = "com.titan.eyecare:id/edt_axis_right")
	WebElement rightAXIS;

	@FindBy(id = "com.titan.eyecare:id/rl_quantity_right")
	WebElement rightQty;

	// Left Eye
	@FindBy(id = "com.titan.eyecare:id/edt_sph_left")
	WebElement leftSPH;

	@FindBy(id = "com.titan.eyecare:id/edt_cyl_left")
	WebElement leftCYL;

	@FindBy(id = "com.titan.eyecare:id/edt_axis_left")
	WebElement leftAXIS;

	@FindBy(id = "com.titan.eyecare:id/edt_quantity_left")
	WebElement leftQty;

	@FindBy(id = "com.titan.eyecare:id/txt_btn_title")
	WebElement buyNowCTA;

	@FindBy(id = "com.titan.eyecare:id/txt_subscription_skip")
	WebElement skipOption;

	public void addToCart(CartProduct product) {

		System.out.println("Inside Adding Contact Lens Product");

		test().info("Adding Contact Lens Product");

		smallSwipeUp();

		System.out.println("Before SPH");
		selectIfPresent(rightSPH, "-0.75");
		System.out.println("After SPH");

		smallSwipeUp();

		System.out.println("Before Quantity");
		selectIfPresent(rightQty, "1");
		System.out.println("After Quantity");

		System.out.println("Before Buy Now");
		click(buyNowCTA);
		System.out.println("After Buy Now");

		if (le.isLoginPageDisplayed()) {

			System.out.println("Login Required");

			le.shortLogin(mobileNumber, password);

			System.out.println("Login Done");

			scrollToText("Power(SPH)");

			System.out.println("Before SPH");
			selectIfPresent(rightSPH, "-0.75");
			System.out.println("After SPH");

			click(buyNowCTA);
		}

		click(skipOption);
		test().pass("Contact Lens added successfully");
	}

	private void scrollToText(String text) {

		driver.findElement(
				AppiumBy.androidUIAutomator(
						"new UiScrollable(new UiSelector().scrollable(true))"
								+ ".scrollIntoView(new UiSelector().text(\"" + text + "\"))"));
	}

	private void selectIfPresent(WebElement dropdown, String value) {

		System.out.println("Inside selectIfPresent");

		if (value == null || value.trim().isEmpty())
			return;

		if (driver.findElements(By.id(dropdown.getAttribute("resource-id"))).size() > 0) {

			System.out.println("Clicking Dropdown");
			click(dropdown);

			System.out.println("Selecting Value : " + value);

			click(driver.findElement(
					AppiumBy.androidUIAutomator(
							"new UiSelector().text(\"" + value + "\")")));

			System.out.println("Value Selected");

			test().info("Selected : " + value);
		}

		System.out.println("Exiting selectIfPresent");
	}

	public void smallSwipeUp() {

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
}
