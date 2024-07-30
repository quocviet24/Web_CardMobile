package com.nishikatakagi.ProductDigital.repository;

import com.nishikatakagi.ProductDigital.model.Card;
import com.nishikatakagi.ProductDigital.model.CardType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    List<Card> findByCardTypeAndIsDeletedOrderByExpiryDateAsc(CardType cardType, Boolean isDeleted);

    Optional<Card> findById(int id);
    @Query("select c from Card  c")
    List<Card>getAllCart();
    boolean existsByCardTypeAndSeriNumber(CardType cardType, String seriNumber);
    Page<Card> findAll(Specification<Card> spec, Pageable pageable);
    }
