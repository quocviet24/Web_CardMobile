package com.nishikatakagi.ProductDigital.service;

import org.springframework.scheduling.annotation.Async;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

public interface EmailService {
    @Transactional
    @Async
    void sendPasswordResetEmail(String email, String token) throws MessagingException;
    @Async
    void sendEmailChangeConfirmationEmail(String email, String token) throws MessagingException;
    @Async
    void sendRegistrationVerificationEmail(String email, String token) throws MessagingException;
}
