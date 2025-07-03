package com.mall.userservice.service;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.mall.userservice.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 布隆过滤器服务
 * 用于快速判断用户ID是否存在，防止缓存穿透
 */
@Slf4j
@Service
public class BloomFilterService {

    @Autowired
    private UserRepository userRepository;

    // 布隆过滤器，预期元素数量为10000，误判率为0.01
    private BloomFilter<String> userBloomFilter;

    @PostConstruct
    public void initBloomFilter() {
        log.info("初始化布隆过滤器...");
        
        // 创建布隆过滤器，预期元素数量10000，误判率0.01
        userBloomFilter = BloomFilter.create(
            Funnels.stringFunnel(StandardCharsets.UTF_8),
            10000,
            0.01
        );
        
        // 从数据库加载所有用户ID到布隆过滤器
        loadUserIdsToBloomFilter();
        
        log.info("布隆过滤器初始化完成");
    }

    /**
     * 加载所有用户ID到布隆过滤器
     */
    private void loadUserIdsToBloomFilter() {
        try {
            List<Long> userIds = userRepository.findAllUserIds();
            for (Long userId : userIds) {
                userBloomFilter.put(userId.toString());
            }
            log.info("已加载 {} 个用户ID到布隆过滤器", userIds.size());
        } catch (Exception e) {
            log.error("加载用户ID到布隆过滤器失败", e);
        }
    }

    /**
     * 检查用户ID是否可能存在
     * @param userId 用户ID
     * @return true表示可能存在，false表示一定不存在
     */
    public boolean mightContain(String userId) {
        return userBloomFilter.mightContain(userId);
    }

    /**
     * 检查用户ID是否可能存在
     * @param userId 用户ID
     * @return true表示可能存在，false表示一定不存在
     */
    public boolean mightContain(Long userId) {
        return userBloomFilter.mightContain(userId.toString());
    }

    /**
     * 添加用户ID到布隆过滤器
     * @param userId 用户ID
     */
    public void addUserId(String userId) {
        userBloomFilter.put(userId);
    }

    /**
     * 添加用户ID到布隆过滤器
     * @param userId 用户ID
     */
    public void addUserId(Long userId) {
        userBloomFilter.put(userId.toString());
    }

    /**
     * 重新加载布隆过滤器
     */
    public void reloadBloomFilter() {
        log.info("重新加载布隆过滤器...");
        initBloomFilter();
    }
} 