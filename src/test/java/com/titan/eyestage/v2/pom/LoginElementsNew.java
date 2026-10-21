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

public class LoginElementsNew extends CommonUtils {

	AndroidDriver driver = null;

	public LoginElementsNew(AndroidDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	// Flutter app (4.x): bottom-nav tab, replaces the old toolbar drawer
	@FindBy(xpath="//android.widget.ImageView[@content-desc=\"Account\"]")
	WebElement accountTab;

	@FindBy(xpath="//android.view.View[@content-desc=\"Login\"]")
	WebElement loginIcon;

	@FindBy(xpath="//android.widget.EditText")
	WebElement MobileTextField;

	@FindBy(xpath="//android.view.View[@content-desc=\"Login\"]")
	WebElement loginCTA;

	@FindBy(id="com.titan.eyecare:id/txt_btn_title")
	WebElement loginsubmission;

	// "Log Out" button inside the confirmation dialog
	@FindBy(xpath="//android.view.View[@content-desc=\"Log Out\"]")
	WebElement logoutConfirmation;

	@FindBy(xpath="//android.widget.ImageView[@content-desc=\"My Profile\"]")
	WebElement myProfile;

	@FindBy(xpath = "//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_login_label\"]")
	WebElement loginPageTitleText;

	@FindBy(id="com.android.permissioncontroller:id/permission_allow_foreground_only_button")
	WebElement locationperm;

	@FindBy(id="com.android.permissioncontroller:id/permission_deny_button")
	WebElement notPerm;

	@FindBy(id = "com.titan.eyecare:id/txt_btn_title")
	WebElement buyNowCTA;
	
	@FindBy(xpath="//android.view.View[@content-desc=\"Send OTP via SMS\"]")
	WebElement sendOTPviaSMS;
	
	/*
	 * WebElement otpInput = driver.findElement(
	 * AppiumBy.className("android.widget.EditText") );
	 * 
	 * otpInput.sendKeys(stageOtp);
	 */
	
	@FindBy(className = "android.widget.EditText")
	 WebElement otpInput;
	
	@FindBy(xpath="//android.view.View[@content-desc=\"Verify\"]")
	WebElement verifyCTA;
	
	
	
	

	String path = null;

	public void testexecution(String number, String pass) throws InterruptedException, IOException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		test().info("Login process started");
		click(accountTab);

		// noReset keeps the previous session, so log out first if a user is still signed in
		if (!driver.findElements(AppiumBy.accessibilityId("My Profile")).isEmpty()) {
			test().info("User already logged in - logging out before login");
			logoutSteps();
			click(accountTab);
		}

		click(loginIcon);

		sendKeys(MobileTextField, number);
		click(sendOTPviaSMS);

		// after "Send OTP" the phone field is no longer an EditText, so this resolves to the OTP box
		sendKeys(otpInput, pass);
		click(verifyCTA);

		  wait.until(ExpectedConditions.visibilityOf(myProfile));
		  System.out.println(number + " has successfully logged in");
		  test().info(number + " has successfully logged in");
		  path = captureScreenshot("User_Login");
		  test().info("<a href='data:image/png;base64," +
		  path + "' data-featherlight='image'><img src='data:image/png;base64," + path
		  + "' style='width:200px;height:auto;cursor:pointer;'/></a>");
		  assertTrue(myProfile.isDisplayed(), "User login failed");	

	}

	// Logout execution
	public void logoutSteps() throws IOException {
		click(accountTab);
		test().info("Log out process started");
		driver.findElement(
			    AppiumBy.androidUIAutomator(
			        "new UiScrollable(new UiSelector().scrollable(true))" +
			        ".scrollIntoView(new UiSelector().description(\"Log Out\"))"
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

	    // findElements() otherwise inherits the driver's 30s implicit wait (set in
	    // Base.opn_app), so every "not showing" check - the common case, since this is
	    // called after most add-to-cart steps just to see whether a mid-flow login
	    // popped up - would silently cost 30s. Drop it to near-zero for this one
	    // lookup, then restore it so nothing else in the flow loses its implicit wait.
	    driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

	    int count;

	    try {
	        count = driver.findElements(
	                AppiumBy.id("com.titan.eyecare:id/txt_login_label"))
	                .size();
	    } finally {
	        // Restoring the implicit wait is housekeeping, not the actual result of this check -
	        // if the session is already unstable, this call throwing would replace whatever the
	        // try block above actually found/threw (Java's finally-supersedes-try behavior).
	        try {
	            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
	        } catch (Exception e) {
	            System.out.println("Could not restore implicit wait after login-page check: " + e.getMessage());
	        }
	    }

	    System.out.println("Count = " + count);

	    return count > 0;
	}

}
