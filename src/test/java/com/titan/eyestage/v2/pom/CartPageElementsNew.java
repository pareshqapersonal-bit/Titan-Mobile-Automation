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
	@FindBy(xpath = "//*[starts-with(@content-desc,'SUGGESTIONS')]")
	WebElement productSelection;

	@FindBy(xpath = "//android.view.View[@content-desc=\"Add to cart\"]")
	WebElement buyNowCTA;

	@FindBy(xpath = "//android.view.View[@content-desc=\"Continue Shopping\"]")
	WebElement continueShoppingButton;

	@FindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.widget.ImageView")
	WebElement cartBackButton;

	@FindBy(xpath = "//android.widget.Button")
	WebElement searchBackButton;

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
		// actually change before trusting productSelection.
		String previousSuggestionText = currentSuggestionText();

		sendKeys(searchField, sku);
		waitForSuggestionToRefresh(previousSuggestionText);

		click(productSelection);
	}

	private String currentSuggestionText() {

		List<WebElement> matches = driver.findElements(AppiumBy.xpath("//*[starts-with(@content-desc,'SUGGESTIONS')]"));
		return matches.isEmpty() ? null : matches.get(0).getAttribute("contentDescription");
	}

	// Polls for the SUGGESTIONS row's text to differ from whatever was showing before this
	// SKU was typed. Bounded rather than indefinite - Frame and Eyeglass share a SKU in
	// this suite's test data, so a legitimately-unchanged suggestion (same product,
	// searched again) must not block forever; by the time real debounce/network latency
	// would have settled (observed well under 2s in practice elsewhere), give up waiting
	// and proceed with whatever's there.
	private void waitForSuggestionToRefresh(String previousSuggestionText) {

		if (previousSuggestionText == null) {
			visibilityOf(productSelection);
			return;
		}

		long deadlineMillis = System.currentTimeMillis() + Duration.ofSeconds(8).toMillis();

		while (System.currentTimeMillis() < deadlineMillis) {

			String currentSuggestionTextNow = currentSuggestionText();

			if (currentSuggestionTextNow != null && !currentSuggestionTextNow.equals(previousSuggestionText)) {
				return;
			}

			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}

		System.out.println("SUGGESTIONS row text unchanged after 8s settle window - "
				+ "proceeding (likely a legitimately repeated SKU)");
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

				if (!driver.findElements(AppiumBy.className("android.widget.ImageView")).isEmpty()) {
					return true;
				}

				if (!driver.findElements(AppiumBy.className("android.widget.Button")).isEmpty()) {
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
