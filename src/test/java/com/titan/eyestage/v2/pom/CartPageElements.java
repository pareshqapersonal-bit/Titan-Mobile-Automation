package com.titan.eyestage.v2.pom;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class CartPageElements extends CommonUtils {

	AndroidDriver driver;

	FrameCart frameCart;
	EyeglassCart eyeglassCart;
	ContactLensCart contactLensCart;
	RxSunglassCart rxSunglassCart;
	SunglassCart sunglassCart;
	ReadingGlassesCart readingGlassesCart;
	ComputerGlassesCart computerGlassesCart;
	AccessoriesCart accessoriesCart;

	public CartPageElements(
			AndroidDriver driver,
			String mobileNumber,
			String password) {

		this.driver = driver;

		PageFactory.initElements(driver, this);

		frameCart = new FrameCart(driver, mobileNumber, password);
		eyeglassCart = new EyeglassCart(driver, mobileNumber, password);
		contactLensCart = new ContactLensCart(driver, mobileNumber, password);
		rxSunglassCart = new RxSunglassCart(driver, mobileNumber, password);
		sunglassCart = new SunglassCart(driver, mobileNumber, password);
		readingGlassesCart = new ReadingGlassesCart(driver, mobileNumber, password);
		computerGlassesCart = new ComputerGlassesCart(driver, mobileNumber, password);
		accessoriesCart = new AccessoriesCart(driver, mobileNumber, password);
	}

	@FindBy(id = "com.titan.eyecare:id/rl_toolbar_search")
	WebElement searchClick;

	@FindBy(id = "com.titan.eyecare:id/edt_search")
	WebElement searchField;

	@FindBy(id = "com.titan.eyecare:id/txt_product_name")
	WebElement productSelection;

	@FindBy(id = "com.titan.eyecare:id/txt_btn_title")
	WebElement buyNowCTA;

	@FindBy(xpath = "//android.widget.TextView[@resource-id='com.titan.eyecare:id/txt_btn_title' and @text='Continue Shopping']")
	WebElement continueShoppingButton;

	@FindBy(xpath = "//android.view.ViewGroup[@resource-id='com.titan.eyecare:id/toolbar_home']"
			+ "/android.widget.ImageView[@resource-id='com.titan.eyecare:id/img_toolbar_back']")
	WebElement cartBackButton;

	@FindBy(xpath = "//android.widget.LinearLayout[@resource-id='com.titan.eyecare:id/ll_search_bar']"
			+ "//android.widget.ImageView[@resource-id='com.titan.eyecare:id/img_back']")
	WebElement searchBackButton;

	@FindBy(id = "com.titan.eyecare:id/txt_btn_title")
	WebElement proceedToCheckout;

	@FindBy(id = "com.titan.eyecare:id/txt_title")
	WebElement cartTitle;

	String path = null;

	public void addProductsToCart(List<CartProduct> products) throws InterruptedException, IOException {

		for (int i = 0; i < products.size(); i++) {

			CartProduct product = products.get(i);

			searchProduct(product.getSku());
			visibilityOf(buyNowCTA);
			System.out.println("Buy Now CTA is visible for product: " + product.getSku());
			dispatchProduct(product);
			System.out.println("Product added to cart: " + product.getSku());

			// Just-in-case reset - most add-to-cart flows never leave NATIVE_APP, so this is a
			// no-op most of the time. Unguarded, a transient device/session hiccup on this one
			// housekeeping call would abort the whole loop even though the product was already
			// added successfully above.
			try {
				driver.context("NATIVE_APP");
			} catch (Exception e) {
				System.out.println("Could not confirm NATIVE_APP context after add-to-cart: " + e.getMessage());
			}

			List<WebElement> continueButtons = driver.findElements(
					AppiumBy.xpath(
							"//android.widget.TextView["
									+ "@resource-id='com.titan.eyecare:id/txt_btn_title' "
									+ "and @text='Continue Shopping']"));

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

			if (i < products.size() - 1) {
				System.out.println("Navigating back to search page for next product");

				backToSearch();
			}
		}

		System.out.println("All products added to cart");

		visibilityOf(cartTitle);
		visibilityOf(proceedToCheckout);

		path = captureScreenshot("Cart_Page");

		test().info("Cart Page Displayed");
		test().info("<a href='data:image/png;base64," + path + "' data-featherlight='image'><img src='data:image/png;base64," + path + "' style='width:200px;height:auto;cursor:pointer;'/></a>");

		assertTrue(
				cartTitle.isDisplayed(),
				"Cart page not displayed");
	}

	public void searchProduct(String sku) {

		System.out.println("Current Package = " + driver.getCurrentPackage());
		System.out.println("Current Activity = " + driver.currentActivity());
		click(searchClick);
		sendKeys(searchField, sku);
		click(productSelection);
	}

	private void dispatchProduct(CartProduct product) {

		switch (product.getCategory()) {

		case "Frame":
			System.out.println("Adding Frame Product to Cart");
			frameCart.addToCart(product);
			break;

		case "Eyeglass":
			System.out.println("Adding Eyeglass Product to Cart");
			eyeglassCart.addToCart(product);
			break;

		case "ContactLens":
			System.out.println("Adding Contact Lens Product to Cart");
			contactLensCart.addToCart(product);
			break;

		case "PoweredSunglass":
			System.out.println("Adding Powered Sunglass Product to Cart");
			rxSunglassCart.addToCart(product);
			break;

		case "Sunglass":
			System.out.println("Adding Sunglass Product to Cart");
			sunglassCart.addToCart(product);
			break;

		case "ReadingGlass":
			System.out.println("Adding Reading Glass Product to Cart");
			readingGlassesCart.addToCart();
			break;

		case "ComputerGlass":
			System.out.println("Adding Computer Glass Product to Cart");
			computerGlassesCart.addToCart();
			break;

		case "Accessories":
			System.out.println("Adding Accessories Product to Cart");
			accessoriesCart.addToCart();
			break;

		default:
			throw new RuntimeException("Unknown Category : " + product.getCategory());
		}
	}

	// Navigates back to the search page after adding a product to the cart. Cart-back
	// normally lands on the Product Detail Page (PDP), which needs its own back tap to
	// reach search - but on real BrowserStack devices, after several sequential
	// add-to-cart cycles the PDP activity can be gone from the back stack (Android
	// trimming background activities under memory pressure), so cart-back lands
	// straight on search instead. Wait for whichever one actually shows up instead of
	// assuming the PDP is always there, or this hangs the full 30s waiting on a PDP
	// that isn't coming back.
	public void backToSearch() {

		System.out.println("Clicking Cart Back");

		click(cartBackButton);

		System.out.println("Clicked Cart Back");

		if (waitForPdpOrSearch()) {

			System.out.println("PDP loaded");

			tapPdpBack();
			click(searchBackButton);

			System.out.println("Clicked PDP Back");

		} else {

			System.out.println("Cart Back landed straight on search page (PDP was skipped)");
		}
	}

	// Returns true if the PDP (buyNowCTA) showed up, false if search (searchClick, or an
	// already-active Search screen left over from an earlier product) showed up first instead.
	private boolean waitForPdpOrSearch() {

		// 30s previously caused this poll to time out on the Computer Glass -> Accessories
		// transition specifically: a live page-source dump taken immediately after that timeout
		// fired showed the expected edt_search field already present and correctly identified -
		// the screen was still settling right at the 30s boundary, not missing/misdetected. 40s
		// gives that transition the time it actually needs.
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

		// findElements() otherwise inherits the driver's 30s implicit wait (set in
		// Base.opn_app), so each empty lookup below can silently block up to 30s -
		// two lookups per poll means a single poll can blow past this wait's own
		// 30s budget before it ever gets a real retry. Drop it to near-zero for the
		// duration of this poll loop, then restore it (same fix as
		// LoginElements.isLoginPageDisplayed()).
		driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

		try {
			return wait.until(driver -> {

				if (!driver.findElements(AppiumBy.id("com.titan.eyecare:id/txt_btn_title")).isEmpty()) {
					return true;
				}

				if (!driver.findElements(AppiumBy.id("com.titan.eyecare:id/rl_toolbar_search")).isEmpty()) {
					return false;
				}

				// Cart Back can also restore an earlier product's still-open Search screen
				// straight from the back stack (the PDP entry in between got trimmed under
				// memory pressure) instead of landing on the collapsed search bar above - same
				// destination, but with the previous SKU still typed in and its result still
				// showing (or, per a live page-source capture, sometimes empty with just the
				// placeholder hint text - either way rl_toolbar_search doesn't exist on this
				// screen at all, so neither check above ever matches it). Detect it via the
				// search field itself and clear the stale query here, so the next
				// searchProduct(sku) call types into an empty field instead of appending onto
				// any leftover text.
				List<WebElement> staleSearchField =
						driver.findElements(AppiumBy.id("com.titan.eyecare:id/edt_search"));

				if (!staleSearchField.isEmpty()) {
					staleSearchField.get(0).clear();
					return false;
				}

				return null;
			});
		} finally {
			// Restoring the implicit wait is housekeeping, not the actual result of this poll -
			// if the session is already unstable, this call throwing would replace whatever the
			// try block above actually found/threw (Java's finally-supersedes-try behavior).
			try {
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
			} catch (Exception e) {
				System.out.println("Could not restore implicit wait after PDP/search poll: " + e.getMessage());
			}
		}
	}

	private void tapPdpBack() {

		WebElement back = driver.findElement(
				AppiumBy.id("com.titan.eyecare:id/img_toolbar_back"));

		Rectangle rect = back.getRect();

		int x = rect.getX() + rect.getWidth() / 2;
		int y = rect.getY() + rect.getHeight() / 2;

		System.out.println("PDP Back coordinates: x=" + x + ", y=" + y);

		PointerInput finger = new PointerInput(
				PointerInput.Kind.TOUCH,
				"finger");

		Sequence tap = new Sequence(finger, 1);

		tap.addAction(
				finger.createPointerMove(
						Duration.ZERO,
						PointerInput.Origin.viewport(),
						x,
						y));

		tap.addAction(
				finger.createPointerDown(
						PointerInput.MouseButton.LEFT.asArg()));

		tap.addAction(
				finger.createPointerUp(
						PointerInput.MouseButton.LEFT.asArg()));

		driver.perform(Collections.singletonList(tap));
	}
}
