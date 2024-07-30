package com.nishikatakagi.ProductDigital.dto;

import com.nishikatakagi.ProductDigital.model.CardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDTO {
    CardType cardType;
    String seriNumber;
    String cardNumber;
    Date expiryDate;
}
