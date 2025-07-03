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

@Component
@Slf4j
public class JwtUtil {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public Long getUserIdFromToken(String token) {
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
            
            // 从token中获取用户名
            String username = claims.getSubject();
            log.info("Token解析成功，用户名: {}", username);
            
            // 根据用户名返回对应的用户ID
            // 这里应该查询数据库获取真实的用户ID，现在使用映射
            Long userId = getUserIdByUsername(username);
            log.info("用户ID映射: {} -> {}", username, userId);
            
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
        switch (username) {
            case "admin":
                return 1L;
            case "testuser":
                return 5L;
            case "user":
                return 2L;
            default:
                log.warn("未知用户名: {}，使用默认用户ID 1", username);
                return 1L; // 默认返回用户ID 1
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