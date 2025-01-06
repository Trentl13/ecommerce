package com.backend.store.ecommerce.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.stereotype.Component;

@Configuration
public class WebSecurityConfig {

    private JWTRequestFilter jwtRequestFilter;//ВРЕМЕННО

    public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors.disable());
        http.addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);//преди да се добави това първо извършваше тази проверка(auth -> auth.anyRequest().authenticated()) преди проверката за authentication в JWTReqeustFilter
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/product").permitAll() //позволяваме за /product да се гледа без aauthentication, понеже продуктите могат да се гледат без регистрация например
                .requestMatchers("/auth/register").permitAll()
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/auth/verify").permitAll()
                .requestMatchers("/auth/resetpasswordemail").permitAll()
                .requestMatchers("/auth/resetpassword").permitAll()
                .requestMatchers("/auth/updateaddress").permitAll()
                .requestMatchers("/auth/insertaddress").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()

                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated());
        return http.build();
    }
}
