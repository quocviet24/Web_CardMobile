package com.nishikatakagi.ProductDigital.repository;

import com.nishikatakagi.ProductDigital.model.CardType;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardTypeRepository extends JpaRepository<CardType, Integer> {
    List<CardType> findByPublisherId(int publisherId);
    Page<CardType> findAll(Pageable pageable);
    Page<CardType> findAll(Specification<CardType> spec, Pageable pageable);
    //Lacel

    CardType findByPublisherIdAndUnitPrice(Integer publisher_id, Integer unit_price);
    List<CardType> findByUnitPrice(Integer unitPrice);
}

