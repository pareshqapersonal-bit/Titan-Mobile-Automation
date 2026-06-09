package POM;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import io.appium.java_client.android.AndroidDriver;

public class TrackOrderElements extends CommonUtils {
	AndroidDriver driver = null;
	public TrackOrderElements(AndroidDriver driver)
	{
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}

	String path =null;
	@FindBy(xpath="//android.widget.RelativeLayout[@resource-id=\"com.titan.eyecare:id/rl_toolbar_app\"]/android.widget.ImageView")
	WebElement Drawer;
	
	@FindBy(xpath="//android.widget.TextView[@text=\"Track Order\"]")
	WebElement trackorderlink;
	
	@FindBy(id="com.titan.eyecare:id/edt_mobile")
	WebElement tMobileField;
	
	@FindBy(id="com.titan.eyecare:id/txt_btn_title")
	WebElement tbutton1;
	
	@FindBy(id="com.titan.eyecare:id/txt_title")
	WebElement trackOrderTitleText;
	
	public void trackOrderStepsWithNumber() throws IOException {
		click(Drawer);
		click(trackorderlink);
		
		test.info("Process of tracking the order started");
		
		enter(tMobileField);
		tMobileField.clear();
		sendKeys(tMobileField, "8779906355");
		click(tbutton1);
		path = captureScreenshot("Order tracking");
		
		System.out.println(getText(trackOrderTitleText));
		assertTrue(trackOrderTitleText.isDisplayed(), "Order tracking fialed");
		test.addScreenCaptureFromPath(path);
		test.info("Order has been tracked successfully");
	}
}
