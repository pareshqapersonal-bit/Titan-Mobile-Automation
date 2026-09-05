package com.titan.eyestage.v2.utils;

import java.time.Duration;
import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;

import com.titan.eyestage.v2.Base;

public class CommonUtils extends Base {

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

        newWait()
                .until(ExpectedConditions.elementToBeClickable(element))
                .click();
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
    public static String getTestData(ITestResult result) {

        Object[] p = result.getParameters();

        if (p.length < 4) {
            return p.length == 0 ? "" : " | " + Arrays.toString(p);
        }

        StringBuilder sb = new StringBuilder(" | Device=").append(p[2]).append(", OS=").append(p[3]);

        if (p.length >= 7) {
            sb.append(", TestCase=").append(p[4])
              .append(", Products=").append(p[5])
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
