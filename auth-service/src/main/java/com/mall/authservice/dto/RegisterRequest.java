package com.mall.authservice.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 11, max = 11)
    private String phone; // 手机号

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
} 