package models;

public class CartProduct {

    private String category;
    private String sku;

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
}