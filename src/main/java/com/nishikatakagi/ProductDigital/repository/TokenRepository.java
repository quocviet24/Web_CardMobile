package com.nishikatakagi.ProductDigital.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nishikatakagi.ProductDigital.model.Token;
import com.nishikatakagi.ProductDigital.model.TokenType;
import com.nishikatakagi.ProductDigital.model.User;

@Repository 
public interface TokenRepository extends JpaRepository<Token, Integer> {
	Token findByTokenAndTokenType(String token, TokenType tokenType);
    Token findByUserAndTokenType(User user, TokenType tokenType);
    void deleteByUserAndTokenType(User user, TokenType tokenType);
    void deleteByExpiryDateTimeBefore(LocalDateTime now);

}
