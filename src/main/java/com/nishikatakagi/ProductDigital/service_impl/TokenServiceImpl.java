package com.nishikatakagi.ProductDigital.service_impl;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nishikatakagi.ProductDigital.model.Token;
import com.nishikatakagi.ProductDigital.model.TokenType;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.repository.TokenRepository;
import com.nishikatakagi.ProductDigital.service.TokenService;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private Clock clock;
    private final long expirationTimeInMinutes = 5;

    @Override
    public String generateToken(User user, TokenType tokenType) {
        String token = UUID.randomUUID().toString();
        Date expiryDate = Date.from(Instant.now(clock).plusMillis(expirationTimeInMinutes * 60 * 1000));

        Token newToken = new Token();
        newToken.setUser(user);
        newToken.setTokenType(tokenType);
        newToken.setToken(token);
        newToken.setExpiryDateTime(expiryDate);

        tokenRepository.save(newToken);
        return token;
    }

    @Override
    public boolean validateToken(String token, TokenType tokenType) {
        Token foundToken = tokenRepository.findByTokenAndTokenType(token, tokenType);
        if (foundToken == null || !foundToken.getExpiryDateTime().after(new Date())) {
            return false;
        }
        return true;
    }

    @Transactional
    @Scheduled(fixedDelay = 300000) // run every 5 min
    @Override
    public void removeExpiredTokens() {
        tokenRepository.deleteByExpiryDateTimeBefore(LocalDateTime.now()); 
    }

}
