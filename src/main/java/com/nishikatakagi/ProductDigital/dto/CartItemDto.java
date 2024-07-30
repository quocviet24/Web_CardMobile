package com.nishikatakagi.ProductDigital.dto;

import com.nishikatakagi.ProductDigital.model.CardType;
import com.nishikatakagi.ProductDigital.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private int id;
    private User user;
    private CardType cardType;
    private int quantity;
    private double total;
    // private String imageUrl;

    // You might want to add a method to calculate the total price
    // public Double getTotalPrice() {
    //     return this.quantity * this.unitPrice;
    // }
}
