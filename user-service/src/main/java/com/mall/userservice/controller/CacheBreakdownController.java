package com.mall.userservice.controller;

import com.mall.userservice.service.MutexLockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存击穿防护控制器
 * 提供锁状态监控和管理接口
 */
@Slf4j
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
@Tag(name = "缓存击穿防护", description = "缓存击穿防护相关接口")
public class CacheBreakdownController {

    private final MutexLockService mutexLockService;

    @GetMapping("/lock/status/{lockKey}")
    @Operation(summary = "检查锁状态", description = "检查指定锁的状态")
    public ResponseEntity<Map<String, Object>> checkLockStatus(
            @Parameter(description = "锁的键名") @PathVariable String lockKey) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean isLocked = mutexLockService.isLocked(lockKey);
            long ttl = mutexLockService.getLockTTL(lockKey);
            
            response.put("lockKey", lockKey);
            response.put("isLocked", isLocked);
            response.put("ttl", ttl);
            response.put("timestamp", System.currentTimeMillis());
            
            log.info("检查锁状态: {} -> {}", lockKey, isLocked ? "已锁定" : "未锁定");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("检查锁状态失败: {}", lockKey, e);
            response.put("error", true);
            response.put("message", "检查锁状态失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/lock/{lockKey}")
    @Operation(summary = "强制释放锁", description = "强制释放指定的锁（谨慎使用）")
    public ResponseEntity<Map<String, Object>> forceReleaseLock(
            @Parameter(description = "锁的键名") @PathVariable String lockKey) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            mutexLockService.forceReleaseLock(lockKey);
            
            response.put("lockKey", lockKey);
            response.put("message", "锁已强制释放");
            response.put("timestamp", System.currentTimeMillis());
            
            log.warn("强制释放锁: {}", lockKey);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("强制释放锁失败: {}", lockKey, e);
            response.put("error", true);
            response.put("message", "强制释放锁失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/lock/stats")
    @Operation(summary = "获取锁统计信息", description = "获取所有锁的统计信息")
    public ResponseEntity<Map<String, Object>> getLockStats() {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查常见的锁状态
            String[] commonLocks = {
                "lock:user:1", "lock:user:2", "lock:user:3",
                "lock:user:9999", "lock:user:10000"
            };
            
            Map<String, Object> lockStats = new HashMap<>();
            for (String lockKey : commonLocks) {
                Map<String, Object> lockInfo = new HashMap<>();
                lockInfo.put("isLocked", mutexLockService.isLocked(lockKey));
                lockInfo.put("ttl", mutexLockService.getLockTTL(lockKey));
                lockStats.put(lockKey, lockInfo);
            }
            
            response.put("lockStats", lockStats);
            response.put("timestamp", System.currentTimeMillis());
            
            log.info("获取锁统计信息完成");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取锁统计信息失败", e);
            response.put("error", true);
            response.put("message", "获取锁统计信息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/test/breakdown")
    @Operation(summary = "测试缓存击穿防护", description = "模拟缓存击穿场景进行测试")
    public ResponseEntity<Map<String, Object>> testCacheBreakdown(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String lockKey = "lock:user:" + userId;
            
            // 检查锁状态
            boolean isLocked = mutexLockService.isLocked(lockKey);
            long ttl = mutexLockService.getLockTTL(lockKey);
            
            response.put("userId", userId);
            response.put("lockKey", lockKey);
            response.put("isLocked", isLocked);
            response.put("ttl", ttl);
            response.put("message", "缓存击穿防护测试完成");
            response.put("timestamp", System.currentTimeMillis());
            
            log.info("缓存击穿防护测试: 用户{} -> 锁状态: {}", userId, isLocked ? "已锁定" : "未锁定");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("缓存击穿防护测试失败: 用户{}", userId, e);
            response.put("error", true);
            response.put("message", "缓存击穿防护测试失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/health")
    @Operation(summary = "缓存击穿防护健康检查", description = "检查缓存击穿防护服务状态")
    public ResponseEntity<Map<String, Object>> health() {
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "cache-breakdown-protection");
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(health);
    }
} 