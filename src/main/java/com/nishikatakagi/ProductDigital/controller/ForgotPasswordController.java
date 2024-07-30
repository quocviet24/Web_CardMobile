package com.nishikatakagi.ProductDigital.controller;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nishikatakagi.ProductDigital.dto.UserRecoverPasswordDto;
import com.nishikatakagi.ProductDigital.dto.UserResetPasswordDto;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.Token;
import com.nishikatakagi.ProductDigital.model.TokenType;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.repository.TokenRepository;
import com.nishikatakagi.ProductDigital.service.EmailService;
import com.nishikatakagi.ProductDigital.service.TokenService;
import com.nishikatakagi.ProductDigital.service.SecurityService;
import com.nishikatakagi.ProductDigital.service.UserService;

@Controller
@EnableAsync
public class ForgotPasswordController {

    @Autowired
    HttpSession session;

    @Autowired
    private UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    SecurityService security;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    private TokenService tokenService;

    @GetMapping("/forgotPassword")
    public String forgotPassword(Model model) {
        // Check if the user is login or not
        UserSessionDto user = (UserSessionDto) session.getAttribute("user_sess");
        if (user != null) {
            return "redirect:/";
        } else {
            UserRecoverPasswordDto userDTO = new UserRecoverPasswordDto();
            model.addAttribute("user", userDTO);
            return "/publics/forgot-password";
        }
    }

    @PostMapping("/forgotPassword")
    @Transactional
    public String forgotPasswordProcess(@ModelAttribute UserRecoverPasswordDto userDTO, Model model,
            RedirectAttributes redirectAttributes) {

        User user = userService.findUserDBByUserEmail(userDTO.getEmail());
        if (user != null && !user.isDeleted()) {
            // Delete existing password reset tokens for this user
            tokenRepository.deleteByUserAndTokenType(user, TokenType.PASSWORD_RESET);

            // Generate and save a new reset token
            String token = tokenService.generateToken(user, TokenType.PASSWORD_RESET);
            session.setAttribute("passwordResetToken_" + user.getEmail(), token); 
            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendPasswordResetEmail(user.getEmail(), token);
                } catch (MessagingException e) {
                    // Handle email sending error (e.g., log, notify admin)
                    e.printStackTrace();
                }
            });
            return "publics/sent-email-success.html";
        } else {
            model.addAttribute("user", userDTO); // Add userDTO back to the model for form repopulation
            if (user == null) {
                model.addAttribute("error", "Tài khoản không tồn tại");
            } else {
                model.addAttribute("error", "Tài khoản của bạn đã bị khóa");
            }
            return "publics/forgot-password"; // Return to the forgot password page with the error message
        }
    }

    @GetMapping("/forgotPassword/reset")
    public String showForgotPasswordPage() {
        return "redirect:/forgotPassword";
    }

    @GetMapping("/forgotPassword/reset/{token}")
    public String showPage(@PathVariable String token, Model model, RedirectAttributes redirectAttributes) {
        if (token == null || token.isEmpty()) {
            return "redirect:/forgotPassword";
        }
        Token resetToken = tokenRepository.findByTokenAndTokenType(token, TokenType.PASSWORD_RESET);

        // if resetToken is not null and not expired
        if (resetToken != null && resetToken.getExpiryDateTime().after(new Date())) {
            model.addAttribute("email", resetToken.getUser().getEmail());
            model.addAttribute("user", new UserResetPasswordDto());
            return "publics/reset-password.html";
        }

        redirectAttributes.addFlashAttribute("resetTokenExpired", true);
        return "redirect:/forgotPassword";
    }

    @PostMapping("/forgotPassword/reset") // No token in the path
    public String resetPassword(@Valid @ModelAttribute("user") UserResetPasswordDto userResetPasswordDto,
            BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam("email") String email) { // Retrieve token from request parameter

        if (!userResetPasswordDto.getNewPassword().equals(userResetPasswordDto.getConfirmPassword())) {
            bindingResult.addError(
                    new FieldError("user", "confirmPassword", "Mật khẩu mới và xác nhận mật khẩu không khớp!"));
        }

        if (bindingResult.hasErrors()) {
            return "publics/reset-password";
        }

        // Retrieve token from session using email
        String token = (String) session.getAttribute("passwordResetToken_" + email); 

        if (tokenService.validateToken(token, TokenType.PASSWORD_RESET)) { 
            User user = userService.findUserDBByUserEmail(email);
            userService.changePassword(user.getId(), userResetPasswordDto.getNewPassword());

            // Remove token from session after successful reset
            session.removeAttribute("passwordResetToken_" + email);  

            redirectAttributes.addFlashAttribute("passwordResetted", true);
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("resetTokenExpired", true);
            return "redirect:/forgotPassword";
        }
    }

}
