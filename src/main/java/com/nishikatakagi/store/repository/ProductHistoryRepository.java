package com.nishikatakagi.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nishikatakagi.store.models.ProductHistory;

public interface ProductHistoryRepository extends JpaRepository<ProductHistory,Integer>{

}
