package com.titan.eyestage.v2.models;

public class CartProduct {

	private final String category;
	private final String sku;

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

	@Override
	public String toString() {
		return category + ":" + sku;
	}
}
