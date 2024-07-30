package com.nishikatakagi.ProductDigital.service_impl;

import com.nishikatakagi.ProductDigital.model.Transaction;
import com.nishikatakagi.ProductDigital.repository.TransactionRepository;
import com.nishikatakagi.ProductDigital.service.TransactionService;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(Transaction transaction) {
        try {
            transactionRepository.save(transaction);
            System.out.println("Transaction saved: " + transaction);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    public Page<Transaction> getTransactions(int pageNo, int pageSize, String status, String dateFrom,
            String dateTo, String searchColumn, String searchTerm) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "transactionDate"));
        Specification<Transaction> spec = Specification.where(null);

        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("order").get("status"), status));
        }

        if (dateFrom != null && !dateFrom.isEmpty()) {
            LocalDate from = LocalDate.parse(dateFrom);
            spec = spec.and(
                    (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("transactionDate"), from.atStartOfDay()));
        }

        if (dateTo != null && !dateTo.isEmpty()) {
            LocalDate to = LocalDate.parse(dateTo);
            spec = spec.and(
                    (root, query, cb) -> cb.lessThanOrEqualTo(root.get("transactionDate"), to.atTime(LocalTime.MAX)));
        }

        if (searchColumn != null && !searchColumn.isEmpty() && searchTerm != null && !searchTerm.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                switch (searchColumn) {
                    case "orderId":
                        try {
                            int orderIdValue = Integer.parseInt(searchTerm);
                            return cb.equal(root.get("order").get("id"), orderIdValue);
                        } catch (NumberFormatException e) {
                            // If the searchTerm is not a valid integer, return a condition that's always
                            // false
                            return cb.isTrue(cb.literal(false));
                        }
                    case "bankId":
                        return cb.like(cb.lower(root.get("bankId")), "%" + searchTerm.toLowerCase() + "%");
                    case "accountNumber":
                        return cb.like(cb.lower(root.get("accountNumber")), "%" + searchTerm.toLowerCase() + "%");
                    case "accountName":
                        return cb.like(cb.lower(root.get("accountName")), "%" + searchTerm.toLowerCase() + "%");
                    case "username":
                        return cb.like(cb.lower(root.get("user").get("username")),
                                "%" + searchTerm.toLowerCase() + "%");
                    default:
                        return null;
                }
            });
        }

        return transactionRepository.findAll(spec, pageable);
    }
}
