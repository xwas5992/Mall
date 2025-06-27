package com.mall.productservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret.getBytes())
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();
            
            // 从token中获取用户名
            String username = claims.getSubject();
            System.out.println("Token解析成功，用户名: " + username);
            
            // 临时使用固定用户ID进行测试
            // 实际项目中应该查询数据库获取用户ID
            if ("admin".equals(username)) {
                return 1L;
            } else if ("testuser".equals(username)) {
                return 5L;
            } else {
                return 1L; // 默认返回用户ID 1
            }
        } catch (Exception e) {
            System.err.println("Token解析失败: " + e.getMessage());
            throw new RuntimeException("Token解析失败: " + e.getMessage());
        }
    }
} 