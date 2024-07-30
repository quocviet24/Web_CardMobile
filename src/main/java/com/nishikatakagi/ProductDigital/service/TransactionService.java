package com.nishikatakagi.ProductDigital.service;

import com.nishikatakagi.ProductDigital.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {
    public void saveTransaction(Transaction transaction);

    public Page<Transaction> getTransactions(int pageNo, int pageSize, String status, String dateFrom, String dateTo,
            String searchColumn, String searchTerm);
}
