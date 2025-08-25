package com.mall.userservice;

import com.mall.userservice.dto.UserUpdateRequest;
import com.mall.userservice.service.BloomFilterService;
import com.mall.userservice.service.CacheService;
import com.mall.userservice.service.CacheAvalancheProtectionService;
import com.mall.userservice.service.HighAvailabilityCacheService;
import com.mall.userservice.service.MutexLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CacheService cacheService;
    
    @Autowired
    private BloomFilterService bloomFilterService;
    
    @Autowired
    private CacheAvalancheProtectionService avalancheProtectionService;
    
    @Autowired
    private HighAvailabilityCacheService haCacheService;
    
    @Autowired
    private MutexLockService mutexLockService;

    public User register(User user) {
        // nickname默认等于phone
        user.setNickname(user.getPhone());
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setUpdatedAt(java.time.LocalDateTime.now());
        // 如果传入id，校验唯一性
        if (user.getId() != null) {
            if (userRepository.existsById(user.getId())) {
                throw new RuntimeException("用户ID已存在");
            }
        }
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    /**
     * 根据ID查找用户（带缓存穿透、雪崩和击穿防护）
     */
    public User findById(Long id) {
        log.info("查找用户: {}", id);
        
        // 1. 布隆过滤器检查
        if (!bloomFilterService.mightContain(id)) {
            log.warn("布隆过滤器显示用户不存在: {}", id);
            return null;
        }
        
        // 2. 使用互斥锁服务防止缓存击穿
        String cacheKey = "user:" + id;
        String lockKey = "lock:user:" + id;
        
        return mutexLockService.getCacheWithMutexLock(cacheKey, lockKey, () -> {
            // 3. 查询数据库
            User user = userRepository.findById(id).orElse(null);
            
            if (user != null) {
                log.info("从数据库查询到用户: {}", id);
                // 如果用户存在，添加到布隆过滤器
                bloomFilterService.addUserId(id);
                
                // 如果是热点数据，设置带随机TTL的缓存
                if (isHotUser(id)) {
                    avalancheProtectionService.setWithRandomTTL(cacheKey, user, 3600, java.util.concurrent.TimeUnit.SECONDS);
                    log.info("设置热点用户缓存: {}", id);
                }
            } else {
                log.warn("数据库中用户不存在: {}", id);
            }
            
            return user;
        });
    }
    
    /**
     * 判断是否为热点用户
     */
    private boolean isHotUser(Long userId) {
        // 这里可以根据业务逻辑判断是否为热点用户
        // 例如：用户ID为1-10的用户可能是热点用户
        return userId != null && userId >= 1 && userId <= 10;
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User updateUserInfo(Long userId, UserUpdateRequest request) {
        User user = findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新用户信息
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        
        // 更新修改时间
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);

        // 同步到auth-service
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            java.util.Map<String, Object> syncMap = new java.util.HashMap<>();
            syncMap.put("username", user.getUsername());
            syncMap.put("phone", user.getPhone());
            syncMap.put("nickname", user.getNickname());
            syncMap.put("avatar", user.getAvatar());
            restTemplate.postForEntity("http://localhost:8081/api/auth/sync-user-info", syncMap, Void.class);
        } catch (Exception e) {
            System.err.println("[WARN] 同步auth-service用户信息失败: " + e.getMessage());
        }

        return savedUser;
    }
} 