package com.nishikatakagi.ProductDigital.config;

import java.time.Clock;

import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nishikatakagi.ProductDigital.common.Captcha;
import com.nishikatakagi.ProductDigital.filter.AdminFilter;
import com.nishikatakagi.ProductDigital.filter.BeforeLoginFilter;
import com.nishikatakagi.ProductDigital.filter.LoginFilter;
import com.nishikatakagi.ProductDigital.mapper.UserRegisterMapper;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @SuppressWarnings("null")
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/images/publisher/**")
                .addResourceLocations("classpath:/static/assets/images/publisher/");
    }
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone(); // Use the system's default clock for the system's default time zone
    }

    @Bean
    public PhysicalNamingStrategy physicalNamingStrategy() {
        return new CustomPhysicalNamingStrategy();
    }
    @Bean
    public UserRegisterMapper userRegisterMapper() {
        return new UserRegisterMapper();
    }
    @Bean
    Captcha captcha() {
        return new Captcha();
    }

    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilter() {
        //customer access
        FilterRegistrationBean<LoginFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoginFilter());
        registrationBean.addUrlPatterns("/profile/*","/profile");
        registrationBean.addUrlPatterns("/changepassword");
        registrationBean.addUrlPatterns("/cart/*","/cart");
        registrationBean.addUrlPatterns("/order/*","/order");
        registrationBean.addUrlPatterns("/orderAdmin/*","/orderAdmin");
        registrationBean.addUrlPatterns("/publisher/*","/publisher");
        
        return registrationBean;
    }
    @Bean
    public FilterRegistrationBean<BeforeLoginFilter> beforeLoginFilter(){
        //just before login
        FilterRegistrationBean<BeforeLoginFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new BeforeLoginFilter());
        registrationBean.addUrlPatterns("/register/*","/register");
        registrationBean.addUrlPatterns("/login");
        registrationBean.addUrlPatterns("/forgotpassword/*","/forgotpassword");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AdminFilter> adminFilter() {
        //admin access
        FilterRegistrationBean<AdminFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AdminFilter());
        registrationBean.addUrlPatterns("/account/*","/account");
        registrationBean.addUrlPatterns("/publisher/*","/publisher");
        registrationBean.addUrlPatterns("/admin/*","/admin");
        registrationBean.addUrlPatterns("/cardType/*","/cardType");
        registrationBean.addUrlPatterns("/cardAdmin/*","/cardAdmin");
        registrationBean.addUrlPatterns("/statistic/*","/statistic");
        registrationBean.addUrlPatterns("/icon");
        registrationBean.addUrlPatterns("/transaction");
        return registrationBean;
    }
}
