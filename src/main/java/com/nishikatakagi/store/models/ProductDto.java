package com.nishikatakagi.store.models;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ProductDto {

	@NotEmpty(message = "The name is required")
	private String name;
	
	@NotEmpty(message = "The brand is required")
	private String brand;
	
	@NotEmpty(message = "The category is required")
	private String category;
	
	@Min(0)
	private double price;
	
	@Size(min = 10, message = "The description should be at least 10 characters")
	@Size(max = 2000, message = "The description cannot exceed 2000 characters")
	private String description;
	
	private MultipartFile imgFile;
	
	public ProductDto() {
		// TODO Auto-generated constructor stub
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MultipartFile getImgFile() {
		return imgFile;
	}

	public void setImgFile(MultipartFile imgFile) {
		this.imgFile = imgFile;
	}

	@Override
	public String toString() {
		return "ProductDto [name=" + name + ", brand=" + brand + ", category=" + category + ", price=" + price
				+ ", description=" + description + ", imgFile=" + imgFile + "]";
	}

	
	
}
