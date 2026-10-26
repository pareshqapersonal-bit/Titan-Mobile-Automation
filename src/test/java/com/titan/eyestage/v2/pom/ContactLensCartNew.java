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
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

// Handles adding a Contact Lens product to the cart, including right-eye
// power (SPH/CYL/AXIS) and quantity selection.
public class ContactLensCartNew extends CommonUtils {

	AndroidDriver driver;
	

	public ContactLensCartNew(AndroidDriver driver) {

		this.driver = driver;
		
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		
	}

	
	//Left Eye
	@FindBy(xpath = "//android.view.View[@content-desc='SPH']/following::android.widget.Button[@text='Select'][1]")
	WebElement leftSPHSelect;
	
	
	
	// Right Eye
	@FindBy(xpath = "//android.view.View[@content-desc='SPH']/following::android.widget.Button[@text='Select'][2]")
	WebElement rightSPHSelect;

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
		selectIfPresent(leftSPHSelect, "-2.25");
		System.out.println("After SPH");

		smallSwipeUp();

		System.out.println("Before Quantity");
		selectIfPresent(rightQty, "1");
		System.out.println("After Quantity");

		System.out.println("Before Buy Now");
		click(buyNowCTA);
		System.out.println("After Buy Now");

		

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
		else {
		/*
		 * if (driver.findElements(By.id(dropdown.getAttribute("resource-id"))).size() >
		 * 0) {
		 * 
		 * System.out.println("Clicking Dropdown"); click(dropdown);
		 * 
		 * System.out.println("Selecting Value : " + value);
		 * 
		 * click(driver.findElement( AppiumBy.androidUIAutomator(
		 * "new UiSelector().text(\"" + value + "\")")));
		 */

			
			dropdown.click();

			WebElement option = driver.findElement(
			    AppiumBy.androidUIAutomator(
			        "new UiScrollable(new UiSelector().className(\"android.widget.ScrollView\").instance(1))" +
			        ".setAsVerticalList()" +
			        ".scrollToEnd(13)" 
			    )
			);
		

			//option.click();
		
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
