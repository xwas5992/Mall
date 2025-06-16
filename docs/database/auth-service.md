# 认证服务数据库设计

## 1. 数据库概述

### 1.1 数据库信息
- 数据库名：mall_auth
- 字符集：utf8mb4
- 排序规则：utf8mb4_general_ci
- 存储引擎：InnoDB

### 1.2 表清单
1. 认证用户表 (auth_user)
2. 认证客户端表 (auth_client)
3. 认证令牌表 (auth_token)
4. 刷新令牌表 (auth_refresh_token)
5. 授权码表 (auth_code)
6. 认证日志表 (auth_log)
7. 认证黑名单表 (auth_blacklist)
8. 认证白名单表 (auth_whitelist)
9. 认证设备表 (auth_device)
10. 认证IP表 (auth_ip)

## 2. 表结构设计

### 2.1 认证用户表 (auth_user)
```sql
CREATE TABLE `auth_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `password` varchar(128) NOT NULL COMMENT '密码',
    `salt` varchar(32) NOT NULL COMMENT '盐值',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `account_non_expired` tinyint NOT NULL DEFAULT '1' COMMENT '账号是否过期：0-过期，1-未过期',
    `account_non_locked` tinyint NOT NULL DEFAULT '1' COMMENT '账号是否锁定：0-锁定，1-未锁定',
    `credentials_non_expired` tinyint NOT NULL DEFAULT '1' COMMENT '凭证是否过期：0-过期，1-未过期',
    `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用：0-禁用，1-启用',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` varchar(64) DEFAULT NULL COMMENT '最后登录IP',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证用户表';
```

### 2.2 认证客户端表 (auth_client)
```sql
CREATE TABLE `auth_client` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '客户端ID',
    `client_id` varchar(64) NOT NULL COMMENT '客户端标识',
    `client_secret` varchar(128) NOT NULL COMMENT '客户端密钥',
    `client_name` varchar(64) NOT NULL COMMENT '客户端名称',
    `client_type` tinyint NOT NULL COMMENT '客户端类型：1-Web应用，2-移动应用，3-小程序',
    `redirect_uri` varchar(255) DEFAULT NULL COMMENT '重定向URI',
    `scope` varchar(255) DEFAULT NULL COMMENT '授权范围',
    `authorized_grant_types` varchar(255) NOT NULL COMMENT '授权类型',
    `access_token_validity` int NOT NULL DEFAULT '3600' COMMENT '访问令牌有效期(秒)',
    `refresh_token_validity` int NOT NULL DEFAULT '86400' COMMENT '刷新令牌有效期(秒)',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_client_id` (`client_id`),
    KEY `idx_client_type` (`client_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证客户端表';
```

### 2.3 认证令牌表 (auth_token)
```sql
CREATE TABLE `auth_token` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '令牌ID',
    `token_id` varchar(64) NOT NULL COMMENT '令牌标识',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `client_id` varchar(64) NOT NULL COMMENT '客户端ID',
    `token_type` varchar(32) NOT NULL COMMENT '令牌类型',
    `token_value` varchar(255) NOT NULL COMMENT '令牌值',
    `scope` varchar(255) DEFAULT NULL COMMENT '授权范围',
    `expires_at` datetime NOT NULL COMMENT '过期时间',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-无效，1-有效',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_token_id` (`token_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_client_id` (`client_id`),
    KEY `idx_token_type` (`token_type`),
    KEY `idx_status` (`status`),
    KEY `idx_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证令牌表';
```

### 2.4 刷新令牌表 (auth_refresh_token)
```sql
CREATE TABLE `auth_refresh_token` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '刷新令牌ID',
    `token_id` varchar(64) NOT NULL COMMENT '令牌标识',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `client_id` varchar(64) NOT NULL COMMENT '客户端ID',
    `token_value` varchar(255) NOT NULL COMMENT '令牌值',
    `scope` varchar(255) DEFAULT NULL COMMENT '授权范围',
    `expires_at` datetime NOT NULL COMMENT '过期时间',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-无效，1-有效',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_token_id` (`token_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_client_id` (`client_id`),
    KEY `idx_status` (`status`),
    KEY `idx_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='刷新令牌表';
```

### 2.5 授权码表 (auth_code)
```sql
CREATE TABLE `auth_code` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '授权码ID',
    `code` varchar(64) NOT NULL COMMENT '授权码',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `client_id` varchar(64) NOT NULL COMMENT '客户端ID',
    `redirect_uri` varchar(255) NOT NULL COMMENT '重定向URI',
    `scope` varchar(255) DEFAULT NULL COMMENT '授权范围',
    `expires_at` datetime NOT NULL COMMENT '过期时间',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-无效，1-有效',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_client_id` (`client_id`),
    KEY `idx_status` (`status`),
    KEY `idx_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授权码表';
```

### 2.6 认证日志表 (auth_log)
```sql
CREATE TABLE `auth_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` bigint DEFAULT NULL COMMENT '用户ID',
    `username` varchar(64) DEFAULT NULL COMMENT '用户名',
    `client_id` varchar(64) NOT NULL COMMENT '客户端ID',
    `operation` varchar(64) NOT NULL COMMENT '操作类型',
    `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
    `location` varchar(255) DEFAULT NULL COMMENT '操作地点',
    `user_agent` varchar(255) DEFAULT NULL COMMENT '用户代理',
    `status` tinyint NOT NULL COMMENT '状态：0-失败，1-成功',
    `error_msg` varchar(255) DEFAULT NULL COMMENT '错误消息',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_client_id` (`client_id`),
    KEY `idx_operation` (`operation`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证日志表';
```

### 2.7 认证黑名单表 (auth_blacklist)
```sql
CREATE TABLE `auth_blacklist` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `type` tinyint NOT NULL COMMENT '类型：1-IP，2-用户，3-设备',
    `value` varchar(255) NOT NULL COMMENT '值',
    `reason` varchar(255) DEFAULT NULL COMMENT '原因',
    `expires_at` datetime DEFAULT NULL COMMENT '过期时间',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_by` bigint NOT NULL COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_value` (`type`, `value`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_expires_at` (`expires_at`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证黑名单表';
```

### 2.8 认证白名单表 (auth_whitelist)
```sql
CREATE TABLE `auth_whitelist` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `type` tinyint NOT NULL COMMENT '类型：1-IP，2-用户，3-设备',
    `value` varchar(255) NOT NULL COMMENT '值',
    `reason` varchar(255) DEFAULT NULL COMMENT '原因',
    `expires_at` datetime DEFAULT NULL COMMENT '过期时间',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_by` bigint NOT NULL COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_value` (`type`, `value`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_expires_at` (`expires_at`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证白名单表';
```

### 2.9 认证设备表 (auth_device)
```sql
CREATE TABLE `auth_device` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设备ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `device_id` varchar(64) NOT NULL COMMENT '设备标识',
    `device_name` varchar(64) DEFAULT NULL COMMENT '设备名称',
    `device_type` varchar(32) DEFAULT NULL COMMENT '设备类型',
    `device_model` varchar(64) DEFAULT NULL COMMENT '设备型号',
    `os_name` varchar(64) DEFAULT NULL COMMENT '操作系统',
    `os_version` varchar(32) DEFAULT NULL COMMENT '系统版本',
    `app_name` varchar(64) DEFAULT NULL COMMENT '应用名称',
    `app_version` varchar(32) DEFAULT NULL COMMENT '应用版本',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` varchar(64) DEFAULT NULL COMMENT '最后登录IP',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_device` (`user_id`, `device_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_device_id` (`device_id`),
    KEY `idx_status` (`status`),
    KEY `idx_last_login_time` (`last_login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证设备表';
```

### 2.10 认证IP表 (auth_ip)
```sql
CREATE TABLE `auth_ip` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'IP ID',
    `ip` varchar(64) NOT NULL COMMENT 'IP地址',
    `location` varchar(255) DEFAULT NULL COMMENT '地理位置',
    `isp` varchar(64) DEFAULT NULL COMMENT '运营商',
    `login_count` int NOT NULL DEFAULT '0' COMMENT '登录次数',
    `fail_count` int NOT NULL DEFAULT '0' COMMENT '失败次数',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `last_login_user` varchar(64) DEFAULT NULL COMMENT '最后登录用户',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ip` (`ip`),
    KEY `idx_status` (`status`),
    KEY `idx_last_login_time` (`last_login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证IP表';
```

## 3. 索引设计

### 3.1 认证用户表索引
1. 主键索引：id
2. 唯一索引：username
3. 普通索引：status, created_at

### 3.2 认证客户端表索引
1. 主键索引：id
2. 唯一索引：client_id
3. 普通索引：client_type, status

### 3.3 认证令牌表索引
1. 主键索引：id
2. 唯一索引：token_id
3. 普通索引：user_id, client_id, token_type, status, expires_at

### 3.4 刷新令牌表索引
1. 主键索引：id
2. 唯一索引：token_id
3. 普通索引：user_id, client_id, status, expires_at

### 3.5 授权码表索引
1. 主键索引：id
2. 唯一索引：code
3. 普通索引：user_id, client_id, status, expires_at

### 3.6 认证日志表索引
1. 主键索引：id
2. 普通索引：user_id, username, client_id, operation, status, created_at

### 3.7 认证黑名单表索引
1. 主键索引：id
2. 唯一索引：(type, value)
3. 普通索引：type, status, expires_at, created_by

### 3.8 认证白名单表索引
1. 主键索引：id
2. 唯一索引：(type, value)
3. 普通索引：type, status, expires_at, created_by

### 3.9 认证设备表索引
1. 主键索引：id
2. 唯一索引：(user_id, device_id)
3. 普通索引：user_id, device_id, status, last_login_time

### 3.10 认证IP表索引
1. 主键索引：id
2. 唯一索引：ip
3. 普通索引：status, last_login_time

## 4. 字段说明

### 4.1 通用字段
- id：主键，bigint，自增
- created_at：创建时间，datetime
- updated_at：更新时间，datetime
- deleted_at：删除时间，datetime
- status：状态，tinyint
- remark：备注，varchar(255)

### 4.2 认证用户表字段
- username：用户名，varchar(64)
- password：密码，varchar(128)
- salt：盐值，varchar(32)
- account_non_expired：账号是否过期，tinyint
- account_non_locked：账号是否锁定，tinyint
- credentials_non_expired：凭证是否过期，tinyint
- enabled：是否启用，tinyint
- last_login_time：最后登录时间，datetime
- last_login_ip：最后登录IP，varchar(64)

### 4.3 认证客户端表字段
- client_id：客户端标识，varchar(64)
- client_secret：客户端密钥，varchar(128)
- client_name：客户端名称，varchar(64)
- client_type：客户端类型，tinyint
- redirect_uri：重定向URI，varchar(255)
- scope：授权范围，varchar(255)
- authorized_grant_types：授权类型，varchar(255)
- access_token_validity：访问令牌有效期，int
- refresh_token_validity：刷新令牌有效期，int

### 4.4 认证令牌表字段
- token_id：令牌标识，varchar(64)
- user_id：用户ID，bigint
- username：用户名，varchar(64)
- client_id：客户端ID，varchar(64)
- token_type：令牌类型，varchar(32)
- token_value：令牌值，varchar(255)
- scope：授权范围，varchar(255)
- expires_at：过期时间，datetime

### 4.5 刷新令牌表字段
- token_id：令牌标识，varchar(64)
- user_id：用户ID，bigint
- username：用户名，varchar(64)
- client_id：客户端ID，varchar(64)
- token_value：令牌值，varchar(255)
- scope：授权范围，varchar(255)
- expires_at：过期时间，datetime

### 4.6 授权码表字段
- code：授权码，varchar(64)
- user_id：用户ID，bigint
- username：用户名，varchar(64)
- client_id：客户端ID，varchar(64)
- redirect_uri：重定向URI，varchar(255)
- scope：授权范围，varchar(255)
- expires_at：过期时间，datetime

### 4.7 认证日志表字段
- user_id：用户ID，bigint
- username：用户名，varchar(64)
- client_id：客户端ID，varchar(64)
- operation：操作类型，varchar(64)
- ip：IP地址，varchar(64)
- location：操作地点，varchar(255)
- user_agent：用户代理，varchar(255)
- error_msg：错误消息，varchar(255)

### 4.8 认证黑名单表字段
- type：类型，tinyint
- value：值，varchar(255)
- reason：原因，varchar(255)
- expires_at：过期时间，datetime
- created_by：创建人，bigint

### 4.9 认证白名单表字段
- type：类型，tinyint
- value：值，varchar(255)
- reason：原因，varchar(255)
- expires_at：过期时间，datetime
- created_by：创建人，bigint

### 4.10 认证设备表字段
- user_id：用户ID，bigint
- device_id：设备标识，varchar(64)
- device_name：设备名称，varchar(64)
- device_type：设备类型，varchar(32)
- device_model：设备型号，varchar(64)
- os_name：操作系统，varchar(64)
- os_version：系统版本，varchar(32)
- app_name：应用名称，varchar(64)
- app_version：应用版本，varchar(32)
- last_login_time：最后登录时间，datetime
- last_login_ip：最后登录IP，varchar(64)

### 4.11 认证IP表字段
- ip：IP地址，varchar(64)
- location：地理位置，varchar(255)
- isp：运营商，varchar(64)
- login_count：登录次数，int
- fail_count：失败次数，int
- last_login_time：最后登录时间，datetime
- last_login_user：最后登录用户，varchar(64)

## 5. 更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-xx | v1.0.0 | 初始版本 | - | 