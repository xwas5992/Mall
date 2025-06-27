package com.mall.authservice.controller;

import com.mall.authservice.dto.AuthResponse;
import com.mall.authservice.dto.LoginRequest;
import com.mall.authservice.dto.RegisterRequest;
import com.mall.authservice.model.User;
import com.mall.authservice.service.AuthService;
import com.mall.authservice.service.VerificationCodeService;
import com.mall.authservice.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证和授权相关接口")
public class AuthController {

    private final AuthService authService;
    private final VerificationCodeService verificationCodeService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/send-code")
    @Operation(summary = "发送验证码", description = "向指定邮箱发送验证码")
    public ResponseEntity<Map<String, Object>> sendCode(
            @Parameter(description = "邮箱地址") @RequestParam String email) {
        String code = verificationCodeService.generateCode(email);
        // TODO: 实际项目应通过邮件/短信发送，这里直接返回code方便前端测试
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("code", code);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    public ResponseEntity<AuthResponse> register(
            @Parameter(description = "注册信息") @Valid @RequestBody RegisterRequest request, 
            @Parameter(description = "验证码") @RequestParam String code) {
        // 校验验证码
        if (!verificationCodeService.validateCode(request.getEmail(), code)) {
            throw new RuntimeException("验证码错误或已过期");
        }
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录获取Token")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "登录信息") @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify")
    @Operation(summary = "验证Token", description = "验证JWT Token的有效性")
    public ResponseEntity<Map<String, Object>> verifyToken(
            @Parameter(description = "Authorization Header") @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            if (jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                User user = authService.findByUsername(username);
                
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", username);
                response.put("role", user.getRole().name());
                response.put("email", user.getEmail());
                response.put("fullName", user.getFullName());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", false);
                response.put("message", "Token无效");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Token验证失败");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取用户列表", description = "管理员获取所有用户列表（分页）")
    public ResponseEntity<Page<User>> getAllUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = authService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/count")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取用户总数", description = "获取系统用户总数")
    public ResponseEntity<Long> getUserCount() {
        long count = authService.getUserCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详细信息")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        User user = authService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新用户状态", description = "启用或禁用用户账户")
    public ResponseEntity<User> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "是否启用") @RequestParam boolean enabled) {
        User user = authService.updateUserStatus(id, enabled);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除用户", description = "删除指定用户")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "服务健康状态检查")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "auth-service");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }
} 