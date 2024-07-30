package com.nishikatakagi.ProductDigital.repository;

import com.nishikatakagi.ProductDigital.model.Transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    public Page<Transaction> findAll(Specification<Transaction> spec, Pageable pageable);
}
