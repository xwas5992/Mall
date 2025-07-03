package com.mall.authservice.service;

import com.mall.authservice.dto.AuthResponse;
import com.mall.authservice.dto.LoginRequest;
import com.mall.authservice.dto.RegisterRequest;
import com.mall.authservice.model.User;
import com.mall.authservice.repository.UserRepository;
import com.mall.authservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    @Value("${user.service.base-url:http://localhost:8085/api/user/register}")
    private String userServiceRegisterUrl;

    /**
     * 生成唯一6位数字用户名
     */
    private String generateUniqueUsername() {
        Random random = new Random();
        String username;
        do {
            int num = 100000 + random.nextInt(900000);
            username = String.valueOf(num);
        } while (userRepository.existsByUsername(username));
        return username;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String phone = request.getPhone();
        if (userRepository.existsByPhone(phone)) {
            throw new RuntimeException("手机号已被注册");
        }
        // 生成唯一6位数字用户名
        String username = generateUniqueUsername();

        // 写入auth_user表
        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(phone);
        userRepository.save(user);

        // 写入users表（user-service）
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("phone", phone);
        userMap.put("password", user.getPassword());
        userMap.put("nickname", phone);
        userMap.put("role", user.getRole().name());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userMap, headers);
        
        try {
            restTemplate.postForEntity(userServiceRegisterUrl, entity, String.class);
        } catch (Exception e) {
            throw new RuntimeException("注册users表失败: " + e.getMessage());
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);
        return new AuthResponse(token, "Bearer", AuthResponse.UserInfo.fromUser(user));
    }

    public AuthResponse login(LoginRequest request) {
        // 用phone查找用户
        User user = userRepository.findByPhone(request.getPhone())
            .orElseThrow(() -> new RuntimeException("手机号未注册"));

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);
        return new AuthResponse(token, "Bearer", AuthResponse.UserInfo.fromUser(user));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User updateUserStatus(Long id, boolean enabled) {
        User user = getUserById(id);
        user.setEnabled(enabled ? 1 : 0);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
} 