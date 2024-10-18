package com.backend.store.ecommerce.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Configuration
public class WebSecurityConfig { //ВРЕМЕННО
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors.disable());
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
