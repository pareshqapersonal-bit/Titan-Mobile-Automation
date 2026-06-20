package POM;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import com.titan.eyestage.Base;

import Utilities.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class HomePageElements extends CommonUtils {
	
	AndroidDriver driver = null;
	public HomePageElements(AndroidDriver driver)
	{
		this.driver= driver;
		PageFactory.initElements(driver, this);
	}

	    @FindBy(id="com.titan.eyecare:id/viewPager")
	    WebElement bannerCarousel;

	    @FindBy(id="com.titan.eyecare:id/viewPager_banner")
	    List<WebElement> banners;

	    @FindBy(id="com.titan.eyecare:id/dot")
	    List<WebElement> dots;
	    
	    @FindBy(id="com.titan.eyecare:id/txt_title")
	    WebElement productListingTitle;
	    
	    @FindBy(xpath="//android.widget.LinearLayout[@content-desc=\"SUNGLASSES\"]")
	    WebElement CategorySelection;
	    
	    @FindBy(xpath="//android.widget.TextView[@text=\"CONTACT LENSES\"]")
	    WebElement CategorySelectionCL;
	    
	    @FindBy(id="com.titan.eyecare:id/unitySurfaceView")
	    WebElement gameView;
	    
	    
	    @FindBy(xpath="//android.widget.RelativeLayout[@resource-id=\"com.titan.eyecare:id/rl_trouble_whatsapp\"]/android.widget.LinearLayout")
	    WebElement WhatsAppelement;
	    
	    @FindBy(xpath="//android.widget.RelativeLayout[@resource-id=\"com.titan.eyecare:id/rl_trouble_call\"]/android.widget.LinearLayout")
	    WebElement phoneCallElement;
	    
	    @FindBy(xpath="//android.widget.RelativeLayout[@resource-id=\"com.titan.eyecare:id/rl_trouble_store\"]/android.widget.LinearLayout")
	    WebElement locateStoreElement;
	    
	    @FindBy(xpath="//android.widget.RelativeLayout[@resource-id=\"com.titan.eyecare:id/rl_trouble_quiz\"]/android.widget.LinearLayout")
	    WebElement takeAQuizElement;
	    
	    String path=null;
	    int totalBanners = 0;
	    
	    
	    @FindBy(id="com.titan.eyecare:id/edt_find_eye")
	    WebElement lens;
	    
	    @FindBy(id="com.titan.eyecare:id/edt_find_bridge")
	    WebElement bridge;
	    
	    @FindBy(id="com.titan.eyecare:id/edt_find_temple")
	    WebElement temple;
	    
	    @FindBy(id="com.titan.eyecare:id/cardview_find_right")
	    WebElement find;
	    
	    @FindBy(xpath="//android.widget.TextView[@text=\"My Measurements:\"]")
	    WebElement messurementText;
	    
	    @FindBy(id="com.whatsapp.w4b:id/smb_title")
	    WebElement whatsAppPage;
	    
	    @FindBy(id="com.google.android.dialer:id/digits")
	    WebElement dialPage;
	    
	    @FindBy(id="com.titan.eyecare:id/txt_store_list_header")
	    WebElement findAStore;
	    
	    @FindBy(id="com.titan.eyecare:id/txt_btn_title")
	    WebElement getStartedQuiz;
	    
	    
	    @FindBy(id="com.titan.eyecare:id/txt_contact_title")
	    WebElement conactText;
	    
	    @FindBy(id="com.titan.eyecare:id/img_contact_call")
	    WebElement contactIcon;
	    
	    @FindBy(id="com.titan.eyecare:id/img_contact_whatsapp")
	    WebElement whatsAppIcon;
	    
	    @FindBy(id="com.titan.eyecare:id/img_contact_message")
	    WebElement mailIcon;
	    
	    @FindBy(xpath="//android.widget.TextView[@resource-id=\"android:id/text1\" and @text=\"Gmail\"]")
	    WebElement mailPopup;
	    

	    public void validateAllBanners(String category) throws IOException
	    {
	    	 
	    	String categoryText=getText(CategorySelection);
	    	if(categoryText.equalsIgnoreCase(category))
	    	{
	    		click(CategorySelection);
	    		 totalBanners = dots.size();
	 	        System.out.println("number of banners are:"+totalBanners);
	    	}else if(categoryText.equalsIgnoreCase(category)) {
	    		click(CategorySelectionCL);
	    		 totalBanners = dots.size();
	 	        System.out.println("number of banners on eyeglasses:"+totalBanners);
	    	}else {
	    		totalBanners = dots.size();
	 	        System.out.println("number of banners on eyeglasses:"+totalBanners);
	    	}
	    	
	        

	        for(int i=0;i<totalBanners;i++)
	        {
	            test.info("Clicking Banner : " + (i+1));

	            banners.get(0).click();

	            verifyLandingPage();
	            path=captureScreenshot("Banner"+(i+1));
	            test.addScreenCaptureFromPath(path);

	            driver.navigate().back();

	            if(i < totalBanners-1)
	            {
	                swipeLeft();
	            }
	        }
	        test.info("All banners are verified");
	    }
	
	    
	    
	    public void verifyLandingPage()
	    {
	        try
	        {
	            visibilityOf(productListingTitle);

	            test.pass("Landing page opened successfully");
	        }
	        catch(Exception e)
	        {
	            test.fail("Landing page not opened");
	            throw e;
	        }
	    }
	
	    
	       //VTO 
	    public void clickVTO() throws IOException, InterruptedException
	    {
	    	test.info("VTO verification started");
	    	driver.findElement(AppiumBy.androidUIAutomator( "new UiScrollable(new UiSelector().scrollable(true))" +
				        ".scrollIntoView(new UiSelector().text(\"Virtual Try-On\"))")).click();
	    	
	    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	    	visibilityOf(gameView);
	    	wait.until(driver -> gameView.getRect().getHeight() > 0
	                  && gameView.getRect().getWidth() > 0);
	    	
	    	
	    	test.info("VTO page displayed");
	    	
	    	assertTrue(gameView.isDisplayed());
	    	System.out.println(gameView.isDisplayed());
	    	System.out.println(gameView.getRect());
	    	Thread.sleep(1500);
	    	path = captureScreenshot("VTO Page");
	    	test.addScreenCaptureFromPath(path);
	    }
	    
	    //Find my fit
	    public void stepsofFineMyFit(String category, String lenstext,String bridgetexr, String templetext) throws IOException
	    {
	    	
	    	String categoryText=getText(CategorySelection);
	    	if(categoryText.equalsIgnoreCase(category))
	    	{
	    		click(CategorySelection);
	    	}
	    	test.info("Process of Fine my fit started");
	    	driver.findElement(AppiumBy.androidUIAutomator( "new UiScrollable(new UiSelector().scrollable(true))" +
			        ".scrollIntoView(new UiSelector().text(\"Find Your Perfect Fit\"))"));
	    	sendKeys(lens, lenstext);
	    	sendKeys(bridge, bridgetexr);
	    	sendKeys(temple, templetext);
	    	((JavascriptExecutor) driver).executeScript(
	    	        "mobile: scrollGesture",
	    	        Map.of(
	    	            "left", 100,
	    	            "top", 100,
	    	            "width", 800,
	    	            "height", 1200,
	    	            "direction", "down",
	    	            "percent", 0.2));
	    	
	    	click(find);
	    	visibilityOf(messurementText);
	    	assertTrue(messurementText.isDisplayed());
	    	path=captureScreenshot("PLP page with find my fit");
	    	test.addScreenCaptureFromPath(path);
	    }
	    
	    
	    //Have trouble section
	    public void haveTrobuleSection() throws IOException
	    {
	    	SoftAssert sa = new SoftAssert();
	    	driver.findElement(AppiumBy.androidUIAutomator( "new UiScrollable(new UiSelector().scrollable(true))" +
			        ".scrollIntoView(new UiSelector().text(\"Have Trouble Deciding?\"))"));
	    	test.info("Have Trouble Deciding section verification started");
	    	click(WhatsAppelement);
	    	test.info("Verifying WhatsApp section");
	    	visibilityOf(whatsAppPage);
	    	//assertTrue(whatsAppPage.isDisplayed());
	    	sa.assertTrue(whatsAppPage.isDisplayed(), "Whats app page is not loaded");
	    	path = captureScreenshot("WhatsApp Page");
	    	test.addScreenCaptureFromPath(path);
	    	driver.navigate().back();
	    	click(phoneCallElement);
	    	test.info("Dail page is Loading");
	    	visibilityOf(dialPage);
	    	//assertTrue(dialPage.isDisplayed());
	    	sa.assertTrue(dialPage.isDisplayed(), "Dial Page is not loaded");
	    	path= captureScreenshot("Dial page");
	    	test.addScreenCaptureFromPath(path);
	    	driver.navigate().back();
	    	driver.navigate().back();
	    	
	    	click(locateStoreElement);
	    	test.info("Store PAge Loading");
	    	visibilityOf(findAStore);
	    	//assertTrue(findAStore.isDisplayed());
	    	sa.assertTrue(findAStore.isDisplayed(), "Store Page os not Loaded");
	    	path=captureScreenshot("Find A Store Page");
	    	test.addScreenCaptureFromPath(path);
	    	driver.navigate().back();
	    	click(takeAQuizElement);
	    	test.info("Quiz page is loading");
	    	visibilityOf(getStartedQuiz);
	    //	assertTrue(getStartedQuiz.isDisplayed());
	    	sa.assertTrue(getStartedQuiz.isDisplayed(), "Quiz page is not loaded");
	    	path= captureScreenshot("Quiz Page");
	    	test.addScreenCaptureFromPath(path);
	    	driver.navigate().back();
	    	
	    }
	    //Contact us
	    public void contactUS() throws IOException
	    {
	    	SoftAssert sa = new SoftAssert();
	    	driver.findElement(AppiumBy.androidUIAutomator( "new UiScrollable(new UiSelector().scrollable(true))" +
			        ".scrollIntoView(new UiSelector().text(\"Contact Us\"))"));
	    	
	    	test.info("Verification of Contact us section started");
	    	click(contactIcon);
	    	test.info("Dail page is Loading");
	    	visibilityOf(dialPage);
	    	//assertTrue(dialPage.isDisplayed());
	    	sa.assertTrue(dialPage.isDisplayed(), "Dial Page is not loaded");
	    	path= captureScreenshot("Dial page");
	    	test.addScreenCaptureFromPath(path);
	    	driver.navigate().back();
	    	driver.navigate().back();
	    	
	    	click(whatsAppIcon);
	    	test.info("Verifying WhatsApp section");
	    	visibilityOf(whatsAppPage);
	    	//assertTrue(whatsAppPage.isDisplayed());
	    	sa.assertTrue(whatsAppPage.isDisplayed(), "Whats app page is not loaded");
	    	path = captureScreenshot("WhatsApp Page");
	    	test.addScreenCaptureFromPath(path);
	    	driver.navigate().back();
	    	
	    	click(mailIcon);
	    	test.info("Verification of mail page started");
	    	visibilityOf(mailPopup);
	    	sa.assertTrue(mailPopup.isDisplayed(), "Mail options are not displayed");
	    	path = captureScreenshot("Mail options");
	    	test.addScreenCaptureFromPath(path);
	    	driver.navigate().back();
	    	
	    	
	    	
	    	
	    }
}
