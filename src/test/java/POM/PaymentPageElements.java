package POM;

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
    @FindBy(id="com.titan.eyecare:id/txt_btn_title")
    private WebElement continuePaymentCTA;
    @FindBy(xpath = "//android.widget.LinearLayout[@resource-id=\"com.titan.eyecare:id/ll_checkout_payment_method_banking_list\"]/android.widget.LinearLayout[2]")
	WebElement bankSelection;

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

        case PAYTM:
            // Paytm locator
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

        default:
            throw new IllegalArgumentException(
                "Unsupported payment method: " + paymentMethod
            );
        }
        
        
    }
    
    public void continueToPayment() {

        visibilityOf(continuePaymentCTA);
        click(continuePaymentCTA);

        System.out.println("Clicked Continue to Payment");
    }
}