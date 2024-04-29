package com.lumenprototype.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {
    // 상수로 허용할 원본 URL 및 허용할 메서드 정의
    private static final String[] ALLOWED_ORIGINS = {
            "http://192.168.0.16:3000/",
            "http://192.168.0.48:3000/",
            "http://localhost:3000/",

            // 다른 허용할 원본 URL 추가 가능
    };
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(ALLOWED_ORIGINS)
                .allowedMethods("GET", "POST", "PATCH", "DELETE")
                .allowedHeaders("*");
    }
}