package com.nishikatakagi.ProductDigital.service_impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.nishikatakagi.ProductDigital.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
@EnableAsync
@Service
public class EmailServiceImpl implements EmailService{
    @Value("${api.base.url}")
    private String apiBaseUrl;
    @Autowired
    public JavaMailSender mailSender;
    @Autowired
    public TemplateEngine templateEngine;

    @Transactional
    @Async
    public void sendPasswordResetEmail(String email, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("[ShopGrids] Yêu cầu đặt lại mật khẩu");

        // Prepare the Thymeleaf context and template
        Context context = new Context();
        String url = apiBaseUrl + "forgotPassword/reset/" + token;
        context.setVariable("resetPasswordUrl", url);

        // Correctly reference the template with subdirectory
        String htmlMsg = templateEngine.process("emails/password-reset", context);
        helper.setText(htmlMsg, true);
        mailSender.send(message);
    }

    @Async
    @Override
    public void sendEmailChangeConfirmationEmail(String email, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("[ShopGrids] Xác nhận thay đổi email");

        Context context = new Context();
        context.setVariable("confirmEmailChangeUrl", apiBaseUrl + "/profile/change-email/" + token); 

        String htmlMsg = templateEngine.process("emails/email-change-confirmation", context); 
        helper.setText(htmlMsg, true);

        mailSender.send(message);
    }

    @Async
    @Override
    public void sendRegistrationVerificationEmail(String email, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("[ShopGrids] Xác minh tài khoản");

        Context context = new Context();
        context.setVariable("verifyRegistrationUrl", apiBaseUrl + "/register/verify/" + token); 

        String htmlMsg = templateEngine.process("emails/registration-verification", context); 
        helper.setText(htmlMsg, true);

        mailSender.send(message);
    }
    
}
