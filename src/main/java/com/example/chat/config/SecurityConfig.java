package com.example.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/ws/**", "/topic/**", "/api/chat/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf().disable(); // Disable CSRF only if needed for testing or sockets

        return http.build();
    }
}
