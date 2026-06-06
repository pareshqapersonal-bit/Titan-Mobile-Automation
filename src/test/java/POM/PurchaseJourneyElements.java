package POM;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import Utilities.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class PurchaseJourneyElements extends CommonUtils {

	

	AndroidDriver driver = null;
	public PurchaseJourneyElements(AndroidDriver driver)
	{
		this.driver = driver;
		PageFactory.initElements(driver, this);
		
		
	}
	
	@FindBy(id="com.titan.eyecare:id/rl_toolbar_search")
	WebElement searchClick;
	
	@FindBy(id="com.titan.eyecare:id/edt_search")
	WebElement searchField;
	
	@FindBy(id="com.titan.eyecare:id/txt_product_name")
	WebElement productSelection;
	
	@FindBy(id="com.titan.eyecare:id/txt_btn_title")
	WebElement buyNowCTA;
	
	@FindBy(id="com.titan.eyecare:id/txt_btn_title")
	WebElement proceedToCheckout;
	
	@FindBy(xpath = "//*[@text='Axis']")
	WebElement bankSelection;
	
	@FindBy(id="com.titan.eyecare:id/layout_checkout_button")
	WebElement proceedToPay;
	
	@FindBy(id="com.titan.eyecare:id/txt_btn_title")
	WebElement continuePaymentCTA;;
	
	
	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	
	
	
	
	//Search flow
	public void searchSteps()
	{
		click(searchClick);
		sendKeys(searchField, "GM344PK1F");
		
	}
	
	
	//Cart flow
	public void cartFlow()
	{
		click(productSelection);
		click(buyNowCTA);
	}
	
	//Proceed to checkout flow
	public void proceedToCheckout()
	{
		click(proceedToCheckout);
	}
	
	//Address section
	public void proceedToPay()
	{
		click(proceedToPay);
	}
	
	//Payment section
	public void payment()
	{
		driver.findElement(
			    AppiumBy.androidUIAutomator(
			        "new UiScrollable(new UiSelector().scrollable(true))" +
			        ".scrollIntoView(new UiSelector().text(\"Net Banking\"))"
			    )
			).click();
		
		click(bankSelection);
		click(continuePaymentCTA);
		
		System.out.println("DOM RazorPay"+driver.getContextHandles());
	}

}
