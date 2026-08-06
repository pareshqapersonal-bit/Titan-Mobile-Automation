package POM;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utilities.CommonUtils;
import io.appium.java_client.android.AndroidDriver;
import models.CartProduct;

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

    public CartPageElements(AndroidDriver driver) {

        this.driver = driver;

        PageFactory.initElements(driver, this);

        frameCart = new FrameCart(driver);
        eyeglassCart = new EyeglassCart(driver);
        contactLensCart = new ContactLensCart(driver);
        rxSunglassCart = new RxSunglassCart(driver);
        sunglassCart = new SunglassCart(driver);
        readingGlasssesCart = new ReadingGlasssesCart(driver);
        computerGlassesCart = new ComputerGlassesCart(driver);
        accessoriesCart = new AccessoriesCart(driver);
    }

    @FindBy(id="com.titan.eyecare:id/rl_toolbar_search")
    WebElement searchClick;

    @FindBy(id="com.titan.eyecare:id/edt_search")
    WebElement searchField;

    @FindBy(id="com.titan.eyecare:id/txt_product_name")
    WebElement productSelection;

    @FindBy(xpath="//android.widget.ImageView[@resource-id='com.titan.eyecare:id/img_back']")
    WebElement backButton;
    
    @FindBy(id="com.titan.eyecare:id/img_toolbar_back")
    WebElement pdpBackButton;
    
    @FindBy(id="com.titan.eyecare:id/img_back")
    WebElement searchBackButton;

    public void addProductsToCart(List<CartProduct> products) {

        for(int i=0;i<products.size();i++) {

            CartProduct product = products.get(i);

            searchProduct(product.getSku());

            dispatchProduct(product);

            if(i < products.size()-1) {
            	System.out.println("Navigating back to search page for next product");
                backToSearch();
            }
        }
    }

    public void searchProduct(String sku) {

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

        System.out.println("Clicking PDP Back");

        click(pdpBackButton);

        System.out.println("Clicked PDP Back");

        click(pdpBackButton);

        System.out.println("Clicking Search Back");

        click(searchBackButton);

        System.out.println("Clicked Search Back");
    }
}