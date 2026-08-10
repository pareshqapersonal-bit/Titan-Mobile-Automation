	package Utilities;
	
	import java.util.ArrayList;
	import java.util.List;
	
	import models.CartProduct;
	
	public class CartDataMapper {
	
	    private static final String SHEET_NAME = "TestData";
	
	    public static List<CartProduct> getProducts(int rowNum) {
	
	        ExcelReader excel = new ExcelReader();
	        
	        try {
	        List<CartProduct> products = new ArrayList<>();
	
	        // Frame
	        addProductIfPresent(
	                products,
	                "Frame",
	                excel.getCellData(SHEET_NAME, rowNum, "FrameSKU"));
	
	        // Eyeglass
	        addProductIfPresent(
	                products,
	                "Eyeglass",
	                excel.getCellData(SHEET_NAME, rowNum, "EyeglassSKU"));
	
	        // Sunglass
	        addProductIfPresent(
	                products,
	                "Sunglass",
	                excel.getCellData(SHEET_NAME, rowNum, "SunglassSKU"));
	
	        // Contact Lens
	        addProductIfPresent(
	                products,
	                "ContactLens",
	                excel.getCellData(SHEET_NAME, rowNum, "CLSKU"));
	     // Powered Sunglass
	        addProductIfPresent(
	                products,
	                "PoweredSunglass",
	                excel.getCellData(SHEET_NAME, rowNum, "PoweredSunglassSKU"));
	        // Reading Glass
	        addProductIfPresent(
	                products,
	                "ReadingGlass",
	                excel.getCellData(SHEET_NAME, rowNum, "ReadingGlassSKU"));
	        
	       //Computer Glass
	        addProductIfPresent(
	                products,
	                "ComputerGlass",
	                excel.getCellData(SHEET_NAME, rowNum, "ComputerGlassSKU"));
	        
	        // Accessories
	        addProductIfPresent(
	                products,
	                "Accessories",
	                excel.getCellData(SHEET_NAME, rowNum, "AccessoriesSKU"));
	
	        return products;
	        } finally {
	            excel.closeWorkbook();
	        }
	    }
	    
	   
	
	    private static void addProductIfPresent(List<CartProduct> products,
	                                            String category,
	                                            String sku) {
	
	        if (sku != null && !sku.trim().isEmpty()) {
	            products.add(new CartProduct(category, sku.trim()));
	        }
	    }
	}