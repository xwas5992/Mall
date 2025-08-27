package com.mall.productservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.user-service.base-url:http://localhost:8085}")
    private String userServiceBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    public Integer getUserIdFromToken(String token) {
        try {
            // 移除Bearer前缀
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            
            log.info("开始解析token: {}", actualToken.substring(0, Math.min(20, actualToken.length())) + "...");
            
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret.getBytes())
                    .parseClaimsJws(actualToken)
                    .getBody();
            
            // 1) 优先从自定义声明读取 userId/id
            Integer userId = null;
            Object claimUserId = claims.get("userId");
            if (claimUserId instanceof Number) {
                long uid = ((Number) claimUserId).longValue();
                if (uid > Integer.MAX_VALUE) {
                    throw new RuntimeException("用户ID超出整型范围: " + uid);
                }
                userId = (int) uid;
            } else if (claimUserId instanceof String && ((String) claimUserId).matches("\\d+")) {
                long uid = Long.parseLong((String) claimUserId);
                if (uid > Integer.MAX_VALUE) {
                    throw new RuntimeException("用户ID超出整型范围: " + uid);
                }
                userId = (int) uid;
            }

            if (userId == null) {
                Object claimId = claims.get("id");
                if (claimId instanceof Number) {
                    long uid = ((Number) claimId).longValue();
                    if (uid > Integer.MAX_VALUE) {
                        throw new RuntimeException("用户ID超出整型范围: " + uid);
                    }
                    userId = (int) uid;
                } else if (claimId instanceof String && ((String) claimId).matches("\\d+")) {
                    long uid = Long.parseLong((String) claimId);
                    if (uid > Integer.MAX_VALUE) {
                        throw new RuntimeException("用户ID超出整型范围: " + uid);
                    }
                    userId = (int) uid;
                }
            }

            // 2) 若没有声明，再看 subject：
            String subject = claims.getSubject();
            if (userId == null && subject != null && subject.matches("\\d+")) {
                long uid = Long.parseLong(subject);
                if (uid > Integer.MAX_VALUE) {
                    throw new RuntimeException("用户ID超出整型范围: " + uid);
                }
                userId = (int) uid;
            }

            // 3) 若 subject 不是数字，则当作用户名查询 user-service 获取 id
            if (userId == null) {
                if (subject == null || subject.isBlank()) {
                    throw new RuntimeException("Token中缺少用户标识");
                }
                String encoded = URLEncoder.encode(subject, StandardCharsets.UTF_8);
                String url = userServiceBaseUrl + "/api/user/profile/" + encoded;
                try {
                    ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
                    if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                        throw new RuntimeException("无法根据用户名获取用户信息: " + subject);
                    }
                    Object idField = resp.getBody().get("id");
                    if (idField instanceof Number) {
                        long uid = ((Number) idField).longValue();
                        if (uid > Integer.MAX_VALUE) {
                            throw new RuntimeException("用户ID超出整型范围: " + uid);
                        }
                        userId = (int) uid;
                    } else if (idField instanceof String && ((String) idField).matches("\\d+")) {
                        long uid = Long.parseLong((String) idField);
                        if (uid > Integer.MAX_VALUE) {
                            throw new RuntimeException("用户ID超出整型范围: " + uid);
                        }
                        userId = (int) uid;
                    } else {
                        throw new RuntimeException("用户服务返回的ID无效");
                    }
                } catch (RestClientException e) {
                    log.error("调用用户服务失败: {}", e.getMessage());
                    throw new RuntimeException("认证失败：无法解析用户ID");
                }
            }

            log.info("Token解析成功，用户ID: {} (subject={})", userId, subject);
            // 验证用户ID范围（可选，根据实际业务需求调整）
            if (userId <= 0) {
                throw new RuntimeException("无效的用户ID: " + userId);
            }
            
            return userId;
            
        } catch (ExpiredJwtException e) {
            log.error("Token已过期: {}", e.getMessage());
            throw new RuntimeException("Token已过期，请重新登录");
        } catch (MalformedJwtException e) {
            log.error("Token格式错误: {}", e.getMessage());
            throw new RuntimeException("Token格式错误");
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT格式: {}", e.getMessage());
            throw new RuntimeException("不支持的JWT格式");
        } catch (SignatureException e) {
            log.error("Token签名验证失败: {}", e.getMessage());
            throw new RuntimeException("Token签名验证失败");
        } catch (NumberFormatException e) {
            log.error("用户ID格式转换失败: {}", e.getMessage());
            throw new RuntimeException("用户ID格式无效");
        } catch (IllegalArgumentException e) {
            log.error("Token参数错误: {}", e.getMessage());
            throw new RuntimeException("Token参数错误");
        } catch (Exception e) {
            log.error("Token解析失败: {}", e.getMessage());
            throw new RuntimeException("Token解析失败: " + e.getMessage());
        }
    }
    
    private Long getUserIdByUsername(String username) {
        // 这里应该查询数据库获取真实的用户ID
        // 现在使用硬编码映射进行测试
        switch (username.toLowerCase()) {
            case "admin":
                return 1L;
            case "testuser":
            case "test_user":
                return 5L;
            case "user":
            case "user1":
                return 2L;
            case "user2":
                return 3L;
            case "user3":
                return 4L;
            default:
                log.error("未知用户名: {}，无法获取用户ID", username);
                throw new RuntimeException("用户名不存在或未授权: " + username);
        }
    }
    
    public boolean validateToken(String token) {
        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            
            Jwts.parser()
                    .setSigningKey(jwtSecret.getBytes())
                    .parseClaimsJws(actualToken);
            
            return true;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
} 