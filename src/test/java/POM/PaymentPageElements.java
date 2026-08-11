package POM;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

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
    
    @FindBy(xpath = "//android.widget.TextView[@resource-id='com.titan.eyecare:id/txt_checkout_payment_method_title' and @text='Google Pay']")
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

    public void selectPaymentMethod(String paymentMethod) {

        PaymentMethod method =
                PaymentMethod.fromExcel(paymentMethod);

        switch (method) {

        case GOOGLE_PAY:
            // Google Pay locator - we'll add after Inspector
        	 visibilityOf(GPay);
             click(GPay);
             System.out.println("Selected payment method: Google Pay");
             
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
            break;

        case OTHER_UPI:
            // Other UPI locator
            break;

        case NET_BANKING:
            // Net Banking locator
        	test.info("Selecting payment method");
    		driver.findElement(
    			    AppiumBy.androidUIAutomator(
    			        "new UiScrollable(new UiSelector().scrollable(true))" +
    			        ".scrollIntoView(new UiSelector().text(\"Net Banking\"))"
    			    )
    			).click();
    		
    		click(bankSelection);
    		click(continuePaymentCTA);
            break;
            
         case CASH_ON_DELIVERY:
        	 driver.findElement(
     			    AppiumBy.androidUIAutomator(
     			        "new UiScrollable(new UiSelector().scrollable(true))" +
     			        ".scrollIntoView(new UiSelector().text(\"Cash on Delivery\"))"
     			    )
     			).click();
        	 click(confirmOrder);
			 break;
			 
	   case WALLLET:
			
			  driver.findElement( AppiumBy.androidUIAutomator(
			  "new UiScrollable(new UiSelector().scrollable(true))" +
			  ".scrollIntoView(new UiSelector().text(\"Total Amount\"))" ) ); 
			  String amount=totalAmount.getText();
			  
			  System.out.println("amount is"+ amount);
			  
			  driver.findElement( AppiumBy.androidUIAutomator(
			  "new UiScrollable(new UiSelector().scrollable(true))" +
			  ".scrollIntoView(new UiSelector().text(\"Titan Wallet\"))" ) );
			  click(walletCheckbox); sendKeys(walletAmountField, "100");
			  click(walletRedeemButton);
			 
		   
	 

		   

		    
			break;

        default:
            throw new IllegalArgumentException(
                "Unsupported payment method: " + paymentMethod
            );
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
}