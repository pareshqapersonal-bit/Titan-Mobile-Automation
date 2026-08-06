package POM;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;

public class ContactLensCart extends CommonUtils {

    AndroidDriver driver;
    LoginElements le;

    public ContactLensCart(AndroidDriver driver) {

        this.driver = driver;
        PageFactory.initElements(driver, this);

        le = new LoginElements(driver);
    }

    // Headers
    @FindBy(id = "com.titan.eyecare:id/txt_pdp_right_eye_header")
    WebElement rightEyeHeader;

    // Right Eye Dropdowns
    @FindBy(id = "com.titan.eyecare:id/edt_sph_right")
    WebElement rightSPH;

    @FindBy(id = "com.titan.eyecare:id/edt_cyl_right")
    WebElement rightCYL;

    @FindBy(id = "com.titan.eyecare:id/edt_axis_right")
    WebElement rightAXIS;

    @FindBy(id = "com.titan.eyecare:id/edt_quantity_right")
    WebElement rightQty;

    // Left Eye Dropdowns
    @FindBy(id = "com.titan.eyecare:id/edt_sph_left")
    WebElement leftSPH;

    @FindBy(id = "com.titan.eyecare:id/edt_cyl_left")
    WebElement leftCYL;

    @FindBy(id = "com.titan.eyecare:id/edt_axis_left")
    WebElement leftAXIS;

    @FindBy(id = "com.titan.eyecare:id/edt_quantity_left")
    WebElement leftQty;

    @FindBy(id = "com.titan.eyecare:id/txt_btn_title")
    WebElement buyNowCTA;

    public void addToCart(CartProduct product) {

    	System.out.println("Inside Adding Contact Lens Product to Cart");
        test.info("Adding Contact Lens Product");

        scrollToText("Power(SPH)");

        // Right Eye
        selectIfPresent(rightSPH, "-0.75");
        // selectIfPresent(rightCYL, "-0.75");
        // selectIfPresent(rightAXIS, "180");
        scrollToText("Quantity");
         selectIfPresent(rightQty, "1");

        click(buyNowCTA);

        if (le.isLoginPageDisplayed()) {

            le.shortLogin("8586565656", "254265");

            // Need to reselect values after login

          
            scrollToText("Power(SPH)");

            selectIfPresent(rightSPH, "-0.25");
            // selectIfPresent(rightCYL, "-0.75");
            // selectIfPresent(rightAXIS, "180");
            scrollToText("Quantity");
             selectIfPresent(rightQty, "1");

            click(buyNowCTA);
        }

        test.pass("Contact Lens added successfully");
    }

	/*
	 * private void scrollToRightEye() {
	 * System.out.println("Scrolling to Right Eye Section");
	 * visibilityOf(buyNowCTA); driver.findElement( AppiumBy.androidUIAutomator(
	 * "new UiScrollable(new UiSelector().scrollable(true))" +
	 * ".scrollIntoView(new UiSelector().text(\"Right Eye\"))" )).click(); }
	 */
    
    //scroll function 
    public void scrollToText(String text) {

        driver.findElement(
            AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().text(\"" + text + "\"))"
            )
        );
    }

    /**
     * Generic helper.
     */
    private void selectIfPresent(WebElement dropdown, String value) {

        if (value == null || value.trim().isEmpty())
            return;

        if (driver.findElements(By.id(dropdown.getAttribute("resource-id"))).size() > 0) {

            click(dropdown);

            click(driver.findElement(
                    AppiumBy.androidUIAutomator(
                            "new UiSelector().text(\"" + value + "\")")));

            test.info("Selected : " + value);
        }
    }

}