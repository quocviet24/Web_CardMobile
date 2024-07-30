package com.nishikatakagi.ProductDigital.service;

import org.springframework.validation.BindingResult;

import com.nishikatakagi.ProductDigital.dto.UserLoginRequestDto;

public interface AuthService {
    BindingResult Login(UserLoginRequestDto userLoginRequestDto, BindingResult result);
}
