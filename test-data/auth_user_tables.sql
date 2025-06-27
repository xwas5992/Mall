-- mall_auth 数据库表结构
-- 基于 auth-service 的 User 实体类

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mall_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE mall_auth;

-- 创建认证用户表
CREATE TABLE IF NOT EXISTS auth_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    email VARCHAR(64) NOT NULL COMMENT '邮箱',
    full_name VARCHAR(64) DEFAULT NULL COMMENT '姓名',
    role VARCHAR(16) NOT NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证用户表';

-- 说明：
-- 1. 表名使用 auth_user（与User实体类的@Table注解一致）
-- 2. 字段与User实体类的属性完全对应
-- 3. 用户名和邮箱设置为唯一索引
-- 4. 角色默认为USER，支持USER和ADMIN
-- 5. enabled字段默认为1（启用状态）
-- 6. 使用utf8mb4字符集支持emoji等特殊字符 