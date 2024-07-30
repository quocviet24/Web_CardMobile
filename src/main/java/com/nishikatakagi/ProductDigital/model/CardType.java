package com.nishikatakagi.ProductDigital.model;

import java.util.Date;

import jakarta.persistence.Column;
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
@Data
@Table(name = "card_types")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardType {

    public CardType() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @ManyToOne
    @JoinColumn(name = "publisher_id")
    Publisher publisher;
    @Column(columnDefinition = "decimal(10,2)")
    double unitPrice;
    int inStock;
    int soldQuantity;
    Boolean isDeleted;
    Date deletedDate;
    Integer deletedBy;
    Date createdDate;
    Integer createdBy;

    @Override
    public String toString() {
        return "Id " + id + " PublisherId " + publisher.getId();
    }
}
