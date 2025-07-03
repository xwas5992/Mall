package com.mall.authservice.dto;

import com.mall.authservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private UserInfo user;

    @Data
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String fullName;
        private String phone;
        private User.UserRole role;

        public static UserInfo fromUser(User user) {
            return new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getPhone(),
                user.getRole()
            );
        }
    }
} 