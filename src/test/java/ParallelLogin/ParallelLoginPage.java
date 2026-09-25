package ParallelLogin;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class ParallelLoginPage {

    private AndroidDriver driver;
    private WebDriverWait wait;

    public ParallelLoginPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//android.widget.RelativeLayout[@resource-id=\"com.titan.eyecare:id/rl_toolbar_app\"]/android.widget.ImageView")
    private WebElement drawer;

    @FindBy(xpath = "//android.widget.LinearLayout[@resource-id=\"com.titan.eyecare:id/ll_without_login\"]/android.widget.RelativeLayout")
    private WebElement loginIcon;

    @FindBy(xpath = "//android.widget.EditText[@resource-id=\"com.titan.eyecare:id/edt_email_phone\"]")
    private WebElement mobileTextField;

    @FindBy(xpath = "//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_btn_title\"]")
    private WebElement loginCTA;

    @FindBy(id = "com.titan.eyecare:id/txt_btn_title")
    private WebElement loginSubmission;

    @FindBy(id = "com.titan.eyecare:id/txt_username")
    private WebElement profileName;
    @FindBy(id="com.titan.eyecare:id/btn_negative")
	WebElement laterCTA;

    public void openLogin() {

        wait.until(ExpectedConditions.elementToBeClickable(drawer))
                .click();

        wait.until(ExpectedConditions.elementToBeClickable(loginIcon))
                .click();
    }


    public void login(String mobile, String password) {

        wait.until(ExpectedConditions.visibilityOf(mobileTextField))
                .sendKeys(mobile);

        wait.until(ExpectedConditions.elementToBeClickable(loginCTA))
                .click();

        WebElement passwordField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        org.openqa.selenium.By.className(
                                "android.widget.EditText"
                        )
                )
        );

        passwordField.sendKeys(password);

        wait.until(ExpectedConditions.elementToBeClickable(loginSubmission))
                .click();
    }


    public boolean isLoggedIn() {
    	
    	if(isEncirclepopupDisplayed())
		{
			laterCTA.click();
		}

        return wait.until(
                ExpectedConditions.visibilityOf(profileName)
        ).isDisplayed();
    }
    
    public boolean isEncirclepopupDisplayed()
	{
		 int count;

		    try {
		        count = driver.findElements(
		                AppiumBy.id("com.titan.eyecare:id/btn_negative"))
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