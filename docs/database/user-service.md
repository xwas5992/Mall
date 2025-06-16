# 用户服务数据库设计

## 1. 数据库概述

### 1.1 数据库信息
- 数据库名：mall_user
- 字符集：utf8mb4
- 排序规则：utf8mb4_general_ci
- 存储引擎：InnoDB

### 1.2 表清单
1. 用户表 (user)
2. 用户地址表 (user_address)
3. 会员表 (user_member)
4. 会员等级表 (user_member_level)
5. 积分记录表 (user_points_record)
6. 用户认证表 (user_auth)
7. 用户登录日志表 (user_login_log)

## 2. 表结构设计

### 2.1 用户表 (user)
```sql
CREATE TABLE `user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `password` varchar(128) NOT NULL COMMENT '密码',
    `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
    `phone` varchar(32) DEFAULT NULL COMMENT '手机号',
    `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
    `gender` tinyint DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
    `birthday` date DEFAULT NULL COMMENT '生日',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` varchar(64) DEFAULT NULL COMMENT '最后登录IP',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 2.2 用户地址表 (user_address)
```sql
CREATE TABLE `user_address` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `receiver` varchar(64) NOT NULL COMMENT '收货人',
    `phone` varchar(32) NOT NULL COMMENT '手机号',
    `province` varchar(64) NOT NULL COMMENT '省份',
    `city` varchar(64) NOT NULL COMMENT '城市',
    `district` varchar(64) NOT NULL COMMENT '区县',
    `detail` varchar(255) NOT NULL COMMENT '详细地址',
    `zip_code` varchar(32) DEFAULT NULL COMMENT '邮编',
    `is_default` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认：0-否，1-是',
    `tag` varchar(32) DEFAULT NULL COMMENT '地址标签',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_default` (`is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';
```

### 2.3 会员表 (user_member)
```sql
CREATE TABLE `user_member` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会员ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `level_id` bigint NOT NULL COMMENT '等级ID',
    `level_name` varchar(64) NOT NULL COMMENT '等级名称',
    `points` int NOT NULL DEFAULT '0' COMMENT '积分',
    `total_points` int NOT NULL DEFAULT '0' COMMENT '累计积分',
    `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_level_id` (`level_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';
```

### 2.4 会员等级表 (user_member_level)
```sql
CREATE TABLE `user_member_level` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '等级ID',
    `name` varchar(64) NOT NULL COMMENT '等级名称',
    `level` int NOT NULL COMMENT '等级值',
    `icon` varchar(255) DEFAULT NULL COMMENT '等级图标',
    `points` int NOT NULL DEFAULT '0' COMMENT '所需积分',
    `discount` decimal(3,2) NOT NULL DEFAULT '1.00' COMMENT '折扣率',
    `privileges` json DEFAULT NULL COMMENT '特权列表',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_level` (`level`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级表';
```

### 2.5 积分记录表 (user_points_record)
```sql
CREATE TABLE `user_points_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `points` int NOT NULL COMMENT '积分变动值',
    `type` tinyint NOT NULL COMMENT '类型：1-购物，2-评价，3-签到，4-活动，5-系统',
    `source` varchar(64) NOT NULL COMMENT '来源',
    `description` varchar(255) DEFAULT NULL COMMENT '描述',
    `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';
```

### 2.6 用户认证表 (user_auth)
```sql
CREATE TABLE `user_auth` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '认证ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `auth_type` tinyint NOT NULL COMMENT '认证类型：1-手机，2-邮箱，3-微信，4-支付宝',
    `auth_account` varchar(64) NOT NULL COMMENT '认证账号',
    `auth_name` varchar(64) DEFAULT NULL COMMENT '认证名称',
    `auth_avatar` varchar(255) DEFAULT NULL COMMENT '认证头像',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_auth_account` (`auth_type`, `auth_account`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户认证表';
```

### 2.7 用户登录日志表 (user_login_log)
```sql
CREATE TABLE `user_login_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `login_type` tinyint NOT NULL COMMENT '登录类型：1-密码，2-验证码，3-第三方',
    `login_ip` varchar(64) NOT NULL COMMENT '登录IP',
    `login_location` varchar(255) DEFAULT NULL COMMENT '登录地点',
    `user_agent` varchar(255) DEFAULT NULL COMMENT '用户代理',
    `device` varchar(64) DEFAULT NULL COMMENT '设备信息',
    `status` tinyint NOT NULL COMMENT '状态：0-失败，1-成功',
    `message` varchar(255) DEFAULT NULL COMMENT '提示信息',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_login_type` (`login_type`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户登录日志表';
```

## 3. 索引设计

### 3.1 用户表索引
1. 主键索引：id
2. 唯一索引：username, phone, email
3. 普通索引：status, created_at

### 3.2 用户地址表索引
1. 主键索引：id
2. 普通索引：user_id, is_default

### 3.3 会员表索引
1. 主键索引：id
2. 唯一索引：user_id
3. 普通索引：level_id, status

### 3.4 会员等级表索引
1. 主键索引：id
2. 唯一索引：level
3. 普通索引：status

### 3.5 积分记录表索引
1. 主键索引：id
2. 普通索引：user_id, type, created_at

### 3.6 用户认证表索引
1. 主键索引：id
2. 唯一索引：(auth_type, auth_account)
3. 普通索引：user_id, status

### 3.7 用户登录日志表索引
1. 主键索引：id
2. 普通索引：user_id, login_type, created_at

## 4. 字段说明

### 4.1 通用字段
- id：主键，bigint，自增
- created_at：创建时间，datetime
- updated_at：更新时间，datetime
- deleted_at：删除时间，datetime
- status：状态，tinyint

### 4.2 用户表字段
- username：用户名，varchar(64)
- password：密码，varchar(128)
- nickname：昵称，varchar(64)
- avatar：头像，varchar(255)
- phone：手机号，varchar(32)
- email：邮箱，varchar(64)
- gender：性别，tinyint
- birthday：生日，date
- last_login_time：最后登录时间，datetime
- last_login_ip：最后登录IP，varchar(64)

### 4.3 用户地址表字段
- user_id：用户ID，bigint
- receiver：收货人，varchar(64)
- phone：手机号，varchar(32)
- province：省份，varchar(64)
- city：城市，varchar(64)
- district：区县，varchar(64)
- detail：详细地址，varchar(255)
- zip_code：邮编，varchar(32)
- is_default：是否默认，tinyint
- tag：地址标签，varchar(32)

### 4.4 会员表字段
- user_id：用户ID，bigint
- level_id：等级ID，bigint
- level_name：等级名称，varchar(64)
- points：积分，int
- total_points：累计积分，int
- expire_time：过期时间，datetime

### 4.5 会员等级表字段
- name：等级名称，varchar(64)
- level：等级值，int
- icon：等级图标，varchar(255)
- points：所需积分，int
- discount：折扣率，decimal(3,2)
- privileges：特权列表，json

### 4.6 积分记录表字段
- user_id：用户ID，bigint
- points：积分变动值，int
- type：类型，tinyint
- source：来源，varchar(64)
- description：描述，varchar(255)
- expire_time：过期时间，datetime

### 4.7 用户认证表字段
- user_id：用户ID，bigint
- auth_type：认证类型，tinyint
- auth_account：认证账号，varchar(64)
- auth_name：认证名称，varchar(64)
- auth_avatar：认证头像，varchar(255)

### 4.8 用户登录日志表字段
- user_id：用户ID，bigint
- login_type：登录类型，tinyint
- login_ip：登录IP，varchar(64)
- login_location：登录地点，varchar(255)
- user_agent：用户代理，varchar(255)
- device：设备信息，varchar(64)
- status：状态，tinyint
- message：提示信息，varchar(255)

## 5. 更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-xx | v1.0.0 | 初始版本 | - | 