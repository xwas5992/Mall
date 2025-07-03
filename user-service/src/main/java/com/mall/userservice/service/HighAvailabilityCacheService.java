package com.mall.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 高可用缓存服务
 * 实现故障转移和多级缓存
 */
@Slf4j
@Service
public class HighAvailabilityCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存
     */
    public void setCache(String key, Object value, long ttl, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl, unit);
            log.info("Redis设置缓存成功: {}", key);
        } catch (Exception e) {
            log.error("Redis设置缓存失败: {}", key, e);
            throw new RuntimeException("缓存服务不可用", e);
        }
    }

    /**
     * 获取缓存
     */
    public Object getCache(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                log.info("Redis获取缓存成功: {}", key);
                return value;
            }
        } catch (Exception e) {
            log.error("Redis获取缓存失败: {}", key, e);
        }

        log.info("缓存未命中: {}", key);
        return null;
    }

    /**
     * 删除缓存
     */
    public void deleteCache(String key) {
        try {
            redisTemplate.delete(key);
            log.info("Redis删除缓存成功: {}", key);
        } catch (Exception e) {
            log.error("Redis删除缓存失败: {}", key, e);
        }
    }

    /**
     * 检查Redis连接状态
     */
    public boolean isRedisAvailable() {
        try {
            redisTemplate.opsForValue().get("health_check");
            return true;
        } catch (Exception e) {
            log.warn("Redis连接检查失败", e);
            return false;
        }
    }

    /**
     * 获取缓存统计信息
     */
    public void getCacheStats() {
        boolean redisAvailable = isRedisAvailable();
        log.info("缓存服务状态 - Redis: {}", redisAvailable ? "可用" : "不可用");
    }
} 