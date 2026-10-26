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

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

// Handles adding a Contact Lens product to the cart, including right-eye
// power (SPH/CYL/AXIS) and quantity selection.
public class ContactLensCartNew extends CommonUtils {

	AndroidDriver driver;
	
	@AndroidFindBy(accessibility = "Add to cart")
	WebElement addToCartButton;
	
	@AndroidFindBy(accessibility = "Request a Callback")
	WebElement callBackButton;
	
	@AndroidFindBy(accessibility = "Subscribe and Save")
	WebElement subandSave;

	public ContactLensCartNew(AndroidDriver driver) {

		this.driver = driver;
		
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		
	}

	
	//Left Eye
	// Positional (not @text='Select') so it still matches after a value is chosen
	@FindBy(xpath = "//android.view.View[@content-desc='SPH']/following::android.widget.Button[1]")
	WebElement leftSPHSelect;
	
	@FindBy(xpath = "//android.view.View[@content-desc='CYL']/following::android.widget.Button[1]")
	WebElement leftCYLSelect;
	
	@FindBy(xpath = "//android.view.View[@content-desc='AXIS']/following::android.widget.Button[1]")
	WebElement leftAxisSelect;
	
	@FindBy(xpath = "//android.view.View[@content-desc='No of boxes']/following::android.widget.Button[1]")
	WebElement leftBoxesSelect;
	
	
	
	// Right Eye
	@FindBy(xpath = "//android.view.View[@content-desc='SPH']/following::android.widget.Button[2]")
	WebElement rightSPHSelect;
	
	@FindBy(xpath = "//android.view.View[@content-desc='CYL']/following::android.widget.Button[2]")
	WebElement rightCYLSelect;
	
	@FindBy(xpath = "//android.view.View[@content-desc='AXIS']/following::android.widget.Button[2]")
	WebElement rightAxisSelect;
	
	@FindBy(xpath = "//android.view.View[@content-desc='No of boxes']/following::android.widget.Button[2]")
	WebElement rightBoxesSelect;


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

	@AndroidFindBy(accessibility = "Proceed without subscription")
	WebElement skipOption;

	public void addToCart(CartProduct product) {

		System.out.println("Inside Adding Contact Lens Product");

		test().info("Adding Contact Lens Product");

		smallSwipeUp();

		System.out.println("Before SPH");
		selectIfPresent(leftSPHSelect, "5");
		
		
		selectIfPresent(rightSPHSelect, "5");
		
		System.out.println("After SPH");

		selectIfPresent(leftCYLSelect, "-0.75");
		selectIfPresent(rightCYLSelect, "-0.75");
		
		selectIfPresent(leftAxisSelect, "80");
		selectIfPresent(rightAxisSelect, "80");

		selectIfPresent(leftBoxesSelect, "1");
		selectIfPresent(rightBoxesSelect, "1");
		click(addToCartButton);
		System.out.println("After Buy Now");
		
		// isDisplayed() on a PageFactory element throws NoSuchElementException (after the 30s
		// implicit wait) when the element is absent, so a missing callback popup used to abort
		// before the subscription sheet was handled. clickIfPresent() returns false instead.
		clickIfPresent(callBackButton, 5);

		// "Subscribe and Save" sheet appears after Add to cart; skip it if shown
		if (clickIfPresent(skipOption, 10)) {
			System.out.println("Skipped Subscribe and Save");
		}

		
		test().pass("Contact Lens added successfully");
	}

	private void scrollToText(String text) {

		driver.findElement(
				AppiumBy.androidUIAutomator(
						"new UiScrollable(new UiSelector().scrollable(true))"
								+ ".scrollIntoView(new UiSelector().text(\"" + text + "\"))"));
	}


	private void selectIfPresent(WebElement dropdown, String value) {
	    if (value == null || value.trim().isEmpty()) {
	        return;
	    }

	    // Open the dropdown; the list appears as an extra ScrollView. Retry once
	    // because the first tap is sometimes swallowed while the page settles.
	    By scrollableBy = By.xpath("//*[@scrollable='true']");
	    int before = driver.findElements(scrollableBy).size();
	    Rectangle list = null;
	    for (int attempt = 0; attempt < 2 && list == null; attempt++) {
	        click(dropdown);
	        long end = System.currentTimeMillis() + 3000;
	        while (System.currentTimeMillis() < end) {
	            List<WebElement> scrollables = driver.findElements(scrollableBy);
	            if (scrollables.size() > before) {
	                // The popup list is drawn last, so it is the last scrollable
	                list = scrollables.get(scrollables.size() - 1).getRect();
	                break;
	            }
	        }
	    }
	    if (list == null) {
	        throw new RuntimeException("Dropdown list did not open for value '" + value + "'");
	    }

	    // Options expose the value as content-desc, e.g. "6", "0", "-2.25"
	    By option = By.xpath("//*[@content-desc='" + value + "']");

	    // List runs high to low (6 ... -2.25 ...), so scroll down first, then up
	    boolean[] directions = {true, false};
	    for (boolean scrollDown : directions) {
	        String lastPage = "";
	        for (int i = 0; i < 25; i++) {
	            for (WebElement match : driver.findElements(option)) {
	                Rectangle r = match.getRect();
	                int centerY = r.y + r.height / 2;
	                // Off-screen rows are still in the hierarchy; only tap a visible one
	                if (centerY > list.y && centerY < list.y + list.height) {
	                    match.click();
	                    System.out.println("Clicked option: " + value);
	                    test().info("Selected: " + value);
	                    return;
	                }
	            }
	            String page = driver.getPageSource();
	            if (page.equals(lastPage)) {
	                break; // reached end of list in this direction
	            }
	            lastPage = page;
	            swipeInDropdownList(list, scrollDown);
	        }
	    }

	    throw new RuntimeException("Option '" + value + "' not found in dropdown");
	}

	// Swipes inside the open dropdown list bounds so the page behind does not scroll.
	private void swipeInDropdownList(Rectangle list, boolean scrollDown) {
	    int x = list.x + list.width / 2;
	    int from = list.y + (int) (list.height * (scrollDown ? 0.8 : 0.2));
	    int to = list.y + (int) (list.height * (scrollDown ? 0.2 : 0.8));

	    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
	    Sequence swipe = new Sequence(finger, 1);
	    swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, from));
	    swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
	    swipe.addAction(finger.createPointerMove(Duration.ofMillis(400), PointerInput.Origin.viewport(), x, to));
	    swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
	    driver.perform(List.of(swipe));
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
