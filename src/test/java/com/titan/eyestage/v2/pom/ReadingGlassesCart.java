package com.titan.eyestage.v2.pom;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.android.AndroidDriver;

// Handles adding a Reading Glasses product to the cart, including the
// power selection step this category requires before Buy Now.
public class ReadingGlassesCart extends CommonUtils {

	AndroidDriver driver;
	private final String mobileNumber;
	private final String password;
	LoginElements le;

	public ReadingGlassesCart(AndroidDriver driver, String mobileNumber, String password) {

		this.driver = driver;
		this.mobileNumber = mobileNumber;
		this.password = password;
		PageFactory.initElements(driver, this);

		le = new LoginElements(driver);
	}

	@FindBy(xpath = "//android.widget.TextView[@resource-id=\"com.titan.eyecare:id/txt_lens_power\" and @text=\"+1.25\"]")
	WebElement Power;

	@FindBy(id = "com.titan.eyecare:id/txt_btn_title")
	WebElement buyNowCTA;

	public void addToCart() {

		click(Power);
		click(buyNowCTA);

		if (le.isLoginPageDisplayed()) {

			le.shortLogin(mobileNumber, password);

			click(Power);
			click(buyNowCTA);
		}
	}
}
