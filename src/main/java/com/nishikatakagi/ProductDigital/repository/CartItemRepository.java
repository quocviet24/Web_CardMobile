package com.nishikatakagi.ProductDigital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nishikatakagi.ProductDigital.model.CardType;
import com.nishikatakagi.ProductDigital.model.CartItem;
import com.nishikatakagi.ProductDigital.model.User;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUserId(int userId);

    List<CartItem> findByUserUsername(String username);

    CartItem findByUserIdAndCardTypeId(int userId, int cardTypeId);

    CartItem findById(int cartItemId);

    void deleteById(int id);

    CartItem findByCardTypeAndUser(CardType cardType, User user);

    void deleteByCardTypeId(int cardTypeId);

}
