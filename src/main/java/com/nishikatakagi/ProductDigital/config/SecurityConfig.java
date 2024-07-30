package com.nishikatakagi.ProductDigital.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http.
//                authorizeHttpRequests(auth -> {
//                    auth.requestMatchers("/**"
////                            "/login/**",
////                            "/assets/**",
////                            "/login/assets/css/**"
//                    ).permitAll();
//                    auth.anyRequest().authenticated();
//                })
//                .oauth2Login(Customizer.withDefaults())
//                .formLogin(Customizer.withDefaults())
//                .build();
//    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.anyRequest().permitAll();
                })
                .formLogin(form -> form
                        .loginPage("/oauth2/login")
                )
                .logout(logout -> logout
                        .logoutUrl("/oauth2/logout")
                        .logoutSuccessUrl("/")
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/login")
                        .defaultSuccessUrl("/oauth2/google", true)
                )
                .build();
    }
}
