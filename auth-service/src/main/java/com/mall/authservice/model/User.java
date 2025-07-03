package com.mall.authservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "auth_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String phone;

    @NotBlank
    private String password;

    private String fullName;

    private String nickname;

    private String avatar;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(columnDefinition = "TINYINT(1)")
    private Integer enabled = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public boolean isEnabled() {
        return enabled != null && enabled == 1;
    }

    public enum UserRole {
        USER,
        ADMIN
    }
} 