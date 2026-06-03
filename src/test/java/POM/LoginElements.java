package POM;

import java.beans.Visibility;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
	
	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	
	
	public void testexecution() throws InterruptedException {
		
		//wait.until(ExpectedConditions.visibilityOf(Drawer)).click();
		click(Drawer);
		
	//wait.until(ExpectedConditions.visibilityOf(	loginIcon)).click();
		click(loginIcon);
	//wait.until(ExpectedConditions.visibilityOf(MobileTextField)).sendKeys(Keys.ENTER);
		sendKeys(MobileTextField, "8698294937");
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

	edits.get(0).sendKeys("254265");
	//wait.until(ExpectedConditions.visibilityOf(loginsubmission)).click();
	click(loginsubmission);
//	wait.until(ExpectedConditions.visibilityOf(Drawer)).click();
    click(Drawer);	

	
	
	
	}
	
	//Logout execution
		public void logoutSteps()
		{
			//wait.until(ExpectedConditions.visibilityOf(Drawer)).click();
			click(Drawer);
			driver.findElement(
				    AppiumBy.androidUIAutomator(
				        "new UiScrollable(new UiSelector().scrollable(true))" +
				        ".scrollIntoView(new UiSelector().text(\"Logout\"))"
				    )
				).click();
			
			//wait.until(ExpectedConditions.visibilityOf(logoutConfirmation)).click();
			click(logoutConfirmation);
		}

		
		
}
