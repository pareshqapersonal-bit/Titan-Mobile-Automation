package Utilities;

import java.time.Duration;
import java.util.Arrays;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
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
    public static String getTestData(ITestResult result) {

        if (result.getParameters().length == 0) {
            return "";
        }

        return " | " + Arrays.toString(result.getParameters());
    }
}
