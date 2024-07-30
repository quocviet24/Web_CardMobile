package com.nishikatakagi.ProductDigital.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardUpdateDTO {
    int id;
    Integer cardTypeId;
    String seriNumber;
    String cardNumber;
    Date expiryDate;
    Boolean isDeleted;
    Date deletedDate;
    Integer deletedBy;
    Date createdDate;
    Integer createdBy;
    Date lastUpdated;
    Integer updatedBy;
}
