package com.nishikatakagi.ProductDigital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutItemDTO {
    private int cardTypeId;
    private int quantity;

    @Override
    public String toString() {
        return "CheckoutItemDTO [cardTypeId=" + cardTypeId + ", quantity=" + quantity + "]";
    }
}
