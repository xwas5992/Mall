package com.mall.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存用户信息
     */
    @Cacheable(value = "user", key = "#username")
    public Object cacheUser(String username, Object userData) {
        log.info("缓存用户信息: {}", username);
        return userData;
    }

    /**
     * 获取缓存的用户信息
     */
    @Cacheable(value = "user", key = "#username")
    public Object getUserFromCache(String username) {
        log.info("从缓存获取用户信息: {}", username);
        return null; // 如果缓存中没有，返回null
    }

    /**
     * 清除用户缓存
     */
    @CacheEvict(value = "user", key = "#username")
    public void evictUserCache(String username) {
        log.info("清除用户缓存: {}", username);
    }

    /**
     * 缓存token黑名单
     */
    public void cacheTokenBlacklist(String token, long expirationTime) {
        String key = "blacklist:" + token;
        long ttl = expirationTime - System.currentTimeMillis();
        if (ttl > 0) {
            redisTemplate.opsForValue().set(key, "blacklisted", ttl, TimeUnit.MILLISECONDS);
            log.info("将token加入黑名单: {}", token.substring(0, Math.min(20, token.length())) + "...");
        }
    }

    /**
     * 检查token是否在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        String key = "blacklist:" + token;
        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            log.info("Token在黑名单中: {}", token.substring(0, Math.min(20, token.length())) + "...");
        }
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 缓存验证码
     */
    public void cacheVerificationCode(String key, String code, long expirationSeconds) {
        redisTemplate.opsForValue().set("verification:" + key, code, expirationSeconds, TimeUnit.SECONDS);
        log.info("缓存验证码: {} -> {}", key, code);
    }

    /**
     * 获取验证码
     */
    public String getVerificationCode(String key) {
        String code = (String) redisTemplate.opsForValue().get("verification:" + key);
        log.info("获取验证码: {} -> {}", key, code);
        return code;
    }

    /**
     * 删除验证码
     */
    public void deleteVerificationCode(String key) {
        redisTemplate.delete("verification:" + key);
        log.info("删除验证码: {}", key);
    }

    /**
     * 缓存登录失败次数
     */
    public void cacheLoginFailCount(String username, int count, long expirationMinutes) {
        String key = "login_fail:" + username;
        redisTemplate.opsForValue().set(key, count, expirationMinutes, TimeUnit.MINUTES);
        log.info("缓存登录失败次数: {} -> {}", username, count);
    }

    /**
     * 获取登录失败次数
     */
    public Integer getLoginFailCount(String username) {
        String key = "login_fail:" + username;
        Object count = redisTemplate.opsForValue().get(key);
        log.info("获取登录失败次数: {} -> {}", username, count);
        return count != null ? (Integer) count : 0;
    }

    /**
     * 清除登录失败次数
     */
    public void clearLoginFailCount(String username) {
        String key = "login_fail:" + username;
        redisTemplate.delete(key);
        log.info("清除登录失败次数: {}", username);
    }

    /**
     * 设置用户会话
     */
    public void setUserSession(String sessionId, Object userData, long expirationMinutes) {
        String key = "session:" + sessionId;
        redisTemplate.opsForValue().set(key, userData, expirationMinutes, TimeUnit.MINUTES);
        log.info("设置用户会话: {}", sessionId);
    }

    /**
     * 获取用户会话
     */
    public Object getUserSession(String sessionId) {
        String key = "session:" + sessionId;
        Object session = redisTemplate.opsForValue().get(key);
        log.info("获取用户会话: {} -> {}", sessionId, session != null ? "存在" : "不存在");
        return session;
    }

    /**
     * 删除用户会话
     */
    public void deleteUserSession(String sessionId) {
        String key = "session:" + sessionId;
        redisTemplate.delete(key);
        log.info("删除用户会话: {}", sessionId);
    }
} 