package POM;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import Utilities.CommonUtils;
import io.appium.java_client.android.AndroidDriver;

public class PaymentPageElements extends CommonUtils {

    AndroidDriver driver;

    public PaymentPageElements(AndroidDriver driver) {
        this.driver = driver;
    }
    
    @FindBy(xpath = "(//android.widget.ImageView[@resource-id=\"com.titan.eyecare:id/img_checkout_payment_method_selector\"])[1]")
    private WebElement GPayCheckbox;
    
    

    public void selectPaymentMethod(String paymentMethod) {

        PaymentMethod method =
                PaymentMethod.fromExcel(paymentMethod);

        switch (method) {

        case GOOGLE_PAY:
            // Google Pay locator - we'll add after Inspector
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
            break;

        default:
            throw new IllegalArgumentException(
                "Unsupported payment method: " + paymentMethod
            );
        }
    }
}