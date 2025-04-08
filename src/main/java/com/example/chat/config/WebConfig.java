package com.example.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // .allowedOrigins("http://192.168.58.42:3002/") // Replace with your frontend URL
                        // .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        // .allowCredentials(true)
                        
                        .allowedOrigins("*") // ✅ Allow all origins
                        .allowedMethods("*") // ✅ Allow all HTTP methods
                        .allowedHeaders("*")
                        .allowCredentials(false) // ❗ Set to true only if you're allowing credentials with specific origins
                        .maxAge(3600);
            }
        };
    }
}
