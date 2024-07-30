package com.nishikatakagi.ProductDigital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nishikatakagi.ProductDigital.dto.GoogleUserDto;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.mapper.UserRegisterMapper;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/oauth2")
public class OauthLoginController {
    private static final Logger log = LoggerFactory.getLogger(OauthLoginController.class);

    @Autowired
    UserService userService;
    @Autowired
    HttpSession session;
    @Autowired
    private UserRegisterMapper userRegisterMapper;

    @GetMapping("/login")
    public String oauth2Login() {
        // This method can be left empty, Spring Security will handle the redirect
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/google")
    public String getGoogleUser(OAuth2AuthenticationToken authentication) {
        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Register the JavaTimeModule
        GoogleUserDto dto = mapper.convertValue(attributes, GoogleUserDto.class);
        log.warn(dto.toString());

        if (userService.checkEmailExist(dto.getEmail())){
            //Get user and save to session
            User user = userService.findUserDBByUserEmail(dto.getEmail());
            UserSessionDto userSessionDto = new UserSessionDto();
            BeanUtils.copyProperties(user, userSessionDto);
            session.setAttribute("user_sess", userSessionDto); 
                return "redirect:/";
        } else {
            //Register new user
            User user = ParseGoogleUserToUserDB(dto);
            userService.saveUser(user);
            //Save to session
            UserSessionDto userSessionDto = new UserSessionDto();
            BeanUtils.copyProperties(user, userSessionDto);
            session.setAttribute("user_sess", userSessionDto);
            return "redirect:/";
        }    
    }

    private User ParseGoogleUserToUserDB(GoogleUserDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getGivenName());
        user.setFirstName(dto.getName());
        user.setPassword(UUID.randomUUID().toString());
        user.setRoleId(2);
        user.setVerified(true);
        user.setDeleted(false);
        user.setCreatedDate(Date.valueOf(LocalDateTime.now().toLocalDate()));
        return user;
    }
}
