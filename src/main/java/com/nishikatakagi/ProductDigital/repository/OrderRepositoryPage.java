package com.nishikatakagi.ProductDigital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.nishikatakagi.ProductDigital.model.Order;

@Repository
public interface OrderRepositoryPage extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
}
