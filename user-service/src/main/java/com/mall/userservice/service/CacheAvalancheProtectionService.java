package com.mall.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存雪崩防护服务
 * 通过随机TTL、多级缓存、高可用集群等方式防止缓存雪崩
 */
@Slf4j
@Service
public class CacheAvalancheProtectionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private HighAvailabilityCacheService highAvailabilityCacheService;

    /**
     * 设置带随机TTL的缓存，防止同时过期
     */
    public void setWithRandomTTL(String key, Object value, long baseTTL, TimeUnit timeUnit) {
        try {
            // 基础TTL + 随机偏移量(±10%)
            long randomOffset = (long) (baseTTL * 0.1 * (Math.random() - 0.5));
            long finalTTL = baseTTL + randomOffset;
            
            redisTemplate.opsForValue().set(key, value, finalTTL, timeUnit);
            log.info("设置缓存成功 - key: {}, TTL: {} {}", key, finalTTL, timeUnit);
        } catch (Exception e) {
            log.error("设置缓存失败 - key: {}", key, e);
            // 降级到高可用缓存
            highAvailabilityCacheService.setCache(key, value, baseTTL, timeUnit);
        }
    }

    /**
     * 获取缓存，支持多级缓存降级
     */
    public Object getWithFallback(String key) {
        try {
            // 第一级：主缓存
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                log.debug("从主缓存获取成功 - key: {}", key);
                return value;
            }

            // 第二级：高可用缓存
            value = highAvailabilityCacheService.getCache(key);
            if (value != null) {
                log.info("从高可用缓存获取成功 - key: {}", key);
                return value;
            }

            log.warn("缓存未命中 - key: {}", key);
            return null;
        } catch (Exception e) {
            log.error("获取缓存异常 - key: {}", key, e);
            // 降级到高可用缓存
            return highAvailabilityCacheService.getCache(key);
        }
    }

    /**
     * 检查缓存健康状态
     */
    public boolean isCacheHealthy() {
        try {
            String testKey = "health_check_" + System.currentTimeMillis();
            redisTemplate.opsForValue().set(testKey, "test", 10, TimeUnit.SECONDS);
            Object result = redisTemplate.opsForValue().get(testKey);
            redisTemplate.delete(testKey);
            return "test".equals(result);
        } catch (Exception e) {
            log.error("缓存健康检查失败", e);
            return false;
        }
    }

    /**
     * 获取缓存统计信息
     */
    public String getCacheStats() {
        try {
            // 这里可以添加更详细的统计信息
            return "Cache Status: " + (isCacheHealthy() ? "Healthy" : "Unhealthy");
        } catch (Exception e) {
            log.error("获取缓存统计信息失败", e);
            return "Cache Status: Error";
        }
    }
} 