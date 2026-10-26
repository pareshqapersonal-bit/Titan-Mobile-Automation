package com.titan.eyestage.v2.pom;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.titan.eyestage.v2.utils.CommonUtils;
import com.titan.eyestage.v2.utils.PaymentMethodMapper.PaymentMethod;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class PaymentPageElementsNew extends CommonUtils {

	AndroidDriver driver;

	public PaymentPageElementsNew(AndroidDriver driver) {
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	// Use a more flexible locator: text may vary or include extra whitespace/locale changes
	@AndroidFindBy(accessibility = "Google Pay")
	private WebElement GPay;
    
	@AndroidFindBy(accessibility = "Continue to Payment")
	private WebElement continuePaymentCTA;

	@AndroidFindBy(accessibility = "ICICI Bank")
	WebElement bankSelection;

	// Credit Card, Debit Card
	@FindBy(xpath = "//android.view.View[@content-desc=\"Credit / Debit Cards\"]/android.widget.ImageView[2]")
	WebElement creditDebitCardCheckbox;
	
	@FindBy(xpath = "//android.widget.ScrollView/android.widget.EditText[1]")
	WebElement cardNumberField;

	@FindBy(xpath = "//android.widget.ScrollView/android.widget.EditText[2]")
	WebElement cardExpiryField;

	@FindBy(xpath = "//android.widget.ScrollView/android.widget.EditText[3]")
	WebElement cardCVVField;

	@FindBy(xpath = "//android.widget.ScrollView/android.widget.EditText[4]")
	WebElement cardHolderNameField;

	// Wallet
	@FindBy(xpath = "//android.widget.CheckBox")
	WebElement walletCheckbox;

	@FindBy(xpath = "//android.view.View[@content-desc='Enter the amount you want to redeem']/following-sibling::android.widget.EditText")
	WebElement walletAmountField;

	@AndroidFindBy(accessibility = "Redeem")
	WebElement walletRedeemButton;

	@AndroidFindBy(xpath = "//android.view.View[starts-with(@content-desc, 'Total Amount')]")
	private WebElement totalAmount;

	@FindBy(xpath = "//android.widget.TextView[@resource-id='com.titan.eyecare:id/txt_btn_title' and @text='Wohoo!']")
	WebElement paymentConfirmation;

	@FindBy(id = "com.titan.eyecare:id/txt_app_rate_btn_later")
	private WebElement mayBLater;

	@FindBy(xpath = "//android.widget.LinearLayout[@resource-id='com.titan.eyecare:id/ll_search_bar']"
			+ "//android.widget.ImageView[@resource-id='com.titan.eyecare:id/img_back']")
	WebElement searchBackButton;

	@AndroidFindBy(xpath = "//android.view.View[starts-with(@content-desc,'TEQ-')]")
	WebElement OrderIDText;
	
	// The COD fee baked into the old exact-match content-desc (₹149.00) doesn't hold for
	// every cart - it varies by cart total, so the full-string match only happened to work
	// when a cart's fee was exactly that amount and timed out (30s, every other cart) once
	// confirmed against real failures. Matching on just the "Cash On Delivery" prefix drops
	// the fee-dependent part entirely.
	@FindBy(xpath = "//android.view.View[starts-with(@content-desc,\"Cash On Delivery\")]/android.widget.ImageView[2]")
	WebElement cashOnDeliveryCheckbox;
	
	@AndroidFindBy(accessibility = "Place Order")
	WebElement codPlaceOrderCTA;
	
	@AndroidFindBy(accessibility = "Confirm Order")
	WebElement confirmOrderButton;
	
	@AndroidFindBy(accessibility = "Order Confirmed.")
	WebElement orderConfirmationText;
	
	@FindBy(xpath = "//android.view.View[@content-desc=\"Netbanking\"]/android.widget.ImageView[2]")
	WebElement netBankingCheckbox;
	
	//Other UPI
	@AndroidFindBy(accessibility = "Other")
	WebElement OtherGpaySelect;
	
	@AndroidFindBy(xpath="//android.view.View[@content-desc=\"Enter your UPI ID\"]/following::android.widget.EditText")
	WebElement OtherGpayPayWithUPIField;
	
	@AndroidFindBy(accessibility = "Verify")
	WebElement VerifyButton;
	
	
	
	
	
	

	String path = null;

	private final By backButtonLocator = AppiumBy.id("com.titan.eyecare:id/img_toolbar_back");

	public void selectPaymentMethod(String paymentMethod) throws InterruptedException, IOException {

		PaymentMethod method = PaymentMethod.fromExcel(paymentMethod);

		switch (method) {

		case GOOGLE_PAY:

			try {
				clickPaymentMethodByText("Google Pay");
			} catch (Exception e) {
				try {
					visibilityOf(GPay);
					click(GPay);
				} catch (Exception ex) {
					System.out.println("Failed to select Google Pay: " + ex.getMessage());
					throw ex;
				}
			}

			System.out.println("Selected payment method: Google Pay");

			click(continuePaymentCTA);

			test().info("Initiating Razorpay payment");
			clickInWebviewIfPresentFlutter(
					By.xpath("//android.widget.TextView[@resource-id=\"cancel-btn\"]"),
					15);

			System.out.println("Payment confirmation text: " + getText(paymentConfirmation));
			assertEquals(getText(paymentConfirmation), "Wohoo!", "Payment confirmation text mismatch");

			click(paymentConfirmation);
			click(mayBLater);
			visibilityOf(OrderIDText);
			path = captureScreenshot("Order ID");
			test().info(getText(OrderIDText) + " is the OrderID");
			test().info("<a href='data:image/png;base64," + path + "' data-featherlight='image'><img src='data:image/png;base64," + path + "' style='width:200px;height:auto;cursor:pointer;'/></a>");
			assertTrue(OrderIDText.isDisplayed());
			clickBackFromOrderPage();

			break;

		case PHONEPE:
			// PhonePe locator not implemented yet
			break;

		case CREDIT_DEBIT_CARD:

			test().info("Selecting payment method");
			click(creditDebitCardCheckbox);

			sendKeys(cardNumberField, "4100280000001007");
			sendKeys(cardExpiryField, "12/35");
			sendKeys(cardCVVField, "123");
			sendKeys(cardHolderNameField, "Test User");
			driver.hideKeyboard();
			click(continuePaymentCTA);

			test().info("Initiating Razorpay payment");
			clickInWebviewIfPresentFlutter(By.xpath("//*[contains(@text,'Success')]"), 15);

			visibilityOf(orderConfirmationText);
			System.out.println("Payment confirmation text: " + orderConfirmationText.getAttribute("contentDescription")
					);
			assertEquals(orderConfirmationText.getAttribute("contentDescription")
					, "Order Confirmed.");

//			click(paymentConfirmation);
//			click(mayBLater);
		
			path = captureScreenshot("Order ID");
			test().info(getText(OrderIDText) + " is the OrderID");
			test().info("<a href='data:image/png;base64," + path + "' data-featherlight='image'><img src='data:image/png;base64," + path + "' style='width:200px;height:auto;cursor:pointer;'/></a>");
			assertTrue(OrderIDText.isDisplayed());

			break;

		case OTHER_UPI:
			
			click(OtherGpaySelect);
			sendKeys(OtherGpayPayWithUPIField, "success@upi");
			click(VerifyButton);
			driver.hideKeyboard();
			click(continuePaymentCTA);
			
			visibilityOf(orderConfirmationText);
			System.out.println("Payment confirmation text: " + orderConfirmationText.getAttribute("contentDescription")
					);
			assertEquals(orderConfirmationText.getAttribute("contentDescription")
					, "Order Confirmed.");

//			click(paymentConfirmation);
//			click(mayBLater);
		
			path = captureScreenshot("Order ID");
			test().info(getText(OrderIDText) + " is the OrderID");
			test().info("<a href='data:image/png;base64," + path + "' data-featherlight='image'><img src='data:image/png;base64," + path + "' style='width:200px;height:auto;cursor:pointer;'/></a>");
			assertTrue(OrderIDText.isDisplayed());
			
			break;

		case NET_BANKING:

			System.out.println("Selecting payment method: Net Banking");
			test().info("Selecting payment method");
			/*
			 * driver.findElement( AppiumBy.androidUIAutomator(
			 * "new UiScrollable(new UiSelector().scrollable(true))" +
			 * ".scrollIntoView(new UiSelector().text(\"Net Banking\"))")) .click();
			 */
            click(netBankingCheckbox);
			click(bankSelection);
			click(continuePaymentCTA);

			test().info("Initiating Razorpay payment");
			clickInWebviewIfPresentFlutter(By.xpath("//*[contains(@text,'Success')]"), 15);

			visibilityOf(orderConfirmationText);
			System.out.println("Payment confirmation text: " + orderConfirmationText.getAttribute("contentDescription")
					);
			assertEquals(orderConfirmationText.getAttribute("contentDescription")
					, "Order Confirmed.");

//			click(paymentConfirmation);
//			click(mayBLater);
		
			path = captureScreenshot("Order ID");
			test().info(getText(OrderIDText) + " is the OrderID");
			test().info("<a href='data:image/png;base64," + path + "' data-featherlight='image'><img src='data:image/png;base64," + path + "' style='width:200px;height:auto;cursor:pointer;'/></a>");
			assertTrue(OrderIDText.isDisplayed());

			break;

		case CASH_ON_DELIVERY:

			/*
			 * driver.findElement( AppiumBy.androidUIAutomator(
			 * "new UiScrollable(new UiSelector().scrollable(true))" +
			 * ".scrollIntoView(new UiSelector().text(\"Cash on Delivery\"))")) .click();
			 */
			
			click(cashOnDeliveryCheckbox);
			

			// Unlike GOOGLE_PAY/CREDIT_DEBIT_CARD/NET_BANKING below, selecting Cash on Delivery
			// goes straight to its own "Confirm Order" CTA - there's no separate "Continue to
			// Payment" step in between. Confirmed via a BrowserStack failure screenshot:
			// Confirm Order was already the sticky bottom button while this test still waited
			// the full 30s on continuePaymentCTA, which never appears for this method. Give it
			// a short chance in case that ever changes, but don't fail the flow when it's absent.
			if (!clickIfPresent(codPlaceOrderCTA, 5)) {
				System.out.println("Continue to Payment not shown for Cash on Delivery - proceeding directly to Confirm Order");
			}

			// Encircle check removed here (New-UI class) - same as the other "New" classes.
			// dismissEncirclePopupIfPresent();
			click(confirmOrderButton);

			System.out.println("Payment confirmation text: " + getText(paymentConfirmation));
			assertEquals(getText(paymentConfirmation), "Wohoo!", "Payment confirmation text mismatch");

			click(paymentConfirmation);
			click(mayBLater);
			visibilityOf(OrderIDText);
			path = captureScreenshot("Order ID");
			test().info(getText(OrderIDText) + " is the OrderID");
			test().info("<a href='data:image/png;base64," + path + "' data-featherlight='image'><img src='data:image/png;base64," + path + "' style='width:200px;height:auto;cursor:pointer;'/></a>");
			assertTrue(OrderIDText.isDisplayed());
			clickBackFromOrderPage();

			break;

		case WALLET:

			// Encircle check removed here (New-UI class) - same as the other "New" classes.
			// dismissEncirclePopupIfPresent();


			Dimension size = driver.manage().window().getSize();

			driver.executeScript("mobile: scrollGesture", Map.of(
			    "left", 0,
			    "top", (int) (size.height * 0.25),
			    "width", size.width,
			    "height", (int) (size.height * 0.50),
			    "direction", "down",
			    "percent", 0.5
			));
			// Wallet redemption can persist on the account's cart server-side across runs
			// (the session launches with noReset=true and reuses pooled test credentials -
			// see Base.launchApp), so a prior attempt that got this far before failing later
			// can leave the wallet already applied here, showing "You saved .../Remove"
			// instead of a checkbox. That leftover amount belongs to the earlier cart, not
			// this one - skipping the redeem steps on it left this order only part-covered
			// (₹1549 against a ₹7745 cart) and stuck on Continue to Payment. Remove it so
			// the wallet is always re-applied against the current cart total below.
			By walletRemoveLocator = By.xpath("//android.view.View[starts-with(@content-desc,'Titan Wallet — You saved')]/following::android.widget.ImageView[1]");
			
			if (isElementPresent(walletRemoveLocator)) {
				System.out.println("Wallet already applied (Remove link present) - removing before re-applying");
				click(driver.findElement(walletRemoveLocator));
				visibilityOf(walletCheckbox);
			}

			// Read only after any stale redemption is gone, so this is the full cart total
			// rather than one already reduced by the leftover wallet amount.
			/*
			 * driver.findElement(AppiumBy.androidUIAutomator(
			 * "new UiScrollable(new UiSelector().scrollable(true))" +
			 * ".scrollIntoView(new UiSelector().text(\"Total Amount\"))"));
			 */
			String amount = totalAmount.getAttribute("contentDescription");
			System.out.println("Total Amount: " + amount);
			 amount = totalAmount.getAttribute("contentDescription")
					 .replace("Total Amount", "")
					.replace("₹", "")
					.replace(",", "")
					.replace(".00", "")
					.trim();

			System.out.println(amount);
			System.out.println("amount is" + amount);
			test().info("Cart total before wallet = " + amount);

			/*
			 * driver.findElement(AppiumBy.androidUIAutomator(
			 * "new UiScrollable(new UiSelector().scrollable(true))" +
			 * ".scrollIntoView(new UiSelector().descriptionContains(\"Titan Wallet\"))" ));
			 */

			
			//scroll to wallet and click redeem
		
			
			click(walletCheckbox);
			sendKeys(walletAmountField, amount);
			click(walletRedeemButton);

			// Once the redeemed amount covers the whole cart total, the sticky bottom button
			// switches from "Continue to Payment" to "Confirm Order" and the UPI/Net Banking
			// sections collapse - the order is placed straight from here, with no Razorpay
			// step. Confirmed via BrowserStack failure screenshots ("You saved ₹1549.00" on a
			// ₹1549 cart, Confirm Order showing) while this test still waited the full 30s on
			// continuePaymentCTA.
			// Encircle check removed here (New-UI class) - same as the other "New" classes.
			// dismissEncirclePopupIfPresent();

			if (clickIfPresent(confirmOrderButton, 10)) {
				test().info("Wallet covered the full order total - confirmed order directly");
			} else {

				// Wallet balance short of the total: the remainder has to go through another
				// method, so the bar stays on "Continue to Payment" and needs the same
				// Razorpay-webview confirmation as GOOGLE_PAY/CREDIT_DEBIT_CARD/NET_BANKING above.
				click(continuePaymentCTA);

				// Encircle check removed here (New-UI class) - same as the other "New" classes.
				// dismissEncirclePopupIfPresent();

				test().info("Initiating Razorpay payment (Wallet + UPI)");
				clickInWebviewIfPresent(
						By.xpath("//android.widget.TextView[@resource-id=\"cancel-btn\"]"),
						15);
			}

			visibilityOf(orderConfirmationText);
			System.out.println("Payment confirmation text: " + orderConfirmationText.getAttribute("contentDescription")
					);
			assertEquals(orderConfirmationText.getAttribute("contentDescription")
					, "Order Confirmed.");

//			click(paymentConfirmation);
//			click(mayBLater);
		
			path = captureScreenshot("Order ID");
			test().info(getText(OrderIDText) + " is the OrderID");
			test().info("<a href='data:image/png;base64," + path + "' data-featherlight='image'><img src='data:image/png;base64," + path + "' style='width:200px;height:auto;cursor:pointer;'/></a>");
			assertTrue(OrderIDText.isDisplayed());
		//	clickBackFromOrderPage();

			break;

		default:
			throw new IllegalArgumentException(
					"Unsupported payment method: " + paymentMethod);
		}
	}

	// Razorpay opens its own WEBVIEW context for card/net-banking/GPay confirmation, but it
	// doesn't always appear (some flows auto-confirm without it). The legacy (working) flow
	// confirms this click is never actually required: it probes getContextHandles() the same
	// way but never switches context, so its find always runs against NATIVE_APP - yet the
	// purchase still completes, because the real success signal (paymentConfirmation, a native
	// com.titan.eyecare:id/txt_btn_title element) is rendered natively once Razorpay resolves on
	// its own. A real driver.context(webviewContext) switch was tried here previously and
	// correlated with the session dying mid-test (chromedriver bridge on real BrowserStack
	// devices), so this stays on NATIVE_APP like the working flow instead of crossing into it.
	private void clickInWebviewIfPresent(By locator, int timeoutSeconds) {

		String webviewContext;

		try {
			webviewContext = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
					.until(ignored -> driver.getContextHandles().stream()
							.filter(c -> c.contains("WEBVIEW"))
							.findFirst()
							.orElse(null));
		} catch (TimeoutException e) {
			System.out.println("No WEBVIEW context appeared within " + timeoutSeconds + "s, skipping: " + locator);
			return;
		}

		System.out.println("WEBVIEW context present (" + webviewContext + "), probing from NATIVE_APP: " + locator);

		List<WebElement> matches = driver.findElements(locator);
		System.out.println("Count = " + matches.size());

		if (!matches.isEmpty()) {
			matches.get(0).click();
			System.out.println("Clicked webview element: " + locator);
		} else {
			System.out.println("Webview element not present, skipping click: " + locator);
		}
	}

	// Scrolls to a payment method by visible text (uses UiScrollable) and clicks it.
	// Falls back to finding an element containing the text if UiScrollable fails.
	private void clickPaymentMethodByText(String visibleText) {
		try {
			driver.findElement(
					AppiumBy.androidUIAutomator(
							"new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().textContains(\""
									+ visibleText + "\"))"))
					.click();
			return;
		} catch (Exception e) {
			List<WebElement> elems = driver.findElements(AppiumBy.xpath("//*[contains(@text,'" + visibleText + "')]"));
			if (!elems.isEmpty()) {
				click(elems.get(0));
				return;
			}
			throw e;
		}
	}

	public void clickBackFromOrderPage() {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(backButtonLocator));

		backButton.click();
		click(searchBackButton);
	}
	
	
	
	
	private void clickInWebviewIfPresentFlutter(By locator, int timeoutSeconds) {
	    try {
	        driver.context("NATIVE_APP");

	        WebDriverWait wait = new WebDriverWait(
	                driver, Duration.ofSeconds(timeoutSeconds));

	        try {
	            WebElement element = wait.until(
	                    ExpectedConditions.elementToBeClickable(locator));

	            element.click();
	            System.out.println("Clicked element in NATIVE_APP: " + locator);
	            return;

	        } catch (TimeoutException e) {
	            System.out.println("Element not found in NATIVE_APP: " + locator);
	        }

	        String webviewContext = new WebDriverWait(
	                driver, Duration.ofSeconds(timeoutSeconds))
	                .until(d -> driver.getContextHandles().stream()
	                        .filter(c -> c.contains("WEBVIEW"))
	                        .findFirst()
	                        .orElse(null));

	        driver.context(webviewContext);

	        WebElement element = wait.until(
	                ExpectedConditions.elementToBeClickable(locator));

	        element.click();
	        System.out.println("Clicked element in " + webviewContext + ": " + locator);

	    } catch (TimeoutException e) {
	        System.out.println("Element or WEBVIEW context not found: " + locator);

	    } finally {
	        driver.context("NATIVE_APP");
	    }
	}
}
