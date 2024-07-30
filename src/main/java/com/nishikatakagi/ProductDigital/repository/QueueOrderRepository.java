package com.nishikatakagi.ProductDigital.repository;

import com.nishikatakagi.ProductDigital.model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QueueOrderRepository extends JpaRepository<Queue, Integer> {
    Optional<Queue> findByOrderId(int orderId);
}
