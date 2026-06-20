package POM;

import static org.testng.Assert.assertTrue;

import java.beans.Visibility;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import Utilities.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class LoginElements extends CommonUtils  {
	
	AndroidDriver driver = null;
	public LoginElements(AndroidDriver driver)
	{
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
	
	
	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	
	String path = null;
	public void testexecution(String number, String pass) throws InterruptedException, IOException {
		test.info("Login process started");
		//wait.until(ExpectedConditions.visibilityOf(Drawer)).click();
		click(Drawer);
		
	//wait.until(ExpectedConditions.visibilityOf(	loginIcon)).click();
		click(loginIcon);
	//wait.until(ExpectedConditions.visibilityOf(MobileTextField)).sendKeys(Keys.ENTER);
		sendKeys(MobileTextField, number);
	//MobileTextField.sendKeys("8698294937");
	//wait.until(ExpectedConditions.visibilityOf(loginCTA)).click();
	click(loginCTA);
	
	
	/*
	 * List<WebElement> elements =
	 * driver.findElements(By.id("com.titan.eyecare:id/otp_view"));
	 * 
	 * System.out.println("Number of elemets are"+elements.size()); for(int
	 * i=2;i>=elements.size();i++) {
	 * wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath
	 * ("(//android.widget.FrameLayout[@resource-id=\"com.titan.eyecare:id/otp_view\"])["
	 * +i+"]")))).sendKeys("2"); }
	 */
	
	List<WebElement> edits =
	        driver.findElements(AppiumBy.className("android.widget.EditText"));

	System.out.println("EditText count = " + edits.size());

	edits.get(0).sendKeys(pass);
	//wait.until(ExpectedConditions.visibilityOf(loginsubmission)).click();
	click(loginsubmission);
//	wait.until(ExpectedConditions.visibilityOf(Drawer)).click();
	click(Drawer);	
	path = captureScreenshot("User_Details");
   

	String userPRofileName= wait.until(ExpectedConditions.visibilityOf(profileName)).getText();
	System.out.println("My name is"+userPRofileName);
	test.info(userPRofileName+ "has successfully logged in");
	visibilityOf(profileName);
	test.addScreenCaptureFromPath(path);
	assertTrue(profileName.isDisplayed(), "User login failed"); 
	
	
	}
	
	//Logout execution
		public void logoutSteps() throws IOException
		{
			//wait.until(ExpectedConditions.visibilityOf(Drawer)).click();
			click(Drawer);
			test.info("Log out process started");
			driver.findElement(
				    AppiumBy.androidUIAutomator(
				        "new UiScrollable(new UiSelector().scrollable(true))" +
				        ".scrollIntoView(new UiSelector().text(\"Logout\"))"
				    )
				).click();
			
			//wait.until(ExpectedConditions.visibilityOf(logoutConfirmation)).click();
			click(logoutConfirmation);
			path = captureScreenshot("User_Details");
			test.info("User has successfully logged out");
			test.addScreenCaptureFromPath(path);
			
		}
		
		
		//small login function without navigation

		public void shortLogin(String number, String pass)
		{
			sendKeys(MobileTextField, number);
			click(loginCTA);
			List<WebElement> edits =
			        driver.findElements(AppiumBy.className("android.widget.EditText"));

			System.out.println("EditText count = " + edits.size());

			edits.get(0).sendKeys(pass);
			
			click(loginsubmission);
		}
		
		
		//Login page visibility
		/*public boolean isLoginPageDisplayed()
		{
			try {
			 visibilityOf(loginPageTitleText);
				System.out.println(getText(loginPageTitleText));
				return loginPageTitleText.isDisplayed();
				
			}catch (Exception e) {
				  e.printStackTrace();
				return false;
			}*/
			
		
		public void permissionPopup()
		{try {

	        List<WebElement> allowButtons =
	                driver.findElements(
	                    AppiumBy.id(
	                        "com.android.permissioncontroller:id/permission_allow_button"));

	        if (!allowButtons.isEmpty()) {
	            allowButtons.get(0).click();
	            test.info("Permission popup handled.");
	        }

	    } catch (Exception e) {
	        test.info("Permission popup not displayed.");
	    }
		}
			
			public boolean isLoginPageDisplayed()
			{
			    System.out.println("Checking Login Page");

			    int count = driver.findElements(
			            AppiumBy.id("com.titan.eyecare:id/txt_login_label"))
			            .size();

			    System.out.println("Count = " + count);

			    return count > 0;
			}
		}

