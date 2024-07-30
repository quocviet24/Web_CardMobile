package com.nishikatakagi.ProductDigital.service_impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nishikatakagi.ProductDigital.common.Captcha;
import com.nishikatakagi.ProductDigital.service.CaptchaService;

import jakarta.servlet.http.HttpSession;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    @Autowired
    Captcha captcha;
    @Autowired
    HttpSession session;

    public int createIDCaptcha(){
        int random = (int) (Math.random() * 1000000);
        return random;
    }

    @Override
    public String[] createCaptcha() {
        String captchaValue = "";
        String[] captchaSvg = new String[4];

        int random = (int) (Math.random() * 10);
        captchaValue += random;
        captchaSvg[0] = captcha.digitOne.get(random);
        random = (int) (Math.random() * 10);
        captchaValue += random;
        captchaSvg[1] = captcha.digitTwo.get(random);
        random = (int) (Math.random() * 10);
        captchaValue += random;
        captchaSvg[2] = captcha.digitThree.get(random);
        random = (int) (Math.random() * 10);
        captchaValue += random;
        captchaSvg[3] = captcha.digitFour.get(random);

        //session.setAttribute("captchaValue", captchaValue);
        return captchaSvg;
    }

    @Override
    public String[] captchaValueHTML(int digitOne, int digitTwo, int digitThree, int digitFour) {
        String[] captchaSvg = new String[4];

        captchaSvg[0] = captcha.digitOne.get(digitOne);
        captchaSvg[1] = captcha.digitTwo.get(digitTwo);
        captchaSvg[2] = captcha.digitThree.get(digitThree);
        captchaSvg[3] = captcha.digitFour.get(digitFour);

        //session.setAttribute("captchaValue", captchaValue);
        return captchaSvg;
    }

}
