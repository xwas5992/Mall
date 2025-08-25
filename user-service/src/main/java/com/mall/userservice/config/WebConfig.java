package com.mall.userservice.config;

import com.mall.userservice.interceptor.ParameterValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 注册拦截器和配置
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ParameterValidationInterceptor parameterValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册参数校验拦截器
        registry.addInterceptor(parameterValidationInterceptor)
                .addPathPatterns("/api/user/**")
                .excludePathPatterns("/api/user/health");
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOriginPatterns("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true)
//                .maxAge(3600);
//    }
} 