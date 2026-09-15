package ParallelLogin;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

        return wait.until(
                ExpectedConditions.visibilityOf(profileName)
        ).isDisplayed();
    }
}