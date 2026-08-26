package com.titan.eyestage.v2.pom;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.android.AndroidDriver;

// Handles adding a (non-powered) Sunglass product to the cart.
public class SunglassCart extends CommonUtils {

	AndroidDriver driver;
	private final String mobileNumber;
	private final String password;
	LoginElements le;

	public SunglassCart(AndroidDriver driver, String mobileNumber, String password) {

		this.driver = driver;
		this.mobileNumber = mobileNumber;
		this.password = password;
		PageFactory.initElements(driver, this);

		le = new LoginElements(driver);
	}

	@FindBy(id = "com.titan.eyecare:id/txt_btn_title")
	WebElement buyNowCTA;

	public void addToCart(CartProduct product) {

		test().info("Adding Sunglass product to cart");

		click(buyNowCTA);

		if (le.isLoginPageDisplayed()) {

			le.shortLogin(mobileNumber, password);

			click(buyNowCTA);
		}

		test().pass("Sunglass added to cart successfully");
	}
}
