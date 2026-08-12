package POM;

import static org.testng.Assert.assertEquals;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import Utilities.CommonUtils;
import Utilities.PaymentMethodMapper.PaymentMethod;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class PaymentPageElements extends CommonUtils {

    AndroidDriver driver;

    public PaymentPageElements(AndroidDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    
    // Use a more flexible locator: text may vary or include extra whitespace/locale changes
    @FindBy(xpath = "//android.widget.TextView[contains(@resource-id,'txt_checkout_payment_method_title') and contains(@text,'Google')]")
    private WebElement GPay;

    @FindBy(xpath = "//android.widget.TextView[@text=\"Net Banking\"]")
    private WebElement NetBankingClick;
    @FindBy(xpath = "//android.widget.TextView[@resource-id='com.titan.eyecare:id/txt_btn_title' and @text='Continue to Payment']")
	private WebElement continuePaymentCTA;
    @FindBy(xpath = "//android.widget.LinearLayout[@resource-id=\"com.titan.eyecare:id/ll_checkout_payment_method_banking_list\"]/android.widget.LinearLayout[2]")
	WebElement bankSelection;
    
    //Credit Card, Debit Card
    @FindBy(xpath = "//android.widget.TextView[@text='Credit / Debit Card']")
    private WebElement creditDebitCard;
    
    @FindBy(id = "com.titan.eyecare:id/edt_payment_methods_cardnumber")
    WebElement cardNumberField;
    
    @FindBy(id = "com.titan.eyecare:id/edt_payment_methods_carddate")
    WebElement cardExpiryField;
    
    @FindBy(id = "com.titan.eyecare:id/edt_payment_methods_cardcvv")
    WebElement cardCVVField;
    
    @FindBy(id = "com.titan.eyecare:id/edt_payment_methods_cardholdername")
    WebElement cardHolderNameField;
    
    //cash on delivery
    @FindBy(xpath = "//android.widget.TextView[@resource-id='com.titan.eyecare:id/txt_btn_title' and @text='Confirm Order']")
    private WebElement confirmOrder;
    
    //Wallet
    @FindBy(id = "com.titan.eyecare:id/chk_cart_wallet")
    WebElement walletCheckbox;
    
    @FindBy(id = "com.titan.eyecare:id/edt_wallet_amount")
    WebElement walletAmountField;
    
    @FindBy(id = "com.titan.eyecare:id/txt_wallet_amount_redeem")
    WebElement walletRedeemButton;
    
    @FindBy(id = "com.titan.eyecare:id/txt_cart_total_price")
    private WebElement totalAmount;
    
    @FindBy(xpath = "//android.widget.TextView[@resource-id='com.titan.eyecare:id/txt_btn_title' and @text='Wohoo!']")
    WebElement paymentConfirmation;
    
    @FindBy(id = "com.titan.eyecare:id/txt_app_rate_btn_later")
    private WebElement mayBLater;
    
    @FindBy(xpath =
    	    "//android.widget.LinearLayout[@resource-id='com.titan.eyecare:id/ll_search_bar']" +
    	    "//android.widget.ImageView[@resource-id='com.titan.eyecare:id/img_back']")
    	WebElement searchBackButton;
    
    private final By backButtonLocator =
    	    AppiumBy.id("com.titan.eyecare:id/img_toolbar_back");

    public void selectPaymentMethod(String paymentMethod) throws InterruptedException {

        PaymentMethod method =
                PaymentMethod.fromExcel(paymentMethod);

        switch (method) {

        case GOOGLE_PAY:
            // Google Pay locator - we'll add after Inspector
            // Try a scroll-into-view first (handles off-screen items) then click.
            try {
                clickPaymentMethodByText("Google Pay");
            } catch (Exception e) {
                // fallback to the @FindBy element if UIAutomator didn't find it
                try {
                    visibilityOf(GPay);
                    click(GPay);
                } catch (Exception ex) {
                    System.out.println("Failed to select Google Pay: " + ex.getMessage());
                    throw ex;
                }
            }
            System.out.println("Selected payment method: Google Pay");
            
            
            click(continuePaymentCTA);
            //razorpay flow
            
            test.info("Initiating Razorpay payment");
   		 System.out.println("RazorPay Context"+driver.getContextHandles());
   		Thread.sleep(1000);
   		Set<String> context = driver.getContextHandles();
   		System.out.println("Context is"+context);
   		
   		for(String ctext : context)
   	    {
   	        System.out.println(ctext);
   	        if(ctext.contains("WEBVIEW"))
   	        {
   	        	List<WebElement> buttons =
   	        		    driver.findElements(By.xpath("//android.widget.TextView[@resource-id=\"cancel-btn\"]"));

   	        		System.out.println("Count = " + buttons.size());
   	        		driver.findElement(By.xpath("//android.widget.TextView[@resource-id=\"cancel-btn\"]")).click();
   	        
   	        	break;
   	        }
   	    }
   		Thread.sleep(1000);
   		
   		System.out.println("Payment confirmation text: " + getText(paymentConfirmation));
   		assertEquals(getText(paymentConfirmation), "Wohoo!", "Payment confirmation text mismatch");
   		
   		click(paymentConfirmation);
   		click(mayBLater);
   		clickBackFromOrderPage();
             
            break;

        case PHONEPE:
            // PhonePe locator
            break;

        case CREDIT_DEBIT_CARD:
        	test.info("Selecting payment method");
    		driver.findElement(
    			    AppiumBy.androidUIAutomator(
    			        "new UiScrollable(new UiSelector().scrollable(true))" +
    			        ".scrollIntoView(new UiSelector().text(\"Credit / Debit Card\"))"
    			    )
    			).click();
    		
    		sendKeys(cardNumberField, "4100280000001007");
    		sendKeys(cardExpiryField, "12/35");
    		sendKeys(cardCVVField, "123");
    		sendKeys(cardHolderNameField, "Test User");
    		click(continuePaymentCTA);
    		
    		 test.info("Initiating Razorpay payment");
    		 System.out.println("RazorPay Context"+driver.getContextHandles());
    		Thread.sleep(200);
    		 context = driver.getContextHandles();
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
    		Thread.sleep(200);
    		
    		
    		System.out.println("Payment confirmation text: " + getText(paymentConfirmation));
       		assertEquals(getText(paymentConfirmation), "Wohoo!", "Payment confirmation text mismatch");
    		
            break;

        case OTHER_UPI:
            // Other UPI locator
            break;

        case NET_BANKING:
            // Net Banking locator
        	System.out.println("Selecting payment method: Net Banking");
        	test.info("Selecting payment method");
    		driver.findElement(
    			    AppiumBy.androidUIAutomator(
    			        "new UiScrollable(new UiSelector().scrollable(true))" +
    			        ".scrollIntoView(new UiSelector().text(\"Net Banking\"))"
    			    )
    			).click();
    		
    		click(bankSelection);
    		click(continuePaymentCTA);
    		
    		 test.info("Initiating Razorpay payment");
    		 System.out.println("RazorPay Context"+driver.getContextHandles());
    		Thread.sleep(200);
    		 context = driver.getContextHandles();
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
    		Thread.sleep(200);
    		
    		System.out.println("Payment confirmation text: " + getText(paymentConfirmation));
       		assertEquals(getText(paymentConfirmation), "Wohoo!", "Payment confirmation text mismatch");
    		
            break;
            
         case CASH_ON_DELIVERY:
        	 driver.findElement(
     			    AppiumBy.androidUIAutomator(
     			        "new UiScrollable(new UiSelector().scrollable(true))" +
     			        ".scrollIntoView(new UiSelector().text(\"Cash on Delivery\"))"
     			    )
     			).click();
        	 click(confirmOrder);
        	 
        	 System.out.println("Payment confirmation text: " + getText(paymentConfirmation));
        		assertEquals(getText(paymentConfirmation), "Wohoo!", "Payment confirmation text mismatch");
			 break;
			 
	   case WALLLET:
			
			  driver.findElement( AppiumBy.androidUIAutomator(
			  "new UiScrollable(new UiSelector().scrollable(true))" +
			  ".scrollIntoView(new UiSelector().text(\"Total Amount\"))" ) ); 
			  String amount = totalAmount.getText()
				        .replace("₹", "")
				        .replace(",", "")
				        .replace(".00", "")
				        .trim();

			  System.out.println(amount);
			  System.out.println("amount is"+ amount);
			  
			  driver.findElement( AppiumBy.androidUIAutomator(
			  "new UiScrollable(new UiSelector().scrollable(true))" +
			  ".scrollIntoView(new UiSelector().text(\"Titan Wallet\"))" ) );
			  click(walletCheckbox); sendKeys(walletAmountField, amount);
			  click(walletRedeemButton);
			  click(confirmOrder);
			  System.out.println("Payment confirmation text: " + getText(paymentConfirmation));
	       		assertEquals(getText(paymentConfirmation), "Wohoo!", "Payment confirmation text mismatch");
			 
		   
	 

		   

		    
			break;

        default:
            throw new IllegalArgumentException(
                "Unsupported payment method: " + paymentMethod
            );
        }
        
        
    }
    
    /**
     * Scrolls to a payment method by visible text (uses UiScrollable) and clicks it.
     * Falls back to finding an element containing the text if UiScrollable fails.
     */
    private void clickPaymentMethodByText(String visibleText) {
        try {
            driver.findElement(
                AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().textContains(\"" + visibleText + "\"))"
                )
            ).click();
            return;
        } catch (Exception e) {
            // fallback: try xpath contains text
            List<WebElement> elems = driver.findElements(AppiumBy.xpath("//*[contains(@text,'" + visibleText + "')]") );
            if (!elems.isEmpty()) {
                click(elems.get(0));
                return;
            }
            throw e;
        }
    }
    
    public void continueToPayment() {

        System.out.println("========== CONTINUE PAYMENT START ==========");

        System.out.println("Current Activity: " + driver.currentActivity());
        System.out.println("Current Context: " + driver.getContext());

        System.out.println("Contexts: " + driver.getContextHandles());

        List<WebElement> buttons = driver.findElements(
            AppiumBy.xpath(
                "//android.widget.TextView[@resource-id='com.titan.eyecare:id/txt_btn_title']"
            )
        );

        System.out.println("txt_btn_title count = " + buttons.size());

        for (WebElement button : buttons) {
            try {
                System.out.println(
                    "TEXT = [" + button.getText() + "]"
                    + " | DISPLAYED = " + button.isDisplayed()
                    + " | ENABLED = " + button.isEnabled()
                );
            } catch (Exception e) {
                System.out.println("Could not read button: " + e.getMessage());
            }
        }

        System.out.println("========== CONTINUE PAYMENT END ==========");
    }
    
    
    public void clickBackFromOrderPage() {

        WebDriverWait wait =
            new WebDriverWait(driver, Duration.ofSeconds(30));

        WebElement backButton =
            wait.until(ExpectedConditions.elementToBeClickable(backButtonLocator));

        backButton.click();
        click(searchBackButton);
    }
}