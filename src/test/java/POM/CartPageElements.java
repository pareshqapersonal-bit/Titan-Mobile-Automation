package POM;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import Utilities.CommonUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;

import java.time.Duration;
import java.util.Collections;

import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

public class CartPageElements extends CommonUtils {

    AndroidDriver driver;

    FrameCart frameCart;
    EyeglassCart eyeglassCart;
    ContactLensCart contactLensCart;
    RxSunglassCart rxSunglassCart;
    SunglassCart sunglassCart;
    ReadingGlasssesCart readingGlasssesCart;
    ComputerGlassesCart computerGlassesCart;
    AccessoriesCart accessoriesCart;
    private final String loginUser;
    public CartPageElements(
            AndroidDriver driver,
            String loginUser) {

        this.driver = driver;
        this.loginUser = loginUser;

        PageFactory.initElements(driver, this);

        frameCart =
                new FrameCart(driver, loginUser);

        eyeglassCart =
                new EyeglassCart(driver, loginUser);

        contactLensCart =
                new ContactLensCart(driver, loginUser);

        rxSunglassCart =
                new RxSunglassCart(driver, loginUser);

        sunglassCart =
                new SunglassCart(driver, loginUser);

        readingGlasssesCart =
                new ReadingGlasssesCart(driver, loginUser);

        computerGlassesCart =
                new ComputerGlassesCart(driver, loginUser);

        accessoriesCart =
                new AccessoriesCart(driver, loginUser);
    }

    @FindBy(id="com.titan.eyecare:id/rl_toolbar_search")
    WebElement searchClick;

    @FindBy(id="com.titan.eyecare:id/edt_search")
    WebElement searchField;

    @FindBy(id="com.titan.eyecare:id/txt_product_name")
    WebElement productSelection;
    
    @FindBy(id="com.titan.eyecare:id/txt_btn_title")
    WebElement buyNowCTA;


    @FindBy(xpath="//android.widget.ImageView[@resource-id='com.titan.eyecare:id/img_back']")
    WebElement backButton;
    
    @FindBy(id="com.titan.eyecare:id/img_toolbar_back")
    WebElement pdpBackButton;
    
    
    
    @FindBy(id = "com.titan.eyecare:id/card_dialog_congratulations")
    WebElement cartRulePopup;

    @FindBy(id = "com.titan.eyecare:id/txt_btn_title")
    WebElement continueShoppingCTA;
    
    @FindBy(xpath="//android.widget.TextView[@resource-id='com.titan.eyecare:id/txt_btn_title' and @text='Continue Shopping']")
	WebElement continueShoppingButton;
    
    @FindBy(xpath =
    	    "//android.view.ViewGroup[@resource-id='com.titan.eyecare:id/toolbar_home']" +
    	    "/android.widget.ImageView[@resource-id='com.titan.eyecare:id/img_toolbar_back']")
    	WebElement cartBackButton;
    
    @FindBy(xpath =
    	    "//android.widget.LinearLayout[@resource-id='com.titan.eyecare:id/ll_search_bar']" +
    	    "//android.widget.ImageView[@resource-id='com.titan.eyecare:id/img_back']")
    	WebElement searchBackButton;
    
    @FindBy(id="com.titan.eyecare:id/txt_btn_title")
	WebElement proceedToCheckout;
    
    @FindBy(id="com.titan.eyecare:id/txt_title")
	WebElement cartTitle;
    
    String path = null;

  

    public void addProductsToCart(List<CartProduct> products) throws InterruptedException, IOException {

        for(int i=0;i<products.size();i++) {

            CartProduct product = products.get(i);

            searchProduct(product.getSku());
              visibilityOf(buyNowCTA);
              System.out.println("Buy Now CTA is visible for product: " + product.getSku());
            dispatchProduct(product);
            System.out.println("Product added to cart: " + product.getSku());

            driver.context("NATIVE_APP");

            List<WebElement> continueButtons = driver.findElements(
                    AppiumBy.xpath(
                        "//android.widget.TextView[" +
                        "@resource-id='com.titan.eyecare:id/txt_btn_title' " +
                        "and @text='Continue Shopping']"
                    )
            );

            System.out.println("Continue Shopping button count = "
                    + continueButtons.size());

            if (!continueButtons.isEmpty()) {

                System.out.println("Continue Shopping button found");

                visibilityOf(continueShoppingButton);

                System.out.println(
                    "Continue Shopping CTA is visible and text is: "
                    + continueShoppingButton.getText()
                );

                continueShoppingButton.click();

                System.out.println("Clicked Continue Shopping");
            }
            
            
            if(i < products.size()-1) {
            	System.out.println("Navigating back to search page for next product");
            	
                backToSearch();
                
            }
            
           

        }
        
        System.out.println("All products added to cart");

        visibilityOf(cartTitle);
        visibilityOf(proceedToCheckout);

        path = captureScreenshot("Cart_Page");

        test.info("Cart Page Displayed");
        test.addScreenCaptureFromPath(path);

        assertTrue(
            cartTitle.isDisplayed(),
            "Cart page not displayed"
        );
    }

    public void searchProduct(String sku) {

    	System.out.println("Current Package = " + driver.getCurrentPackage());
    	System.out.println("Current Activity = " + driver.currentActivity());
        click(searchClick);
        sendKeys(searchField, sku);
        click(productSelection);
    }

    private void dispatchProduct(CartProduct product) {

        switch(product.getCategory()) {

        case "Frame":
        	System.out.println("Adding Frame Product to Cart");
            frameCart.addToCart(product);
            break;

        case "Eyeglass":
        	System.out.println("Adding Eyeglass Product to Cart");
            eyeglassCart.addToCart(product);
            break;

        case "ContactLens":
        	System.out.println("Adding Contact Lens Product to Cart");
            contactLensCart.addToCart(product);
            break;

        case "PoweredSunglass":
        	System.out.println("Adding Powered Sunglass Product to Cart");
            rxSunglassCart.addToCart(product);
            break;

        case "Sunglass":
        	System.out.println("Adding Sunglass Product to Cart");
            sunglassCart.addToCart(product);
            break;
            
        case "ReadingGlass":
        	System.out.println("Adding Reading Glass Product to Cart");
			readingGlasssesCart.addToCart();
			break;
		
	    case "ComputerGlass":
	         System.out.println("Adding Computer Glass Product to Cart");
	         computerGlassesCart.addToCart();
	         break;
	        
	    case "Accessories":
	         System.out.println("Adding Accessories Product to Cart");
	         accessoriesCart.addToCart();
	         break;
	         


        default:
            throw new RuntimeException("Unknown Category : " + product.getCategory());
        }
    }

    /**
	 * Navigates back to the search page after adding a product to the cart.
	 * This method clicks the back button on the Product Detail Page (PDP) and then
	 * clicks the back button on the search page to return to the search results.
	 */
    public void backToSearch() {

        System.out.println("Clicking Cart Back");

      click(cartBackButton);

        System.out.println("Clicked Cart Back");

        visibilityOf(buyNowCTA);

        System.out.println("PDP loaded");

		/*
		 * List<WebElement> backs = driver.findElements(
		 * AppiumBy.id("com.titan.eyecare:id/img_toolbar_back") );
		 * 
		 * System.out.println("Back buttons found = " + backs.size());
		 * 
		 * for (WebElement back : backs) { System.out.println( "Displayed = " +
		 * back.isDisplayed() + " | Enabled = " + back.isEnabled() + " | Clickable = " +
		 * back.getAttribute("clickable") ); } System.out.println("Clicking PDP Back");
		 */

       // click(pdpBackButton);
       
        tapPdpBack();
        click(searchBackButton);
        

        System.out.println("Clicked PDP Back");
    }
    
    
    private void handleCartRulePopup() {

        List<WebElement> popup = driver.findElements(
                AppiumBy.id("com.titan.eyecare:id/card_dialog_congratulations"));

        if (!popup.isEmpty()) {

            System.out.println("Cart Rule popup displayed");

            click(continueShoppingCTA);

            System.out.println("Clicked Continue Shopping");
        }
    }
    
    
    //Discount popup
    
    
    private boolean isCartRulePopupDisplayed() {

        try {

            WebDriverWait wait = new WebDriverWait(
                    driver,
                    Duration.ofSeconds(5));

            return wait.until(driver -> {

                int count = driver.findElements(
                        AppiumBy.id(
                                "com.titan.eyecare:id/card_dialog_congratulations"))
                        .size();

                System.out.println("Popup check count = " + count);

                return count > 0;
            });

        } catch (TimeoutException e) {

            System.out.println("Popup did not appear within 5 seconds");

            return false;
        }
    }
    
    
    
    private void tapPdpBack() {

        WebElement back = driver.findElement(
                AppiumBy.id("com.titan.eyecare:id/img_toolbar_back")
        );

        Rectangle rect = back.getRect();

        int x = rect.getX() + rect.getWidth() / 2;
        int y = rect.getY() + rect.getHeight() / 2;

        System.out.println("PDP Back coordinates: x=" + x + ", y=" + y);

        PointerInput finger = new PointerInput(
                PointerInput.Kind.TOUCH,
                "finger"
        );

        Sequence tap = new Sequence(finger, 1);

        tap.addAction(
                finger.createPointerMove(
                        Duration.ZERO,
                        PointerInput.Origin.viewport(),
                        x,
                        y
                )
        );

        tap.addAction(
                finger.createPointerDown(
                        PointerInput.MouseButton.LEFT.asArg()
                )
        );

        tap.addAction(
                finger.createPointerUp(
                        PointerInput.MouseButton.LEFT.asArg()
                )
        );

        driver.perform(Collections.singletonList(tap));
    }
    
}