package com.mall.userservice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    public ResponseEntity<User> register(
            @Parameter(description = "用户信息") @RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录验证")
    public ResponseEntity<User> login(
            @Parameter(description = "用户名") @RequestParam String username, 
            @Parameter(description = "密码") @RequestParam String password) {
        User user = userService.login(username, password);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户列表", description = "获取所有用户列表")
    public ResponseEntity<List<User>> list() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "服务健康状态检查")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "user-service");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }
} 