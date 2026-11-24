package com.titan.eyestage.v2.pom;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

// Handles adding an Eyeglass product to the cart, including the optional
// lens-upgrade dialog and lens-add page that this category presents.
public class EyeglassCartNew extends CommonUtils {

	AndroidDriver driver;
	
	
	List<WebElement> continueButtons;
	ApplyCopounOffersPage applyCopounOffersPage;
	
	@FindBy(xpath = "//android.view.View[@content-desc=\"Continue Shopping\"]")
	WebElement continueShoppingButton;

	public EyeglassCartNew(AndroidDriver driver) {

		this.driver = driver;
		
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		
	}

	@AndroidFindBy(accessibility = "Add to cart")
	WebElement addToCartButton;
	
	// "Add Lens" alone matches every lens card and Appium takes the first one. Anchor on the
	// DRIVEZ card (the lens with the Buy 1 Get 1 offer) and take its own "Add Lens" button.
	// The DRIVEZ card's content-desc runs down to its price, so Add Lens is normally inside it.
	// Don't use following:: here - it skips the card's own children and lands on the NEXT
	// lens's Add Lens. The sibling branch covers a button that sits next to the card instead.
	@AndroidFindBy(xpath = "(//android.view.View[contains(@content-desc,'DRIVEZ')]//*[@content-desc='Add Lens'] | //android.view.View[contains(@content-desc,'DRIVEZ')]/following-sibling::*[@content-desc='Add Lens'])[1]")
	WebElement addLensButton;
	
	@AndroidFindBy(accessibility = "Continue")
	WebElement continueButton;

	@FindBy(xpath="//android.widget.ImageView[@content-desc=\"Single Vision\n"
			+ "For positive, negative or intermediate power.\"]")
	WebElement lensAddCTA;

	@FindBy(xpath = "//android.widget.TextView[@resource-id='com.titan.eyecare:id/txt_title' and @text='Choose Lens Upgrades']")
	WebElement lensUpgradePage;

	@FindBy(id = "com.titan.eyecare:id/txt_package_info_title")
	WebElement lensAddPage;

	@FindBy(xpath = "//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_lens_add_dialog_title\"]")
	WebElement lensAddDialog;

	@FindBy(id = "com.titan.eyecare:id/txt_btn_title")
	WebElement lensAddDialogCTA;

	@FindBy(id = "android:id/button1")
	WebElement okSub;
	
	@FindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View[1]/android.view.View/android.view.View/android.view.View[12]")	
    	WebElement powerToggle;
	

	public void addToCart(CartProduct product,String offer) {

		click(addToCartButton);
		click(lensAddCTA);
        click(powerToggle);
        scrollToDrivezLens();
        click(addLensButton);
        click(continueButton);
		
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
		
		
		System.out.println("offer is " + offer);
		if (offer != null && !offer.isEmpty()) {
			System.out.println("Applying offer: " + offer);
			applyCopounOffersPage.applyOffer(offer);
		}
		//lensAddDialogCTA.isDisplayed();
//		click(lensAddDialogCTA);
//		visibilityOf(lensAddPage);
//		System.out.println("Lens Add page displayed");
//		click(lensAddCTA);
//
//		handleLensUpgradePage();
//		click(okSub);
	}

	private static final org.openqa.selenium.By DRIVEZ_ADD_LENS = AppiumBy.xpath(
			"(//android.view.View[contains(@content-desc,'DRIVEZ')]//*[@content-desc='Add Lens'] | //android.view.View[contains(@content-desc,'DRIVEZ')]/following-sibling::*[@content-desc='Add Lens'])[1]");

	// DRIVEZ is second in the lens list. On first load only the top of its card (the title) is
	// on screen and its Add Lens button is below the fold - and not rendered at all, so the
	// locator finds nothing. Scrolling "until DRIVEZ is visible" stops at once because the
	// title already is, so instead scroll the lens sheet until the DRIVEZ Add Lens button
	// itself is on screen, clear of the bottom nav bar.
	private void scrollToDrivezLens() {
		driver.manage().timeouts().implicitlyWait(java.time.Duration.ofMillis(500));
		try {
			org.openqa.selenium.Dimension size = driver.manage().window().getSize();
			for (int i = 0; i < 5; i++) {
				List<WebElement> buttons = driver.findElements(DRIVEZ_ADD_LENS);
				if (!buttons.isEmpty()) {
					org.openqa.selenium.Rectangle r = buttons.get(0).getRect();
					if (r.getY() + r.getHeight() < size.getHeight() * 0.85) {
						System.out.println("DRIVEZ Add Lens button on screen");
						return;
					}
				}
				// Swipe inside the lens list (middle of the screen, below the sheet header).
				driver.executeScript("mobile: scrollGesture", java.util.Map.of(
						"left", (int) (size.getWidth() * 0.1),
						"top", (int) (size.getHeight() * 0.45),
						"width", (int) (size.getWidth() * 0.8),
						"height", (int) (size.getHeight() * 0.4),
						"direction", "down",
						"percent", 0.4,
						// Default speed (5000px/s) flings the list past the target; keep it slow.
						"speed", 1000));
			}
			System.out.println("DRIVEZ Add Lens button still not on screen after scrolling");
		} finally {
			driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(30));
		}
	}

	// Skip optional lens upgrade selection
	private void handleLensUpgradePage() {

		System.out.println("Checking for Lens Upgrade page");
		visibilityOf(lensUpgradePage);

		List<WebElement> skipButton = driver.findElements(
				AppiumBy.xpath("//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_skip\"]"));

		if (!skipButton.isEmpty() && skipButton.get(0).isDisplayed()) {

			System.out.println("Lens Upgrade page displayed");
			skipButton.get(0).click();
			System.out.println("Clicked Skip on Lens Upgrade page");

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

			wait.until(driver -> driver.findElements(
					AppiumBy.xpath(
							"//android.widget.TextView["
									+ "@resource-id='com.titan.eyecare:id/txt_btn_title' "
									+ "and @text='Buy Now']"))
					.stream()
					.filter(WebElement::isDisplayed)
					.findFirst()
					.orElse(null));

			System.out.println("Buy Now button visible again");

			click(addToCartButton);

		} else {

			System.out.println("Lens Upgrade page not displayed");
		}
		
		
		
		
	}
}
