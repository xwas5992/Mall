package com.mall.userservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    
    /**
     * 获取所有用户ID列表
     * 用于布隆过滤器初始化
     */
    @Query("SELECT u.id FROM User u")
    List<Long> findAllUserIds();
} 