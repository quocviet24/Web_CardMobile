package com.nishikatakagi.ProductDigital.service;

import org.springframework.stereotype.Service;

import com.nishikatakagi.ProductDigital.model.TokenType;
import com.nishikatakagi.ProductDigital.model.User;

@Service
public interface TokenService {
    String generateToken(User user, TokenType tokenType);
    boolean validateToken(String token, TokenType tokenType);
    void removeExpiredTokens();
}
