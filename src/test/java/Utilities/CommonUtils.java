package Utilities;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.titan.eyestage.Base;

public class CommonUtils extends Base {

    public void click(WebElement element) {

        WebDriverWait wait =
                new WebDriverWait(driver,
                        Duration.ofSeconds(30));

        wait.until(
                ExpectedConditions.elementToBeClickable(element))
                .click();
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

    public String getText(WebElement element) {

        WebDriverWait wait =
                new WebDriverWait(driver,
                        Duration.ofSeconds(30));

        return wait.until(
                ExpectedConditions.visibilityOf(element))
                .getText();
    }
}
