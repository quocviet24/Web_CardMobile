package com.nishikatakagi.ProductDigital.service;

public interface CaptchaService {
    public String[] createCaptcha();
    public int createIDCaptcha();
    public String[] captchaValueHTML(int digitOne, int digitTwo, int digitThree, int digitFour);
}