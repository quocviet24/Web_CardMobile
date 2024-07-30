package com.nishikatakagi.ProductDigital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nishikatakagi.ProductDigital.dto.UserRegisterRequestDto;
import com.nishikatakagi.ProductDigital.mapper.UserRegisterMapper;
import com.nishikatakagi.ProductDigital.model.Token;
import com.nishikatakagi.ProductDigital.model.TokenType;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.repository.TokenRepository;
import com.nishikatakagi.ProductDigital.service.CaptchaService;
import com.nishikatakagi.ProductDigital.service.EmailService;
import com.nishikatakagi.ProductDigital.service.TokenService;
import com.nishikatakagi.ProductDigital.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@EnableAsync
@RequestMapping("/register")
@Controller
public class RegisterController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    HttpSession session;
    @Autowired
    private UserRegisterMapper userRegisterMapper;

    public Map<Integer, String[]> listCaptchaRegister = new ConcurrentHashMap<>();

    public Map<Integer, String> captchaValueRegister = new ConcurrentHashMap<>();

    public RegisterController() {
    }

    @GetMapping("")
    public String showRegisterPage(Model model) {
        // check if the user is already logged in
        if (session.getAttribute("user_sess") != null) {
            return "redirect:/";
        } else {
            // Tạo một id cho captcha
            int idCaptcha = captchaService.createIDCaptcha();

            // Tạo 4 giá trị captcha và lưu thành 1 string
            String value = "";
            int one = (int) (Math.random() * 10);
            value += one;
            int two = (int) (Math.random() * 10);
            value += two;
            int three = (int) (Math.random() * 10);
            value += three;
            int four = (int) (Math.random() * 10);
            value += four;

            // Lưu trữ giá trị captcha và id vào map
            captchaValueRegister.put(idCaptcha, value);
            // Lưu trữ captcha id và các giá trị captcha chuyển đổi sang html
            listCaptchaRegister.put(idCaptcha, captchaService.captchaValueHTML(one, two, three, four));

            // set model của id mới nhất
            model.addAttribute("idCaptcha", idCaptcha);
            model.addAttribute("captchaSvg", listCaptchaRegister);

            model.addAttribute("user", new UserRegisterRequestDto());
            return "publics/register";
        }

    }

    @PostMapping("")
    public String processRegistrationForm(
            @ModelAttribute("user") @Valid UserRegisterRequestDto user, BindingResult result, Model model,
            @RequestParam("idCaptcha") int idCaptcha, RedirectAttributes redirectAttributes) {

        String captchaValue = captchaValueRegister.get(idCaptcha);

        if (user.getRepassword() != null && !user.getPassword().equals(user.getRepassword())) {
            // check password and re-password
            result.addError(new FieldError("user", "repassword", "Mật khẩu không trùng khớp"));
        }
        if (!user.getPhone().isEmpty() && user.getPhone().length() != 10) {
            // check phone number
            result.addError(new FieldError("user", "phone", "Số điện thoại phải có 10 chữ số"));
        }
        if (!user.getCaptcha().equals(captchaValue)) {
            // check captcha
            result.addError(new FieldError("user", "captcha", "Captcha không đúng!"));
        }
        if (userService.checkUsernameExist(user.getUsername())) {
            // check username exist or not
            result.addError(new FieldError("user", "username", "Tên đăng nhập đã tồn tại"));
        }
        if (userService.checkEmailExist(user.getEmail())) {
            // check email exist or not
            result.addError(new FieldError("user", "email", "Email đã tồn tại"));
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);

            // Tạo một id cho captcha
            int idCaptchaAgain = captchaService.createIDCaptcha();

            // Tạo 4 giá trị captcha và lưu thành 1 string
            String newValue = "";
            int one = (int) (Math.random() * 10);
            newValue += one;
            int two = (int) (Math.random() * 10);
            newValue += two;
            int three = (int) (Math.random() * 10);
            newValue += three;
            int four = (int) (Math.random() * 10);
            newValue += four;

            // Lưu trữ giá trị captcha và id vào map
            captchaValueRegister.put(idCaptchaAgain, newValue);
            // Lưu trữ captcha id và các giá trị captcha chuyển đổi sang html
            listCaptchaRegister.put(idCaptchaAgain, captchaService.captchaValueHTML(one, two, three, four));

            // set model của id mới nhất
            model.addAttribute("idCaptcha", idCaptchaAgain);
            model.addAttribute("captchaSvg", listCaptchaRegister);

            return "publics/register";
        } else {
            User u = userRegisterMapper.toUser(user);
            String token = tokenService.generateToken(u, TokenType.REGISTRATION);
            u.setVerified(false);
            // u.setDeleted(true);
            // session.setAttribute("rawUser-register", u);
            userService.saveUser(u);
            // return "redirect:register/otp";
            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendRegistrationVerificationEmail(user.getEmail(), token);
                } catch (MessagingException e) {
                    // Handle email sending error (e.g., log, notify admin)
                    e.printStackTrace();
                }
            });

            redirectAttributes.addFlashAttribute("verificationEmailSent", true); // Add a flash message
            return "redirect:/login";
        }
    }

    @GetMapping("/verify/{token}")
    public String verifyRegistration(@PathVariable String token, RedirectAttributes redirectAttributes) {
        if (!tokenService.validateToken(token, TokenType.REGISTRATION)) {
            redirectAttributes.addFlashAttribute("error", "Link không hợp lệ hoặc đã hết hạn.");
            return "redirect:/register";
        }

        Token registrationToken = tokenRepository.findByTokenAndTokenType(token, TokenType.REGISTRATION);
        User user = registrationToken.getUser();
        user.setVerified(true); 
        userService.saveUser(user);
        tokenRepository.delete(registrationToken); // Invalidate token

        redirectAttributes.addFlashAttribute("verificationSuccess", true);
        return "redirect:/login"; // Redirect to login after successful verification
    }
}
