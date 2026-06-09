package POM;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.titan.eyestage.Base;

import Utilities.CommonUtils;
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
	    
	    String path=null;
	    int totalBanners = 0;

	    public void validateAllBanners(String category) throws IOException
	    {
	    	 
	    	String categoryText="CONTACT LENSES";
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
	
}
