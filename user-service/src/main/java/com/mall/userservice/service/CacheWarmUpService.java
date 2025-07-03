package com.mall.userservice.service;

import com.mall.userservice.User;
import com.mall.userservice.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 缓存预热服务
 * 在系统启动时预先加载热点数据，防止缓存雪崩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheWarmUpService {

    private final UserRepository userRepository;
    private final CacheAvalancheProtectionService avalancheProtectionService;
    private final HighAvailabilityCacheService haCacheService;

    /**
     * 应用启动完成后执行缓存预热
     */
    @EventListener(ApplicationReadyEvent.class)
    public void warmUpCache() {
        log.info("开始执行缓存预热...");
        
        try {
            // 预热热点用户数据
            warmUpHotUsers();
            
            // 预热系统配置数据
            warmUpSystemConfig();
            
            log.info("缓存预热完成");
        } catch (Exception e) {
            log.error("缓存预热失败", e);
        }
    }

    /**
     * 预热用户缓存
     */
    public void warmUpUserCache() {
        warmUpHotUsers();
    }

    /**
     * 预热系统缓存
     */
    public void warmUpSystemCache() {
        warmUpSystemConfig();
    }

    /**
     * 预热热点用户数据
     */
    private void warmUpHotUsers() {
        log.info("开始预热热点用户数据...");
        
        try {
            // 获取前10个用户作为热点用户
            List<User> hotUsers = userRepository.findAll().stream()
                    .limit(10)
                    .toList();
            
            for (User user : hotUsers) {
                String cacheKey = "user:" + user.getId();
                
                // 使用高可用缓存服务设置缓存
                haCacheService.setCache(cacheKey, user, 30, java.util.concurrent.TimeUnit.MINUTES);
                
                // 设置为热点数据（带随机TTL）
                avalancheProtectionService.setWithRandomTTL(cacheKey, user, 3600, java.util.concurrent.TimeUnit.SECONDS);
                
                log.info("预热用户缓存: {}", user.getId());
            }
            
            log.info("热点用户数据预热完成，共预热 {} 个用户", hotUsers.size());
        } catch (Exception e) {
            log.error("预热热点用户数据失败", e);
        }
    }

    /**
     * 预热系统配置数据
     */
    private void warmUpSystemConfig() {
        log.info("开始预热系统配置数据...");
        
        try {
            // 预热用户统计信息
            long userCount = userRepository.count();
            haCacheService.setCache("stats:user_count", userCount, 60, java.util.concurrent.TimeUnit.MINUTES);
            
            // 预热系统配置
            haCacheService.setCache("config:system_version", "1.0.0", 120, java.util.concurrent.TimeUnit.MINUTES);
            haCacheService.setCache("config:cache_ttl", 1800, 120, java.util.concurrent.TimeUnit.MINUTES);
            
            log.info("系统配置数据预热完成");
        } catch (Exception e) {
            log.error("预热系统配置数据失败", e);
        }
    }

    /**
     * 手动触发缓存预热
     */
    public void manualWarmUp() {
        log.info("手动触发缓存预热...");
        warmUpCache();
    }

    /**
     * 预热指定用户数据
     */
    public void warmUpUser(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                String cacheKey = "user:" + userId;
                haCacheService.setCache(cacheKey, user, 30, java.util.concurrent.TimeUnit.MINUTES);
                log.info("预热指定用户缓存: {}", userId);
            } else {
                log.warn("用户不存在，无法预热: {}", userId);
            }
        } catch (Exception e) {
            log.error("预热指定用户缓存失败: {}", userId, e);
        }
    }

    /**
     * 获取预热状态
     */
    public void getWarmUpStatus() {
        try {
            // 检查热点用户缓存状态
            for (int i = 1; i <= 10; i++) {
                Object cached = haCacheService.getCache("user:" + i);
                log.info("用户 {} 缓存状态: {}", i, cached != null ? "已预热" : "未预热");
            }
            
            // 检查系统配置缓存状态
            Object userCount = haCacheService.getCache("stats:user_count");
            Object systemVersion = haCacheService.getCache("config:system_version");
            
            log.info("用户统计缓存状态: {}", userCount != null ? "已预热" : "未预热");
            log.info("系统版本缓存状态: {}", systemVersion != null ? "已预热" : "未预热");
        } catch (Exception e) {
            log.error("获取预热状态失败", e);
        }
    }
} 