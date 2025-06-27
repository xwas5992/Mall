-- mall_user 数据库表结构
-- 基于 user-service 的 User 实体类

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mall_user CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE mall_user;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 说明：
-- 1. 表名使用 users（与User实体类的@Table注解一致）
-- 2. 字段与User实体类的属性对应
-- 3. 添加了created_at和updated_at时间戳字段
-- 4. 用户名设置为唯一索引
-- 5. 使用utf8mb4字符集支持emoji等特殊字符 