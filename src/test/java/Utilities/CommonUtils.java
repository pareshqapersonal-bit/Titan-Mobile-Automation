package Utilities;

import java.time.Duration;
import java.util.Arrays;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;

import com.titan.eyestage.Base;

import io.appium.java_client.android.AndroidDriver;

public class CommonUtils extends Base {
	
    public void click(WebElement element) {

        WebDriverWait wait =
                new WebDriverWait(driver,
                        Duration.ofSeconds(30));

        wait.until(
                ExpectedConditions.elementToBeClickable(element))
                .click();
    }
    
    //Element visibility utility
    public void visibilityOf(WebElement element) {

        WebDriverWait wait =
                new WebDriverWait(driver,
                        Duration.ofSeconds(30));

        wait.until(
                ExpectedConditions.visibilityOf(element));
                
    }

    public void sendKeys(WebElement element,
                         String value) {

        WebDriverWait wait =
                new WebDriverWait(driver,
                        Duration.ofSeconds(30));

        wait.until(
                ExpectedConditions.visibilityOf(element));

        element.clear();
        element.sendKeys(value);
    }
    //Enter
    public void enter(WebElement element)
    {
    	 WebDriverWait wait =
                 new WebDriverWait(driver,
                         Duration.ofSeconds(30));
    	  wait.until(
                 ExpectedConditions.visibilityOf(element))
                 .sendKeys(Keys.ENTER);
    }

    public String getText(WebElement element) {

        WebDriverWait wait =
                new WebDriverWait(driver,
                        Duration.ofSeconds(30));

        return wait.until(
                ExpectedConditions.visibilityOf(element))
                .getText();
    }
    
    //Testcase details

 // Testcase details
 // Testcase details
    public static String getTestData(ITestResult result) {

        Object[] parameters = result.getParameters();

        if (parameters.length == 0) {
            return "";
        }

        /*
         * purchaseData:
         *
         * [0] TestCaseID
         * [1] List<CartProduct>
         * [2] PaymentMethod
         * [3] LoginUser
         */
        if (parameters.length == 4
                && parameters[1] instanceof java.util.List<?>) {

            String testCaseId =
                    String.valueOf(parameters[0]);

            String paymentMethod =
                    String.valueOf(parameters[2]);

            @SuppressWarnings("unchecked")
            java.util.List<models.CartProduct> products =
                    (java.util.List<models.CartProduct>) parameters[1];

            int categoryCount = products.size();

            String categoryText =
                    categoryCount == 1
                            ? "1 Category"
                            : categoryCount + " Categories";

            return " - TC" + testCaseId
                    + " - " + paymentMethod
                    + " - " + categoryText;
        }

        // Existing behavior for other DataProviders
        return " | " + Arrays.toString(parameters);
    }
    
    //swipe function
    public void swipeLeft()
    {
        Dimension size = driver.manage().window().getSize();

        int startX = (int)(size.width * 0.8);
        int endX   = (int)(size.width * 0.2);
        
        System.out.println("Start x is"+startX);
        System.out.println("end x is"+endX);

        int y = size.height / 2;
        System.out.println("y is"+y);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH,"finger");

        Sequence swipe = new Sequence(finger,1);

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

        driver.perform(Arrays.asList(swipe));
    }
    
    
}
