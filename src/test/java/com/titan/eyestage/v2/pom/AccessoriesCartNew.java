package com.titan.eyestage.v2.pom;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.v2.utils.CommonUtils;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

// Handles adding an Accessories product to the cart.
public class AccessoriesCartNew extends CommonUtils {

	AndroidDriver driver;
	

	public AccessoriesCartNew(AndroidDriver driver) {

		this.driver = driver;
		
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);

		
	}

	@AndroidFindBy(accessibility = "Add to cart")
	WebElement addToCartButton;

	public void addToCart() {

		click(addToCartButton);

		
	}
}
