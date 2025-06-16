package com.mall.userservice;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("用户服务 API")
                        .description("用户注册、登录、查询接口")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Edge Browser")
                                .url("https://www.microsoft.com/edge")));
    }
} 