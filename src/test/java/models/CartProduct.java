package models;

public class CartProduct {

    private String category;
    private String sku;
    private String paymentMethod;

    public CartProduct(String category, String sku) {
        this.category = category;
        this.sku = sku;
    }

    public String getCategory() {
        return category;
    }

    public String getSku() {
        return sku;
    }
    
    public String getPaymentMethod() {
		return paymentMethod;
	}
    
    public void setPaymentMethod(String paymentMethod) {
    			this.paymentMethod = paymentMethod;
    }
}