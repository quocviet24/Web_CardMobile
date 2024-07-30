package com.nishikatakagi.ProductDigital.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "cards")
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Card {
    public Card() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @ManyToOne
	@JoinColumn(name = "card_type_id")
    CardType cardType;
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

    @Override
    public String toString() {
        return "id " + id + " cardType " + cardType.getId() + " seriNumber " + seriNumber + " cardNumber " + cardNumber + " expiryDate " + expiryDate;
    }
}
