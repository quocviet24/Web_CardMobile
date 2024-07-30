package com.nishikatakagi.store.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "productHistory")
public class ProductHistory {
	
    @Id
    private int id;

    private String name;

    private String brand;

    private String category;

    private double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Date createAt;

    private String imgFileName;
	public ProductHistory() {
		// TODO Auto-generated constructor stub
	}
	
	public ProductHistory(int id, String name, String brand, String category, double price, String description,
			Date createAt, String imgFileName) {
		super();
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.category = category;
		this.price = price;
		this.description = description;
		this.createAt = createAt;
		this.imgFileName = imgFileName;
	}



	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	public String getImgFileName() {
		return imgFileName;
	}
	public void setImgFileName(String imgFileName) {
		this.imgFileName = imgFileName;
	}
	@Override
	public String toString() {
		return "ProductHistory [id=" + id + ", name=" + name + ", brand=" + brand + ", category=" + category
				+ ", price=" + price + ", description=" + description + ", createAt=" + createAt + ", imgFileName="
				+ imgFileName + "]";
	}

	
}
