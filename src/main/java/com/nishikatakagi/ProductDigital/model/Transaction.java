package com.nishikatakagi.ProductDigital.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "transactions")
public class Transaction {
    @Id
    String id;
    @OneToOne
    @JoinColumn(name = "order_id")
    Order order;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    String status;
    double amount;
    String accountName;
    String accountNumber;
    Date transactionDate;
    String description;
    String bankId;

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", order=" + order +
                ", user=" + user +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                ", accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", transactionDate=" + transactionDate +
                ", description='" + description + '\'' +
                ", bankId='" + bankId + '\'' +
                '}';
    }
}
