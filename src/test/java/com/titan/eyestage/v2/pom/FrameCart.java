package com.titan.eyestage.v2.pom;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.models.CartProduct;
import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.android.AndroidDriver;

// Handles adding a Frame-only product to the cart (no lens selection).
public class FrameCart extends CommonUtils {

	AndroidDriver driver;
	private final String mobileNumber;
	private final String password;
	LoginElements le;

	public FrameCart(AndroidDriver driver, String mobileNumber, String password) {

		this.driver = driver;
		this.mobileNumber = mobileNumber;
		this.password = password;
		PageFactory.initElements(driver, this);

		le = new LoginElements(driver);
	}

	@FindBy(id = "com.titan.eyecare:id/txt_btn_title")
	WebElement buyNowCTA;

	@FindBy(id = "com.titan.eyecare:id/txt_lens_add_dialog_buy")
	WebElement onlyFrameCTA;

	public void addToCart(CartProduct product) {

		click(buyNowCTA);

		if (le.isLoginPageDisplayed()) {

			le.shortLogin(mobileNumber, password);

			click(buyNowCTA);
		}

		click(onlyFrameCTA);
	}
}
