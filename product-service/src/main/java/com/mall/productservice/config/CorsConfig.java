package com.mall.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许所有来源，包括null origin（file://协议）
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // 或者明确指定允许的来源
        // configuration.setAllowedOrigins(Arrays.asList(
        //     "http://localhost:63342",
        //     "http://127.0.0.1:63342",
        //     "http://localhost:5500",
        //     "http://127.0.0.1:5500",
        //     "http://localhost:8080",
        //     "http://127.0.0.1:8080",
        //     "http://localhost:3000",
        //     "http://127.0.0.1:3000",
        //     "http://localhost:5173",
        //     "http://127.0.0.1:5173",
        //     "http://localhost",
        //     "http://127.0.0.1",
        //     "file://",
        //     "null"
        // ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // 设置预检请求的缓存时间
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}