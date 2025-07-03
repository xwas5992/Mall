package com.mall.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 增强的缓存服务
 * 支持空值缓存，防止缓存穿透
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存用户信息（支持空值缓存）
     */
    public void cacheUser(Long userId, Object userData) {
        String key = "user:" + userId;
        if (userData == null) {
            // 空值缓存，设置较短的过期时间（2分钟）
            redisTemplate.opsForValue().set(key, "NULL_VALUE", 2, TimeUnit.MINUTES);
            log.info("缓存空值用户信息: {} (2分钟过期)", userId);
        } else {
            // 正常数据缓存，设置较长的过期时间（30分钟）
            redisTemplate.opsForValue().set(key, userData, 30, TimeUnit.MINUTES);
            log.info("缓存用户信息: {} (30分钟过期)", userId);
        }
    }

    /**
     * 获取缓存的用户信息
     */
    public Object getUserFromCache(Long userId) {
        String key = "user:" + userId;
        Object cached = redisTemplate.opsForValue().get(key);
        
        if (cached == null) {
            log.info("缓存未命中: {}", userId);
            return null;
        } else if ("NULL_VALUE".equals(cached)) {
            log.info("缓存命中空值: {}", userId);
            return null;
        } else {
            log.info("缓存命中: {}", userId);
            return cached;
        }
    }

    /**
     * 清除用户缓存
     */
    @CacheEvict(value = "user", key = "#userId")
    public void evictUserCache(Long userId) {
        log.info("清除用户缓存: {}", userId);
    }

    /**
     * 缓存用户资料
     */
    @Cacheable(value = "userProfile", key = "#userId")
    public Object cacheUserProfile(Long userId, Object profileData) {
        log.info("缓存用户资料: {}", userId);
        return profileData;
    }

    /**
     * 获取缓存的用户资料
     */
    @Cacheable(value = "userProfile", key = "#userId")
    public Object getUserProfileFromCache(Long userId) {
        log.info("从缓存获取用户资料: {}", userId);
        return null;
    }

    /**
     * 清除用户资料缓存
     */
    @CacheEvict(value = "userProfile", key = "#userId")
    public void evictUserProfileCache(Long userId) {
        log.info("清除用户资料缓存: {}", userId);
    }

    /**
     * 缓存用户会话
     */
    public void cacheUserSession(String sessionId, Object sessionData, long expirationMinutes) {
        String key = "session:" + sessionId;
        redisTemplate.opsForValue().set(key, sessionData, expirationMinutes, TimeUnit.MINUTES);
        log.info("缓存用户会话: {}", sessionId);
    }

    /**
     * 获取缓存的用户会话
     */
    public Object getUserSessionFromCache(String sessionId) {
        String key = "session:" + sessionId;
        Object session = redisTemplate.opsForValue().get(key);
        log.info("从缓存获取用户会话: {} -> {}", sessionId, session != null ? "存在" : "不存在");
        return session;
    }

    /**
     * 删除用户会话缓存
     */
    public void deleteUserSessionCache(String sessionId) {
        String key = "session:" + sessionId;
        redisTemplate.delete(key);
        log.info("删除用户会话缓存: {}", sessionId);
    }

    /**
     * 缓存用户偏好设置
     */
    public void cacheUserPreferences(Long userId, Object preferences, long expirationMinutes) {
        String key = "preferences:" + userId;
        redisTemplate.opsForValue().set(key, preferences, expirationMinutes, TimeUnit.MINUTES);
        log.info("缓存用户偏好设置: 用户{}", userId);
    }

    /**
     * 获取缓存的用户偏好设置
     */
    public Object getUserPreferencesFromCache(Long userId) {
        String key = "preferences:" + userId;
        Object preferences = redisTemplate.opsForValue().get(key);
        log.info("从缓存获取用户偏好设置: 用户{} -> {}", userId, preferences != null ? "存在" : "不存在");
        return preferences;
    }

    /**
     * 删除用户偏好设置缓存
     */
    public void deleteUserPreferencesCache(Long userId) {
        String key = "preferences:" + userId;
        redisTemplate.delete(key);
        log.info("删除用户偏好设置缓存: 用户{}", userId);
    }

    /**
     * 缓存用户统计信息
     */
    public void cacheUserStats(Long userId, Object stats, long expirationMinutes) {
        String key = "stats:" + userId;
        redisTemplate.opsForValue().set(key, stats, expirationMinutes, TimeUnit.MINUTES);
        log.info("缓存用户统计信息: 用户{}", userId);
    }

    /**
     * 获取缓存的用户统计信息
     */
    public Object getUserStatsFromCache(Long userId) {
        String key = "stats:" + userId;
        Object stats = redisTemplate.opsForValue().get(key);
        log.info("从缓存获取用户统计信息: 用户{} -> {}", userId, stats != null ? "存在" : "不存在");
        return stats;
    }

    /**
     * 删除用户统计信息缓存
     */
    public void deleteUserStatsCache(Long userId) {
        String key = "stats:" + userId;
        redisTemplate.delete(key);
        log.info("删除用户统计信息缓存: 用户{}", userId);
    }

    /**
     * 设置缓存过期时间
     */
    public void setExpire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
        log.info("设置缓存过期时间: {} -> {} {}", key, timeout, unit);
    }

    /**
     * 检查key是否存在
     */
    public boolean hasKey(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        log.info("检查key是否存在: {} -> {}", key, exists);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 删除缓存
     */
    public void deleteCache(String key) {
        redisTemplate.delete(key);
        log.info("删除缓存: {}", key);
    }

    /**
     * 清除所有用户相关缓存
     */
    public void clearAllUserCache() {
        // 这里可以添加清除所有用户相关缓存的逻辑
        log.info("清除所有用户相关缓存");
    }
} 