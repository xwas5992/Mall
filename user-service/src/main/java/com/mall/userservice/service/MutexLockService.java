package com.mall.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 互斥锁服务
 * 使用Redis分布式锁防止缓存击穿
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MutexLockService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    // 锁的默认过期时间（防止死锁）
    private static final long DEFAULT_LOCK_TTL = 30;
    private static final TimeUnit DEFAULT_LOCK_TIME_UNIT = TimeUnit.SECONDS;
    
    // 锁的等待时间
    private static final long LOCK_WAIT_TIME = 100;
    private static final TimeUnit LOCK_WAIT_TIME_UNIT = TimeUnit.MILLISECONDS;

    /**
     * 使用互斥锁获取缓存数据
     * 防止缓存击穿
     */
    public <T> T getCacheWithMutexLock(String cacheKey, String lockKey, Supplier<T> dataLoader) {
        // 1. 尝试从缓存获取数据
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("缓存命中，直接返回: {}", cacheKey);
            return (T) cached;
        }

        // 2. 缓存未命中，尝试获取互斥锁
        String lockValue = generateLockValue();
        boolean lockAcquired = tryAcquireLock(lockKey, lockValue);
        
        if (lockAcquired) {
            try {
                log.info("获取到互斥锁，开始重建缓存: {}", cacheKey);
                
                // 3. 双重检查，防止在获取锁期间其他线程已经更新了缓存
                cached = redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    log.info("双重检查缓存命中: {}", cacheKey);
                    return (T) cached;
                }
                
                // 4. 从数据源加载数据
                T data = dataLoader.get();
                
                // 5. 设置缓存
                if (data != null) {
                    redisTemplate.opsForValue().set(cacheKey, data, 30, TimeUnit.MINUTES);
                    log.info("缓存重建成功: {}", cacheKey);
                } else {
                    // 空值缓存，较短时间
                    redisTemplate.opsForValue().set(cacheKey, "NULL_VALUE", 2, TimeUnit.MINUTES);
                    log.info("缓存空值: {}", cacheKey);
                }
                
                return data;
                
            } finally {
                // 6. 释放锁
                releaseLock(lockKey, lockValue);
                log.info("释放互斥锁: {}", lockKey);
            }
        } else {
            // 7. 未获取到锁，等待一段时间后重试
            log.info("未获取到锁，等待重试: {}", lockKey);
            try {
                Thread.sleep(LOCK_WAIT_TIME_UNIT.toMillis(LOCK_WAIT_TIME));
                
                // 重试获取缓存
                cached = redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    log.info("重试后缓存命中: {}", cacheKey);
                    return (T) cached;
                }
                
                log.warn("重试后缓存仍未命中: {}", cacheKey);
                return null;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("等待锁时被中断: {}", lockKey, e);
                return null;
            }
        }
    }

    /**
     * 尝试获取分布式锁
     */
    private boolean tryAcquireLock(String lockKey, String lockValue) {
        try {
            // 使用SET NX EX命令原子性地设置锁
            Boolean result = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, lockValue, DEFAULT_LOCK_TTL, DEFAULT_LOCK_TIME_UNIT);
            
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("获取锁失败: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 释放分布式锁
     */
    private void releaseLock(String lockKey, String lockValue) {
        try {
            // 简单的锁释放逻辑
            Object currentValue = redisTemplate.opsForValue().get(lockKey);
            if (lockValue.equals(currentValue)) {
                redisTemplate.delete(lockKey);
                log.debug("锁释放成功: {}", lockKey);
            } else {
                log.warn("锁值不匹配，无法释放: {}", lockKey);
            }
        } catch (Exception e) {
            log.error("释放锁失败: {}", lockKey, e);
        }
    }

    /**
     * 生成锁的唯一值
     */
    private String generateLockValue() {
        return Thread.currentThread().getId() + ":" + System.currentTimeMillis();
    }

    /**
     * 检查锁是否存在
     */
    public boolean isLocked(String lockKey) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
        } catch (Exception e) {
            log.error("检查锁状态失败: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 强制释放锁（谨慎使用）
     */
    public void forceReleaseLock(String lockKey) {
        try {
            redisTemplate.delete(lockKey);
            log.warn("强制释放锁: {}", lockKey);
        } catch (Exception e) {
            log.error("强制释放锁失败: {}", lockKey, e);
        }
    }

    /**
     * 获取锁的剩余时间
     */
    public long getLockTTL(String lockKey) {
        try {
            Long ttl = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
            return ttl != null ? ttl : -1;
        } catch (Exception e) {
            log.error("获取锁TTL失败: {}", lockKey, e);
            return -1;
        }
    }
} 