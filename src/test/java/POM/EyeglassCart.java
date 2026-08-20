package POM;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import Utilities.CommonUtils;
import Utilities.CredentialManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;
import models.LoginCredentials;

/**
 * Handles adding an Eyeglass product to the cart, including the optional
 * lens-upgrade dialog and lens-add page that this category presents.
 */
public class EyeglassCart extends CommonUtils {

    AndroidDriver driver;
     private final String loginUser;
    LoginElements le;

    public EyeglassCart(AndroidDriver driver, String loginUser) {

        this.driver = driver;
        this.loginUser = loginUser;
        PageFactory.initElements(driver, this);

        le = new LoginElements(driver);
    }

    @FindBy(id="com.titan.eyecare:id/txt_btn_title")
    WebElement buyNowCTA;

    @FindBy(id="com.titan.eyecare:id/txt_btn_title")
    WebElement lensAddCTA;
    
    @FindBy(xpath="//android.widget.TextView[@resource-id='com.titan.eyecare:id/txt_title' and @text='Choose Lens Upgrades']")
    WebElement lensUpgradePage;
    @FindBy(id="com.titan.eyecare:id/txt_package_info_title")
    WebElement lensAddPage;
    
    @FindBy(xpath="//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_lens_add_dialog_title\"]")
    WebElement lensAddDialog;
    
    @FindBy(id="com.titan.eyecare:id/txt_btn_title")
    WebElement lensAddDialogCTA;
    
    @FindBy(id="android:id/button1")
    WebElement okSub;


    public void addToCart(CartProduct product) {

        click(buyNowCTA);

        if(le.isLoginPageDisplayed()) {

        	LoginCredentials credentials =
                    CredentialManager.getCredentials(loginUser);

            le.shortLogin(credentials);

            click(buyNowCTA);
        }

       lensAddDialogCTA.isDisplayed();
       click(lensAddDialogCTA);
       visibilityOf(lensAddPage);
       System.out.println("Lens Add page displayed");
        click(lensAddCTA);
       
        handleLensUpgradePage();
        click(okSub);
    }
    
    
    //skip optional lens selection
    private void handleLensUpgradePage() {
    	System.out.println("Checking for Lens Upgrade page");
  visibilityOf(lensUpgradePage);
        List<WebElement> skipButton = driver.findElements(
                AppiumBy.xpath("//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_skip\"]")
        );

        if (!skipButton.isEmpty() && skipButton.get(0).isDisplayed()) {

            System.out.println("Lens Upgrade page displayed");
            skipButton.get(0).click();
            System.out.println("Clicked Skip on Lens Upgrade page");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement buyNow = wait.until(driver ->
            driver.findElements(
            		AppiumBy.xpath(
            		        "//android.widget.TextView[" +
            		        "@resource-id='com.titan.eyecare:id/txt_btn_title' " +
            		        "and @text='Buy Now']"
            		    )
            ).stream()
            .filter(WebElement::isDisplayed)
            .findFirst()
            .orElse(null)
    );

    System.out.println("Buy Now button visible again");

            click(buyNowCTA);

        } else {

            System.out.println("Lens Upgrade page not displayed");
        }
    }
}