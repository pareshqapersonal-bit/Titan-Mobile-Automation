package POM;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import Utilities.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class PurchaseJourneyElements extends CommonUtils {

	LoginElements le;

	AndroidDriver driver = null;
	public PurchaseJourneyElements(AndroidDriver driver)
	{
		this.driver = driver;
		PageFactory.initElements(driver, this);
		le = new LoginElements(driver);
		
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
	
	@FindBy(xpath = "//android.widget.LinearLayout[@resource-id=\"com.titan.eyecare:id/ll_checkout_payment_method_banking_list\"]/android.widget.LinearLayout[2]")
	WebElement bankSelection;
	
	@FindBy(id="com.titan.eyecare:id/layout_checkout_button")
	WebElement proceedToPay;
	
	@FindBy(id="com.titan.eyecare:id/txt_btn_title")
	WebElement continuePaymentCTA;;
	
	@FindBy(id="com.titan.eyecare:id/txt_btn_title")
	WebElement paymentConfirmation;
	
	@FindBy(id="com.titan.eyecare:id/txt_title")
	WebElement cartTitle;
	
	//LoginElements le = new LoginElements(driver);
	
	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	
	
	String path = null;
	
	//Search flow
	public void searchSteps()
	{   
		test.info("Searching for product");
	
		click(searchClick);
		sendKeys(searchField, "GM344PK1F");
		
		test.info("Product Searching completed");
	}
	
	
	//Cart flow
	public void cartFlow(String number, String pass) throws IOException
	{
		test.info("Adding product to cart");
		click(productSelection);
		click(buyNowCTA);
		if(le.isLoginPageDisplayed())
		{
			System.out.println("inside fun");
			le.shortLogin(number, pass);
			click(buyNowCTA);
			
		}
		test.pass("Product added to cart");
		
		visibilityOf(cartTitle);
		
		path = captureScreenshot("Cart_Page");
		test.info("Cart Page Displayed");
		test.addScreenCaptureFromPath(path);
		assertTrue(cartTitle.isDisplayed(), "Cart page not displayed"); 
	}
	
	//Proceed to checkout flow
	public void proceedToCheckout() throws IOException
	{
		test.info("Proceeding to checkout");
		click(proceedToCheckout);
		 test.pass("Address page opened");
		 path = captureScreenshot("Address page");
		 test.info("Address page displayed");
		 test.addScreenCaptureFromPath(path);
			assertTrue(cartTitle.isDisplayed(), "Address page not displayed"); 

		 
	}
	
	//Address section
	public void proceedToPay() throws IOException
	{
		test.info("Selecting Addresses");
		click(proceedToPay);
		test.pass("Selected  Addresses");
		
		path = captureScreenshot("Payment page");
		 test.info("Payment page displayed");
		 test.addScreenCaptureFromPath(path);
			assertTrue(cartTitle.isDisplayed(), "Payment page not displayed"); 

	}
	
	//Payment section
	public void payment()
	{ 
		test.info("Selecting payment method");
		driver.findElement(
			    AppiumBy.androidUIAutomator(
			        "new UiScrollable(new UiSelector().scrollable(true))" +
			        ".scrollIntoView(new UiSelector().text(\"Net Banking\"))"
			    )
			).click();
		
		click(bankSelection);
		click(continuePaymentCTA);
		test.pass("Payment method selected");
		System.out.println("DOM RazorPay"+driver.getContextHandles());
	}
	
	//Razorpay working
	public void razorPay() throws InterruptedException
	{
		 test.info("Initiating Razorpay payment");
		Thread.sleep(1000);
		Set<String> context = driver.getContextHandles();
		System.out.println("Context is"+context);
		
		for(String ctext : context)
	    {
	        System.out.println(ctext);
	        if(ctext.contains("WEBVIEW"))
	        {
	        	List<WebElement> buttons =
	        		    driver.findElements(By.xpath("//*[contains(@text,'Success')]"));

	        		System.out.println("Count = " + buttons.size());
	        		driver.findElement(By.xpath("//*[contains(@text,'Success')]")).click();
	        
	        	break;
	        }
	    }
		Thread.sleep(1000);
		assertEquals(getText(paymentConfirmation), "Wohoo!");
	//	driver.context("WEBVIEW_com.titan.eyecare");
		 test.pass("Payment completed");
		
	}

}
