package com.nishikatakagi.ProductDigital.controller;

import com.nishikatakagi.ProductDigital.common.CryptoUtil;
import com.nishikatakagi.ProductDigital.dto.UserLoginRequestDto;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.service.AuthService;
import com.nishikatakagi.ProductDigital.service.CaptchaService;
import com.nishikatakagi.ProductDigital.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/login")
@Controller
public class LoginController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService service;
    @Autowired
    private CaptchaService captchaService;

    private final HttpSession httpSession;

    @Autowired
    private HttpServletResponse response;

    // HashMap không an toàn khi sử dụng trong môi trường đa luồng.
    // ConcurrentHashMap là một lựa chọn tốt hơn khi cần thao tác trên map từ nhiều
    // luồng.
    public Map<Integer, String[]> listCaptcha = new ConcurrentHashMap<>();

    public Map<Integer, String> captchaValue = new ConcurrentHashMap<Integer, String>();

    private final Map<String, Integer> listCheckUserLogin = new HashMap<>();

    public LoginController(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @GetMapping("")
    public String ShowLoginPage(Model model, @CookieValue(value = "password", required = false) String password,
            @CookieValue(value = "username", required = false) String userName) {
        if (httpSession.getAttribute("user_sess") != null) {
            return "redirect:";
        }
        UserLoginRequestDto userDto = new UserLoginRequestDto();
        if (userName != null) {
            try {
                userDto.setUserName(CryptoUtil.decrypt(userName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (password != null) {
            try {
                userDto.setPassword(CryptoUtil.decrypt(password));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("userLoginRequestDto", userDto);

        // kiem tra gia tri passwordResetted co bang voi TRUE khong
        boolean passwordResetted = Boolean.TRUE.equals(model.asMap().get("passwordResetted"));
        model.addAttribute("passwordResetted", passwordResetted);

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
        captchaValue.put(idCaptcha, value);
        // Lưu trữ captcha id và các giá trị captcha chuyển đổi sang html
        listCaptcha.put(idCaptcha, captchaService.captchaValueHTML(one, two, three, four));
        // set model của id mới nhất
        model.addAttribute("idCaptcha", idCaptcha);
        model.addAttribute("captchaSvg", listCaptcha);

        // Remove data not use
        TimerTask task = new TimerTask() {
            public void run() {
                captchaValue.remove(idCaptcha);
                listCaptcha.remove(idCaptcha);
                System.out.println("Data removed! in login");
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 60 * 1000);
        return "publics/login";
    }


    @PostMapping("/post")
    public String Login(@ModelAttribute("userLoginRequestDto") @Valid UserLoginRequestDto userLoginRequestDto,
            BindingResult result, Model model, @RequestParam("idCaptcha") int idCaptcha,
            @RequestParam(value = "rememberMe", required = false) boolean rememberMe) {
        result = authService.Login(userLoginRequestDto, result);
        // Nếu có lỗi ở login password
        if (result.hasFieldErrors("password")) {
            // Nếu trong danh sách của có username này thì tạo mới trong Map với value = 0
            listCheckUserLogin.putIfAbsent(userLoginRequestDto.getUserName(), 0);
            if (listCheckUserLogin.get(userLoginRequestDto.getUserName()) < 5) {
                listCheckUserLogin.replace(userLoginRequestDto.getUserName(),
                        listCheckUserLogin.get(userLoginRequestDto.getUserName()) + 1);
            }
        }

        User userBD = service.findByUsername(userLoginRequestDto.getUserName());
        // Tìm giá trị captcha theo idCaptcha lưu trong map
        String value = captchaValue.get(idCaptcha);

        if (!userLoginRequestDto.getCaptcha().equals(value)) {
            if (captchaValue.get(idCaptcha) == null) {
                result.addError(new FieldError("userLoginRequestDto", "captcha", "Captcha đã hết hạn"));
            } else {
                result.addError(new FieldError("userLoginRequestDto", "captcha", "Captcha không đúng"));
            }

        } else {
            captchaValue.remove(idCaptcha);
            listCaptcha.remove(idCaptcha);
            model.addAttribute("listCapthca_ID", captchaValue);
            model.addAttribute("captchaSvg", listCaptcha);
        }

        if (result.hasErrors() || (listCheckUserLogin.get(userLoginRequestDto.getUserName()) != null)
                && listCheckUserLogin.get(userLoginRequestDto.getUserName()) >= 5) {
            model.addAttribute("captchaSvg", captchaService.createCaptcha());
            model.addAttribute("userLoginRequestDto", userLoginRequestDto);
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
            captchaValue.put(idCaptchaAgain, newValue);
            // Lưu trữ captcha id và các giá trị captcha chuyển đổi sang html
            listCaptcha.put(idCaptchaAgain, captchaService.captchaValueHTML(one, two, three, four));
            // set model của id mới nhất
            model.addAttribute("idCaptcha", idCaptchaAgain);
            model.addAttribute("captchaSvg", listCaptcha);

            // Remove data not use
            TimerTask task = new TimerTask() {
                public void run() {
                    captchaValue.remove(idCaptcha);
                    listCaptcha.remove(idCaptcha);
                }
            };

            Timer timer = new Timer();
            timer.schedule(task, 60 * 1000);

            // Nếu số lần sau = 5 thì báo lỗi vào không cho đăng nhập
            if (listCheckUserLogin.get(userLoginRequestDto.getUserName()) != null
                    && listCheckUserLogin.get(userLoginRequestDto.getUserName()) >= 5) {
                model.addAttribute("limitEnterUser", true);
                TimerTask taskRemoveBanAccount5M = new TimerTask() {
                    public void run() {
                        listCheckUserLogin.remove(userLoginRequestDto.getUserName());
                    }
                };
                timer.schedule(taskRemoveBanAccount5M, 60 * 1000);
            }

            return "publics/login";
        } else {
            UserSessionDto userSessionDto = new UserSessionDto();
            // Copy parameter from user to userSession dto
            BeanUtils.copyProperties(userBD, userSessionDto);
            // Save to session
            httpSession.setAttribute("user_sess", userSessionDto);

            // Remember me - lưu trữ tài khoản vào cookie nếu người dùng tích chọn
            if (rememberMe) {
                try {
                    Cookie usernameCookie = new Cookie("username",
                            CryptoUtil.encrypt(userLoginRequestDto.getUserName()));
                    Cookie passwordCookie = new Cookie("password",
                            CryptoUtil.encrypt(userLoginRequestDto.getPassword()));
                    passwordCookie.setMaxAge(60 * 60 * 24);
                    usernameCookie.setMaxAge(60 * 60 * 24);
                    response.addCookie(usernameCookie);
                    response.addCookie(passwordCookie);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "redirect:/";
        }
    }
}
