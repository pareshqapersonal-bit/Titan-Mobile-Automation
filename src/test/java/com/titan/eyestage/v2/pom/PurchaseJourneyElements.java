package com.titan.eyestage.v2.pom;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class PurchaseJourneyElements extends CommonUtils {

	AndroidDriver driver;

	public PurchaseJourneyElements(AndroidDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "com.titan.eyecare:id/txt_btn_title")
	WebElement proceedToCheckout;

	@FindBy(xpath = "//android.widget.TextView[@resource-id='com.titan.eyecare:id/txt_btn_title' and starts-with(@text,'Proceed to Pay')]")
	WebElement proceedToPay;

	@FindBy(xpath = "//android.widget.TextView[@resource-id='com.titan.eyecare:id/txt_btn_title' and @text='Continue to Payment']")
	private WebElement continuePaymentCTA;

	@FindBy(id = "com.titan.eyecare:id/txt_btn_title")
	WebElement confirmAddress;

	@FindBy(id = "com.titan.eyecare:id/edt_first_name")
	WebElement firstName;

	@FindBy(id = "com.titan.eyecare:id/edt_last_name")
	WebElement lastName;

	@FindBy(id = "com.titan.eyecare:id/edt_mobile")
	WebElement mobileNumber;

	@FindBy(xpath = "//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_btn_title\"]")
	WebElement saveAddress;

	@FindBy(id = "com.titan.eyecare:id/txt_btn_title")
	WebElement continueOnAddress;

	private final By selectAddressLocator =
			AppiumBy.xpath("//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_btn_title\" and @text=\"Select Address\"]");

	private final By addNewAddressLocator =
			AppiumBy.xpath("//android.widget.TextView[@text=\"Add New Address\"]");

	String path = null;

	public void proceedToCheckout() throws IOException {

		test().info("Proceeding to checkout");

		click(proceedToCheckout);

		test().pass("Address page opened");

		visibilityOf(proceedToPay);

		System.out.println("Address page loaded");

		path = captureScreenshot("Address page");
		test().info("Address page displayed");
		test().info("<a href='data:image/png;base64," + path + "' data-featherlight='image'><img src='data:image/png;base64," + path + "' style='width:200px;height:auto;cursor:pointer;'/></a>");

		assertTrue(
				proceedToPay.isDisplayed(),
				"Address page not displayed");
	}

	public void proceedToPay() throws IOException {

		System.out.println("Proceeding to pay");
		test().info("Selecting Addresses");

		List<WebElement> selectAddressBtn = driver.findElements(selectAddressLocator);

		if (!selectAddressBtn.isEmpty() && selectAddressBtn.get(0).isDisplayed()) {

			click(selectAddressBtn.get(0));

			List<WebElement> addNewAddressBtn = driver.findElements(addNewAddressLocator);

			if (!addNewAddressBtn.isEmpty() && addNewAddressBtn.get(0).isDisplayed()) {
				click(addNewAddressBtn.get(0));
				click(confirmAddress);
				sendKeys(firstName, "Test");
				sendKeys(lastName, "User");
				sendKeys(mobileNumber, "9876543210");
				click(saveAddress);
				click(continueOnAddress);
			} else {
				System.out.println("No saved address found and 'Add New Address' not shown - address screen may differ from expected flow");
			}
		} else {
			System.out.println("'Select Address' not shown - address already selected/defaulted, proceeding directly");
		}

		click(proceedToPay);

		test().pass("Selected Addresses");

		visibilityOf(continuePaymentCTA);

		System.out.println("Payment page loaded");

		path = captureScreenshot("Payment page");
		test().info("Payment page displayed");
		test().info("<a href='data:image/png;base64," + path + "' data-featherlight='image'><img src='data:image/png;base64," + path + "' style='width:200px;height:auto;cursor:pointer;'/></a>");

		assertTrue(
				continuePaymentCTA.isDisplayed(),
				"Payment page not displayed");
	}
}
