package com.titan.eyestage.v2.pom;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
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

public class CartPageElementsNew extends CommonUtils {

	AndroidDriver driver;

	FrameCartNew frameCart;
	EyeglassCartNew eyeglassCart;
	ContactLensCartNew contactLensCart;
	RxSunglassCartNew rxSunglassCart;
	SunglassCartNew sunglassCart;
	ReadingGlassesCartNew readingGlassesCart;
	ComputerGlassesCartNew computerGlassesCart;
	AccessoriesCartNew accessoriesCart;
	RemoveProductsFromCartPAge removeProductsFromCartPage;

	public CartPageElementsNew(
			AndroidDriver driver
			) {

		this.driver = driver;

		PageFactory.initElements(driver, this);

		frameCart = new FrameCartNew(driver);
		eyeglassCart = new EyeglassCartNew(driver);
		contactLensCart = new ContactLensCartNew(driver);
		rxSunglassCart = new RxSunglassCartNew(driver);
		sunglassCart = new SunglassCartNew(driver);
		readingGlassesCart = new ReadingGlassesCartNew(driver);
		computerGlassesCart = new ComputerGlassesCartNew(driver);
		accessoriesCart = new AccessoriesCartNew(driver);
		 removeProductsFromCartPage = new RemoveProductsFromCartPAge(driver);
		
	}

	@FindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.widget.ImageView[3]")
	WebElement searchClick;

	@FindBy(xpath = "//android.widget.EditText")
	WebElement searchField;
	
	@FindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.widget.ImageView")
	WebElement cartBack;

	// Before the real results render, the search panel shows the default category chips
	// (Frame, Sunglass, Computer Glasses, ...) at this same screen position. "Related
	// Products" turned out to be the wrong target entirely - it's a loosely-related list
	// (confirmed when searching RxP222GR1, a Powered Sunglass, surfaced a plain Sunglass
	// card), not reliably tied to the exact SKU typed. The actual SKU match is the single
	// "SUGGESTIONS" row, which redirects straight to that product's PDP. Its heading and
	// value are combined into one node's content-desc ("SUGGESTIONS\n<product name>"), not
	// a separate heading + following card, so starts-with on that single node is both the
	// wait condition and the click target - no following:: axis needed. Wildcard element
	// type (*) rather than a specific widget class, since widget types have been
	// inconsistent across this app's cards.
	private static final By SUGGESTION_ROW = AppiumBy.xpath("//*[starts-with(@content-desc,'SUGGESTIONS')]");

	@FindBy(xpath = "//android.view.View[@content-desc=\"Add to cart\"]")
	WebElement buyNowCTA;

	@FindBy(xpath = "//android.view.View[@content-desc=\"Continue Shopping\"]")
	WebElement continueShoppingButton;

	@FindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.widget.ImageView")
	WebElement cartBackButton;

	@FindBy(xpath = "//android.view.View[@content-desc=\"Proceed to checkout\"]")
	WebElement proceedToCheckout;

	@FindBy(xpath = "//android.view.View[starts-with(@content-desc,\"Cart\")]")
	WebElement cartTitle;

	String path = null;

	public void addProductsToCart(List<CartProduct> products) throws InterruptedException, IOException {

		// Continue Shopping popup dismissal now lives inside removeProductFromCart() itself -
		// it was popping up right on entering the cart, before a check placed here (after
		// already leaving the cart) ever got a chance to see it.
		removeProductsFromCartPage.removeProductFromCart();
		click(cartBackButton);

		List<WebElement> continueButtons;

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

		// Encircle check removed here (New-UI class) - same as the other "New" classes.
		// if (isEncirclepopupDisplayed()) {
		// 	click(laterCTA);
		// }

		click(searchClick);

		// The SUGGESTIONS row likely has the same leftover-content risk Related Products
		// had - it may still show the previous product's suggestion for a moment before
		// refreshing for the SKU just typed. starts-with(@content-desc,'SUGGESTIONS') alone
		// only proves *a* suggestion row exists, not that it's the new one, so a leftover
		// row from the prior SKU could satisfy it immediately. Snapshot whatever's showing
		// right as the search screen opens, then after typing, wait for that text to
		// actually change before trusting it.
		String previousSuggestionText = currentSuggestionText();

		sendKeys(searchField, sku);

		WebElement suggestion;

		try {
			suggestion = waitForSuggestionToRefresh(previousSuggestionText);
		} catch (TimeoutException e) {
			try {
				path = captureScreenshot("Search_No_Suggestion_" + sku);
				test().fail("No SUGGESTIONS row for SKU '" + sku + "'");
				test().info("<a href='data:image/png;base64," + path + "' data-featherlight='image'><img src='data:image/png;base64," + path + "' style='width:200px;height:auto;cursor:pointer;'/></a>");
			} catch (Exception screenshotError) {
				System.out.println("Could not capture search failure screenshot: " + screenshotError.getMessage());
			}
			throw new TimeoutException("No SUGGESTIONS row for SKU '" + sku + "'", e);
		}

		click(suggestion);
	}

	// The hierarchy can hold more than one SUGGESTIONS node - a hidden one left over from an
	// earlier search screen alongside the one actually on screen. productSelection (and any
	// findElement) resolves to the first match, which was the hidden one: visibilityOf() then
	// timed out after 30s with the real suggestion plainly visible in the failure screenshot.
	// Only ever consider a displayed match.
	private WebElement displayedSuggestion() {

		for (WebElement match : driver.findElements(SUGGESTION_ROW)) {
			try {
				if (match.isDisplayed()) {
					return match;
				}
			} catch (StaleElementReferenceException e) {
				// Re-rendered mid-check - the next poll re-finds it.
			}
		}

		return null;
	}

	private String currentSuggestionText() {

		// Called right as the search screen opens, usually before any suggestion exists -
		// drop the 30s implicit wait so an empty lookup doesn't stall for 30s.
		driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

		try {
			WebElement suggestion = displayedSuggestion();
			return suggestion == null ? null : suggestion.getAttribute("contentDescription");
		} finally {
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		}
	}

	// Waits for a displayed SUGGESTIONS row whose text differs from whatever was showing
	// before this SKU was typed, and returns it as the click target. The "text changed" part
	// is bounded rather than indefinite - Frame and Eyeglass share a SKU in this suite's test
	// data, so a legitimately-unchanged suggestion (same product, searched again) must not
	// block forever; after an 8s settle window, accept whatever displayed suggestion is there.
	// Throws TimeoutException if no displayed suggestion shows up at all within 30s.
	private WebElement waitForSuggestionToRefresh(String previousSuggestionText) {

		long settleDeadlineMillis = System.currentTimeMillis() + Duration.ofSeconds(8).toMillis();

		driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

		try {
			return new WebDriverWait(driver, Duration.ofSeconds(30))
					.ignoring(StaleElementReferenceException.class)
					.until(d -> {

						WebElement suggestion = displayedSuggestion();

						if (suggestion == null) {
							return null;
						}

						if (previousSuggestionText == null
								|| !previousSuggestionText.equals(suggestion.getAttribute("contentDescription"))) {
							return suggestion;
						}

						if (System.currentTimeMillis() >= settleDeadlineMillis) {
							System.out.println("SUGGESTIONS row text unchanged after 8s settle window - "
									+ "proceeding (likely a legitimately repeated SKU)");
							return suggestion;
						}

						return null;
					});
		} finally {
			try {
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
			} catch (Exception e) {
				System.out.println("Could not restore implicit wait after suggestion poll: " + e.getMessage());
			}
		}
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

	private enum Screen { HOME, SEARCH, PDP, CART, UNKNOWN }

	private static final By HOME_TAB = AppiumBy.accessibilityId("Home");
	private static final By CART_HEADER =
			AppiumBy.xpath("//android.view.View[starts-with(@content-desc,'Cart') and contains(@content-desc,'product')]");
	private static final By PDP_ADD_TO_CART = AppiumBy.accessibilityId("Add to cart");
	private static final By SEARCH_BACK = AppiumBy.className("android.widget.Button");
	private static final By SEARCH_FIELD = AppiumBy.className("android.widget.EditText");

	// Navigates back to Home after adding a product to the cart, so the next searchProduct()
	// starts from the home header's search icon. Normally that's cart -> PDP -> search -> home,
	// but the app doesn't always follow that path: after the Rx Sunglass add, cart-back ended up
	// on the search screen instead. The old version told PDP apart from search by "any ImageView
	// on screen" - true on every screen, including search (its Related Products card) - so it
	// "tapped PDP back" on the search screen, which actually opened that product card, then
	// failed looking for the search back Button on the PDP it had just opened. Instead, detect
	// the current screen from markers unique to it (verified via live UI dumps) and take one
	// back step at a time until Home shows up, whatever screen we start from.
	public void backToSearch() {

		long deadlineMillis = System.currentTimeMillis() + Duration.ofSeconds(60).toMillis();

		Screen lastActedOn = null;
		long lastActionMillis = 0;

		driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

		try {
			while (System.currentTimeMillis() < deadlineMillis) {

				Screen screen = currentScreen();

				if (screen == Screen.HOME) {
					System.out.println("Back on Home - ready for next search");
					return;
				}

				// Give the previous back tap time to take effect before acting on the same
				// screen again, or a slow transition gets double-tapped.
				boolean settling = screen == lastActedOn
						&& System.currentTimeMillis() - lastActionMillis < 5000;

				if (screen != Screen.UNKNOWN && !settling) {

					System.out.println("backToSearch: on " + screen + ", stepping back");

					switch (screen) {
					case CART:
						click(cartBackButton);
						break;
					case PDP:
						tapPdpBack();
						break;
					case SEARCH:
						driver.findElement(SEARCH_BACK).click();
						break;
					default:
						break;
					}

					lastActedOn = screen;
					lastActionMillis = System.currentTimeMillis();
				}

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		} finally {
			try {
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
			} catch (Exception e) {
				System.out.println("Could not restore implicit wait after backToSearch: " + e.getMessage());
			}
		}

		throw new TimeoutException("Could not get back to Home from cart within 60s (last screen: "
				+ currentScreen() + ")");
	}

	// Order matters: the PDP also has an EditText (pincode check), so search is identified by
	// its back Button plus the field, and the cart is checked before PDP because cross-sell
	// cards in the cart could reuse an "Add to cart" label.
	private Screen currentScreen() {

		try {
			if (!driver.findElements(CART_HEADER).isEmpty()) {
				return Screen.CART;
			}
			if (!driver.findElements(SEARCH_BACK).isEmpty() && !driver.findElements(SEARCH_FIELD).isEmpty()) {
				return Screen.SEARCH;
			}
			if (!driver.findElements(PDP_ADD_TO_CART).isEmpty()) {
				return Screen.PDP;
			}
			if (!driver.findElements(HOME_TAB).isEmpty()) {
				return Screen.HOME;
			}
		} catch (StaleElementReferenceException e) {
			// Mid-transition re-render - treat as unknown and poll again.
		}

		return Screen.UNKNOWN;
	}

	// PDP's back arrow is the first ImageView in the hierarchy (top-left), tapped by
	// coordinates. Only called once currentScreen() has confirmed we're on the PDP - on other
	// screens the first ImageView is something else entirely.
	private void tapPdpBack() {

		WebElement back = driver.findElement(
				AppiumBy.className("android.widget.ImageView"));

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
