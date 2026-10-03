package com.titan.eyestage.v2.utils;

import java.time.Duration;
import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;

import com.titan.eyestage.v2.Base;
import io.appium.java_client.AppiumBy;

public class CommonUtils extends Base {

    // Shared across every POM (PageFactory.initElements in each subclass's constructor
    // decorates inherited @FindBy fields too) so isEncirclepopupDisplayed()/
    // dismissEncirclePopupIfPresent() below can dismiss the popup regardless of which
    // screen it resurfaces on.
    @FindBy(id = "com.titan.eyecare:id/btn_negative")
    protected WebElement laterCTA;

    // A WebDriverWait's polling loop only rides out NotFoundException by default - any other
    // WebDriverException raised while evaluating the condition (e.g. "Session not started or
    // terminated" / "unexpected driver response" from a momentarily unready Appium/UIAutomator2
    // backend, seen right after a context switch or under real-device network hiccups) escapes
    // the loop immediately instead of being retried within the wait's own budget. Ignoring
    // WebDriverException here keeps every poll inside FluentWait's existing timeout/interval,
    // so a transient one clears on a later poll instead of failing the whole step outright; a
    // truly dead session still surfaces the same way it does today, just as this wait's own
    // TimeoutException once the 30s budget actually runs out.
    private FluentWait<WebDriver> newWait() {
        return new WebDriverWait(driver(), Duration.ofSeconds(30))
                .ignoring(WebDriverException.class);
    }

    public void click(WebElement element) {
        clickRetryingPopup(element, true);
    }

    // The Encircle popup can resurface right before literally any click in the app - login,
    // search, payment, and (discovered after patching those three individually) inside
    // category-specific add-to-cart flows too. Patching each call site as it's discovered
    // doesn't scale, so this is the single choke point every click goes through: on a
    // timeout, check once for the popup, dismiss it, and retry the click exactly once.
    // allowPopupRetry=false on the retry (and for laterCTA's own click below) stops this from
    // recursing if the popup check or its own click ever times out too.
    private void clickRetryingPopup(WebElement element, boolean allowPopupRetry) {

        try {
            newWait()
                    .until(ExpectedConditions.elementToBeClickable(element))
                    .click();
        } catch (TimeoutException e) {

            if (!allowPopupRetry || !isEncirclepopupDisplayed()) {
                throw e;
            }

            System.out.println("Encircle popup found after click timeout, dismissing and retrying: " + element);
            clickRetryingPopup(laterCTA, false);
            clickRetryingPopup(element, false);
        }
    }

    // The Encircle enrollment popup can resurface at multiple points in a session (login,
    // product search, checkout) and silently blocks whatever element the next step expects,
    // producing a "waiting for element to be clickable, but the element null" timeout instead
    // of a clear error. Centralized here so every POM checks/dismisses it the same way.
    //
    // findElements() otherwise inherits the driver's 30s implicit wait (set in Base.opn_app),
    // so every "not showing" check - the common case - would silently cost 30s. Drop it to
    // near-zero for this one lookup, then restore it so nothing else loses its implicit wait.
    public boolean isEncirclepopupDisplayed() {

        driver().manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        int count;

        try {
            count = driver().findElements(
                    AppiumBy.id("com.titan.eyecare:id/btn_negative"))
                    .size();
        } finally {
            try {
                driver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
            } catch (Exception e) {
                System.out.println("Could not restore implicit wait after popup check: " + e.getMessage());
            }
        }

        System.out.println("Encircle popup count = " + count);

        return count > 0;
    }

    public void dismissEncirclePopupIfPresent() {

        if (isEncirclepopupDisplayed()) {
            click(laterCTA);
        }
    }

    // Some flows show a CTA conditionally (e.g. Cash on Delivery goes straight to its own
    // confirm button with no "Continue to Payment" step in between - confirmed via a
    // BrowserStack failure screenshot where Confirm Order was already the sticky bottom
    // button while a 30s click() on Continue to Payment was still timing out). Lets a caller
    // give an optional CTA a short chance without paying/failing on the full click() budget
    // when it legitimately never appears.
    public boolean clickIfPresent(WebElement element, int timeoutSeconds) {

        try {
            new WebDriverWait(driver(), Duration.ofSeconds(timeoutSeconds))
                    .ignoring(WebDriverException.class)
                    .until(ExpectedConditions.elementToBeClickable(element))
                    .click();
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // Mirrors isEncirclepopupDisplayed()'s pattern for an arbitrary locator: a quick existence
    // check that skips the driver's 30s implicit wait (set in Base.opn_app) for the common
    // "not present" case, instead of silently costing 30s every time.
    public boolean isElementPresent(By locator) {

        driver().manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        int count;

        try {
            count = driver().findElements(locator).size();
        } finally {
            try {
                driver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
            } catch (Exception e) {
                System.out.println("Could not restore implicit wait after presence check: " + e.getMessage());
            }
        }

        return count > 0;
    }

    // Element visibility utility
    public void visibilityOf(WebElement element) {

        newWait().until(ExpectedConditions.visibilityOf(element));
    }

    public void sendKeys(WebElement element,
                         String value) {

        newWait().until(ExpectedConditions.visibilityOf(element));

        element.clear();
        element.sendKeys(value);
    }

    // Re-locates by locator on every attempt instead of reusing a cached
    // WebElement, so a mid-screen-transition re-render (element goes stale
    // between find and interact - seen intermittently on real devices)
    // doesn't fail the step outright.
    public void sendKeysToLocator(By locator, String value) {

        FluentWait<WebDriver> wait = newWait();

        StaleElementReferenceException lastFailure = null;

        for (int attempt = 1; attempt <= 3; attempt++) {

            try {
                WebElement element =
                        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

                element.clear();
                element.sendKeys(value);
                return;

            } catch (StaleElementReferenceException e) {
                lastFailure = e;
                System.out.println("Stale element on attempt " + attempt + ", retrying: " + locator);
            }
        }

        throw lastFailure;
    }

    // Enter
    public void enter(WebElement element) {

        newWait()
                .until(ExpectedConditions.visibilityOf(element))
                .sendKeys(Keys.ENTER);
    }

    public String getText(WebElement element) {

        return newWait()
                .until(ExpectedConditions.visibilityOf(element))
                .getText();
    }

    // Testcase details - built as named fields rather than a raw Arrays.toString() dump so
    // every report entry clearly shows which device/case it belongs to. Every v2 data provider
    // (loginDevices, purchaseDevices) shares the same {mobileNumber, password, deviceName,
    // osVersion, ...} shape; indices 0/1 are login credentials and are deliberately never
    // included here - the old raw dump printed the plaintext password into the HTML report.
    //
    // Products (p[5]) is a List<CartProduct> that can carry every category in one row (Frame,
    // Eyeglass, Sunglass, ContactLens, ...) - dumping its full toString() here made this string
    // the ExtentTest node NAME (Extent's sidebar/test-list entries), which turned into a single
    // 250+ char line that wrapped over neighbouring entries in the report. Only the item count
    // goes into the name; PurchaseTest.Steps logs the full per-category/SKU breakdown as a step
    // inside the test instead, where verbose detail belongs.
    public static String getTestData(ITestResult result) {

        Object[] p = result.getParameters();

        if (p.length < 4) {
            return p.length == 0 ? "" : " | " + Arrays.toString(p);
        }

        StringBuilder sb = new StringBuilder(" | Device=").append(p[2]).append(", OS=").append(p[3]);

        if (p.length >= 7) {
            String productsSummary = (p[5] instanceof java.util.Collection)
                    ? ((java.util.Collection<?>) p[5]).size() + " item(s)"
                    : String.valueOf(p[5]);

            sb.append(", TestCase=").append(p[4])
              .append(", Products=").append(productsSummary)
              .append(", PaymentMethod=").append(p[6]);
        }

        return sb.toString();
    }

    // swipe function
    public void swipeLeft() {
        Dimension size = driver().manage().window().getSize();

        int startX = (int) (size.width * 0.8);
        int endX   = (int) (size.width * 0.2);

        System.out.println("Start x is" + startX);
        System.out.println("end x is" + endX);

        int y = size.height / 2;
        System.out.println("y is" + y);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

        Sequence swipe = new Sequence(finger, 1);

        swipe.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(),
                startX,
                y));

        swipe.addAction(finger.createPointerDown(0));

        swipe.addAction(finger.createPointerMove(Duration.ofMillis(700),
                PointerInput.Origin.viewport(),
                endX,
                y));

        swipe.addAction(finger.createPointerUp(0));

        driver().perform(Arrays.asList(swipe));
    }

}
