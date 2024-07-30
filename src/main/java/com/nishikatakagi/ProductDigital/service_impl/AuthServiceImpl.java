package com.nishikatakagi.ProductDigital.service_impl;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.nishikatakagi.ProductDigital.dto.UserLoginRequestDto;
import com.nishikatakagi.ProductDigital.model.Token;
import com.nishikatakagi.ProductDigital.model.TokenType;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.repository.TokenRepository;
import com.nishikatakagi.ProductDigital.repository.UserRepository;
import com.nishikatakagi.ProductDigital.service.AuthService;
import com.nishikatakagi.ProductDigital.service.EmailService;
import com.nishikatakagi.ProductDigital.service.TokenService;

import jakarta.mail.MessagingException;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Autowired
    SecurityServiceImpl security;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    TokenService tokenService;
    @Autowired
    EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public BindingResult Login(UserLoginRequestDto userLoginRequestDto, BindingResult result) {
        // find user by username in database
        User user = userRepository.findUserByUsername(userLoginRequestDto.getUserName());

        // create error if user doesn't exit
        if (user == null) {
            result.addError(new FieldError("user", "userName", "Sai tên đăng nhập hoặc mật khẩu"));
            return result;
        }

        if (!user.isVerified()){
            // Generate a new registration token (if it doesn't exist or is expired)
            Token existingToken = tokenRepository.findByUserAndTokenType(user, TokenType.REGISTRATION);
            if (existingToken == null || !tokenService.validateToken(existingToken.getToken(), TokenType.REGISTRATION)) {
                String token = tokenService.generateToken(user, TokenType.REGISTRATION);

                // Send the verification email asynchronously
                CompletableFuture.runAsync(() -> {
                    try {
                        emailService.sendRegistrationVerificationEmail(user.getEmail(), token);
                    } catch (MessagingException e) {
                        e.printStackTrace(); 
                    }
                });
            }
            result.addError(new FieldError("user", "userName", "Tài khoản chưa được xác minh. Chúng tôi vừa gửi lại email xác minh tới bạn. Vui lòng kiểm tra email."));
            
        }
        if (user.isDeleted()){
            result.addError(new FieldError("user", "userName", "Tài khoản của bạn hiện đang bị quản trị viên tạm dừng hoạt động, nếu có thắc mắc vui lòng liên hệ qua hotline hoặc email!!!"));
        }

        // create error if password not correct
        if (!user.getPassword().equals(security.encode(userLoginRequestDto.getPassword()))) {
            result.addError(new FieldError("user", "password", "Sai tên đăng nhập hoặc mật khẩu"));
            return result;
        }
        return result;
    }
}
