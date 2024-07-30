package com.nishikatakagi.store.mapper;

import com.nishikatakagi.store.models.Product;
import com.nishikatakagi.store.models.ProductHistory;

 public class ProductMapper {

	public static ProductHistory convertt (Product product) {
		ProductHistory ph = new ProductHistory(product.getId(),product.getName(),product.getBrand(),product.getCategory(),product.getPrice(),product.getDescription(),product.getCreateAt(),product.getImgFileName());
		return ph;
	}
	
	public static Product convertt (ProductHistory product) {
		Product ph = new Product(product.getId(),product.getName(),product.getBrand(),product.getCategory(),product.getPrice(),product.getDescription(),product.getCreateAt(),product.getImgFileName());
		return ph;
	}
}
