package com.titan.eyestage.v2.pom;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.titan.eyestage.v2.models.LoginCredentials;
import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class LoginElements extends CommonUtils {

	AndroidDriver driver = null;

	public LoginElements(AndroidDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath="//android.widget.RelativeLayout[@resource-id=\"com.titan.eyecare:id/rl_toolbar_app\"]/android.widget.ImageView")
	WebElement Drawer;

	@FindBy(xpath="//android.widget.LinearLayout[@resource-id=\"com.titan.eyecare:id/ll_without_login\"]/android.widget.RelativeLayout")
	WebElement loginIcon;

	@FindBy(xpath="//android.widget.EditText[@resource-id=\"com.titan.eyecare:id/edt_email_phone\"]")
	WebElement MobileTextField;

	@FindBy(xpath="//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_btn_title\"]")
	WebElement loginCTA;

	@FindBy(xpath="//android.widget.LinearLayout[@resource-id=\"com.titan.eyecare:id/ll_without_login\"]/android.widget.RelativeLayout")
	WebElement otpField;

	@FindBy(id="com.titan.eyecare:id/txt_btn_title")
	WebElement loginsubmission;

	@FindBy(id="android:id/button1")
	WebElement logoutConfirmation;

	@FindBy(id="com.titan.eyecare:id/txt_username")
	WebElement profileName;

	@FindBy(xpath = "//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_login_label\"]")
	WebElement loginPageTitleText;

	@FindBy(id="com.android.permissioncontroller:id/permission_allow_foreground_only_button")
	WebElement locationperm;

	@FindBy(id="com.android.permissioncontroller:id/permission_deny_button")
	WebElement notPerm;

	@FindBy(id = "com.titan.eyecare:id/txt_btn_title")
	WebElement buyNowCTA;

	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

	String path = null;

	public void testexecution(String number, String pass) throws InterruptedException, IOException {
		test().info("Login process started");
		click(Drawer);

		click(loginIcon);
		sendKeys(MobileTextField, number);
		click(loginCTA);

		By passwordFieldLocator = AppiumBy.className("android.widget.EditText");

		System.out.println("EditText count = " + driver.findElements(passwordFieldLocator).size());

		sendKeysToLocator(passwordFieldLocator, pass);
		click(loginsubmission);
		click(Drawer);
		path = captureScreenshot("User_Details");

		String userPRofileName = wait.until(ExpectedConditions.visibilityOf(profileName)).getText();
		System.out.println("My name is" + userPRofileName);
		test().info(userPRofileName + "has successfully logged in");
		visibilityOf(profileName);
		test().info("<a href='data:image/png;base64," + path + "' data-featherlight='image'><img src='data:image/png;base64," + path + "' style='width:200px;height:auto;cursor:pointer;'/></a>");
		assertTrue(profileName.isDisplayed(), "User login failed");

	}

	// Logout execution
	public void logoutSteps() throws IOException {
		click(Drawer);
		test().info("Log out process started");
		driver.findElement(
			    AppiumBy.androidUIAutomator(
			        "new UiScrollable(new UiSelector().scrollable(true))" +
			        ".scrollIntoView(new UiSelector().text(\"Logout\"))"
			    )
			).click();

		click(logoutConfirmation);
		path = captureScreenshot("User_Details");
		test().info("User has successfully logged out");
		test().info("<a href='data:image/png;base64," + path + "' data-featherlight='image'><img src='data:image/png;base64," + path + "' style='width:200px;height:auto;cursor:pointer;'/></a>");

	}

	// small login function without navigation
	public void shortLogin(String number, String pass) {
	    sendKeys(MobileTextField, number);

	    click(loginCTA);

	    By editTextLocator =
	            AppiumBy.className("android.widget.EditText");

	    WebDriverWait wait =
	            new WebDriverWait(driver, Duration.ofSeconds(30));

	    WebElement passwordField = wait.until(
	            ExpectedConditions.visibilityOfElementLocated(editTextLocator)
	    );

	    System.out.println("Password EditText is visible");

	    passwordField.sendKeys(pass);

	    click(loginsubmission);
	}

	public void shortLogin(LoginCredentials credentials) {
		System.out.println("Logging in with credentials: " + credentials.getMobileNumber());
	    shortLogin(
	            credentials.getMobileNumber(),
	            credentials.getPassword()
	    );
	}

	public void permissionPopup() {

	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    try {

	        // Location permission
	        List<WebElement> foregroundAllow = driver.findElements(
	                AppiumBy.id(
	                    "com.android.permissioncontroller:id/permission_allow_foreground_only_button"));

	        if (!foregroundAllow.isEmpty()) {

	            wait.until(ExpectedConditions.elementToBeClickable(
	                    foregroundAllow.get(0))).click();

	            System.out.println("Clicked: While using the app");
	        }

	        // Notification permission
	        List<WebElement> notificationAllow = driver.findElements(
	                AppiumBy.id(
	                    "com.android.permissioncontroller:id/permission_allow_button"));

	        if (!notificationAllow.isEmpty()) {

	            wait.until(ExpectedConditions.elementToBeClickable(
	                    notificationAllow.get(0))).click();

	            System.out.println("Clicked: Allow notification");
	        }

	    } catch (Exception e) {

	        System.out.println("Permission popup handling completed.");
	    }
	}

	public boolean isLoginPageDisplayed() {
	    System.out.println("Checking Login Page");

	    int count = driver.findElements(
	            AppiumBy.id("com.titan.eyecare:id/txt_login_label"))
	            .size();

	    System.out.println("Count = " + count);

	    return count > 0;
	}

}
