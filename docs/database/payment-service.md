# 支付服务数据库设计

## 1. 数据库概述

### 1.1 数据库信息
- 数据库名：mall_payment
- 字符集：utf8mb4
- 排序规则：utf8mb4_general_ci
- 存储引擎：InnoDB

### 1.2 表清单
1. 支付订单表 (payment_order)
2. 支付渠道表 (payment_channel)
3. 支付渠道配置表 (payment_channel_config)
4. 支付交易记录表 (payment_transaction)
5. 退款订单表 (payment_refund)
6. 退款交易记录表 (payment_refund_transaction)
7. 对账记录表 (payment_reconciliation)
8. 支付通知记录表 (payment_notify)
9. 支付日志表 (payment_log)

## 2. 表结构设计

### 2.1 支付订单表 (payment_order)
```sql
CREATE TABLE `payment_order` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付订单ID',
    `payment_no` varchar(64) NOT NULL COMMENT '支付单号',
    `order_id` bigint NOT NULL COMMENT '订单ID',
    `order_no` varchar(64) NOT NULL COMMENT '订单编号',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
    `pay_type` tinyint NOT NULL COMMENT '支付方式：1-微信，2-支付宝，3-银联',
    `pay_channel` varchar(32) NOT NULL COMMENT '支付渠道',
    `status` tinyint NOT NULL COMMENT '支付状态：0-待支付，1-支付中，2-支付成功，3-支付失败，4-已关闭',
    `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
    `expire_time` datetime NOT NULL COMMENT '过期时间',
    `close_time` datetime DEFAULT NULL COMMENT '关闭时间',
    `transaction_id` varchar(64) DEFAULT NULL COMMENT '第三方交易号',
    `return_url` varchar(255) DEFAULT NULL COMMENT '返回地址',
    `notify_url` varchar(255) DEFAULT NULL COMMENT '通知地址',
    `notify_status` tinyint NOT NULL DEFAULT '0' COMMENT '通知状态：0-未通知，1-通知成功，2-通知失败',
    `notify_time` datetime DEFAULT NULL COMMENT '通知时间',
    `notify_count` int NOT NULL DEFAULT '0' COMMENT '通知次数',
    `error_code` varchar(32) DEFAULT NULL COMMENT '错误码',
    `error_msg` varchar(255) DEFAULT NULL COMMENT '错误信息',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_pay_time` (`pay_time`),
    KEY `idx_expire_time` (`expire_time`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付订单表';
```

### 2.2 支付渠道表 (payment_channel)
```sql
CREATE TABLE `payment_channel` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '渠道ID',
    `code` varchar(32) NOT NULL COMMENT '渠道编码',
    `name` varchar(64) NOT NULL COMMENT '渠道名称',
    `type` tinyint NOT NULL COMMENT '渠道类型：1-微信，2-支付宝，3-银联',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付渠道表';
```

### 2.3 支付渠道配置表 (payment_channel_config)
```sql
CREATE TABLE `payment_channel_config` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `channel_id` bigint NOT NULL COMMENT '渠道ID',
    `config_key` varchar(64) NOT NULL COMMENT '配置键',
    `config_value` text NOT NULL COMMENT '配置值',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_channel_key` (`channel_id`, `config_key`),
    KEY `idx_channel_id` (`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付渠道配置表';
```

### 2.4 支付交易记录表 (payment_transaction)
```sql
CREATE TABLE `payment_transaction` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '交易ID',
    `payment_id` bigint NOT NULL COMMENT '支付订单ID',
    `payment_no` varchar(64) NOT NULL COMMENT '支付单号',
    `transaction_id` varchar(64) NOT NULL COMMENT '第三方交易号',
    `pay_type` tinyint NOT NULL COMMENT '支付方式',
    `pay_channel` varchar(32) NOT NULL COMMENT '支付渠道',
    `amount` decimal(10,2) NOT NULL COMMENT '交易金额',
    `status` tinyint NOT NULL COMMENT '交易状态：0-处理中，1-成功，2-失败',
    `trade_time` datetime NOT NULL COMMENT '交易时间',
    `trade_type` varchar(32) NOT NULL COMMENT '交易类型',
    `trade_account` varchar(64) NOT NULL COMMENT '交易账号',
    `trade_name` varchar(64) NOT NULL COMMENT '交易名称',
    `trade_memo` varchar(255) DEFAULT NULL COMMENT '交易备注',
    `error_code` varchar(32) DEFAULT NULL COMMENT '错误码',
    `error_msg` varchar(255) DEFAULT NULL COMMENT '错误信息',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_transaction_id` (`transaction_id`),
    KEY `idx_payment_id` (`payment_id`),
    KEY `idx_payment_no` (`payment_no`),
    KEY `idx_status` (`status`),
    KEY `idx_trade_time` (`trade_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付交易记录表';
```

### 2.5 退款订单表 (payment_refund)
```sql
CREATE TABLE `payment_refund` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '退款订单ID',
    `refund_no` varchar(64) NOT NULL COMMENT '退款单号',
    `payment_id` bigint NOT NULL COMMENT '支付订单ID',
    `payment_no` varchar(64) NOT NULL COMMENT '支付单号',
    `order_id` bigint NOT NULL COMMENT '订单ID',
    `order_no` varchar(64) NOT NULL COMMENT '订单编号',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `amount` decimal(10,2) NOT NULL COMMENT '退款金额',
    `reason` varchar(255) NOT NULL COMMENT '退款原因',
    `status` tinyint NOT NULL COMMENT '退款状态：0-待处理，1-退款中，2-退款成功，3-退款失败',
    `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
    `transaction_id` varchar(64) DEFAULT NULL COMMENT '第三方交易号',
    `notify_url` varchar(255) DEFAULT NULL COMMENT '通知地址',
    `notify_status` tinyint NOT NULL DEFAULT '0' COMMENT '通知状态：0-未通知，1-通知成功，2-通知失败',
    `notify_time` datetime DEFAULT NULL COMMENT '通知时间',
    `notify_count` int NOT NULL DEFAULT '0' COMMENT '通知次数',
    `error_code` varchar(32) DEFAULT NULL COMMENT '错误码',
    `error_msg` varchar(255) DEFAULT NULL COMMENT '错误信息',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`),
    KEY `idx_payment_id` (`payment_id`),
    KEY `idx_payment_no` (`payment_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_refund_time` (`refund_time`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款订单表';
```

### 2.6 退款交易记录表 (payment_refund_transaction)
```sql
CREATE TABLE `payment_refund_transaction` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '交易ID',
    `refund_id` bigint NOT NULL COMMENT '退款订单ID',
    `refund_no` varchar(64) NOT NULL COMMENT '退款单号',
    `transaction_id` varchar(64) NOT NULL COMMENT '第三方交易号',
    `pay_type` tinyint NOT NULL COMMENT '支付方式',
    `pay_channel` varchar(32) NOT NULL COMMENT '支付渠道',
    `amount` decimal(10,2) NOT NULL COMMENT '交易金额',
    `status` tinyint NOT NULL COMMENT '交易状态：0-处理中，1-成功，2-失败',
    `trade_time` datetime NOT NULL COMMENT '交易时间',
    `trade_type` varchar(32) NOT NULL COMMENT '交易类型',
    `trade_account` varchar(64) NOT NULL COMMENT '交易账号',
    `trade_name` varchar(64) NOT NULL COMMENT '交易名称',
    `trade_memo` varchar(255) DEFAULT NULL COMMENT '交易备注',
    `error_code` varchar(32) DEFAULT NULL COMMENT '错误码',
    `error_msg` varchar(255) DEFAULT NULL COMMENT '错误信息',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_transaction_id` (`transaction_id`),
    KEY `idx_refund_id` (`refund_id`),
    KEY `idx_refund_no` (`refund_no`),
    KEY `idx_status` (`status`),
    KEY `idx_trade_time` (`trade_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款交易记录表';
```

### 2.7 对账记录表 (payment_reconciliation)
```sql
CREATE TABLE `payment_reconciliation` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '对账ID',
    `channel_id` bigint NOT NULL COMMENT '渠道ID',
    `channel_code` varchar(32) NOT NULL COMMENT '渠道编码',
    `reconcile_date` date NOT NULL COMMENT '对账日期',
    `total_count` int NOT NULL DEFAULT '0' COMMENT '总笔数',
    `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '总金额',
    `success_count` int NOT NULL DEFAULT '0' COMMENT '成功笔数',
    `success_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '成功金额',
    `fail_count` int NOT NULL DEFAULT '0' COMMENT '失败笔数',
    `fail_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '失败金额',
    `status` tinyint NOT NULL COMMENT '状态：0-待对账，1-对账中，2-对账成功，3-对账失败',
    `check_time` datetime DEFAULT NULL COMMENT '对账时间',
    `check_user` varchar(64) DEFAULT NULL COMMENT '对账人',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_channel_date` (`channel_id`, `reconcile_date`),
    KEY `idx_channel_code` (`channel_code`),
    KEY `idx_reconcile_date` (`reconcile_date`),
    KEY `idx_status` (`status`),
    KEY `idx_check_time` (`check_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对账记录表';
```

### 2.8 支付通知记录表 (payment_notify)
```sql
CREATE TABLE `payment_notify` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `notify_no` varchar(64) NOT NULL COMMENT '通知编号',
    `payment_id` bigint DEFAULT NULL COMMENT '支付订单ID',
    `refund_id` bigint DEFAULT NULL COMMENT '退款订单ID',
    `notify_type` tinyint NOT NULL COMMENT '通知类型：1-支付通知，2-退款通知',
    `notify_url` varchar(255) NOT NULL COMMENT '通知地址',
    `notify_data` text NOT NULL COMMENT '通知数据',
    `status` tinyint NOT NULL COMMENT '通知状态：0-待通知，1-通知成功，2-通知失败',
    `notify_time` datetime DEFAULT NULL COMMENT '通知时间',
    `notify_count` int NOT NULL DEFAULT '0' COMMENT '通知次数',
    `next_notify_time` datetime DEFAULT NULL COMMENT '下次通知时间',
    `error_code` varchar(32) DEFAULT NULL COMMENT '错误码',
    `error_msg` varchar(255) DEFAULT NULL COMMENT '错误信息',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_notify_no` (`notify_no`),
    KEY `idx_payment_id` (`payment_id`),
    KEY `idx_refund_id` (`refund_id`),
    KEY `idx_notify_type` (`notify_type`),
    KEY `idx_status` (`status`),
    KEY `idx_notify_time` (`notify_time`),
    KEY `idx_next_notify_time` (`next_notify_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付通知记录表';
```

### 2.9 支付日志表 (payment_log)
```sql
CREATE TABLE `payment_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `payment_id` bigint DEFAULT NULL COMMENT '支付订单ID',
    `refund_id` bigint DEFAULT NULL COMMENT '退款订单ID',
    `log_type` tinyint NOT NULL COMMENT '日志类型：1-支付日志，2-退款日志，3-通知日志',
    `log_level` tinyint NOT NULL COMMENT '日志级别：1-INFO，2-WARN，3-ERROR',
    `log_content` text NOT NULL COMMENT '日志内容',
    `request_data` text DEFAULT NULL COMMENT '请求数据',
    `response_data` text DEFAULT NULL COMMENT '响应数据',
    `error_code` varchar(32) DEFAULT NULL COMMENT '错误码',
    `error_msg` varchar(255) DEFAULT NULL COMMENT '错误信息',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_payment_id` (`payment_id`),
    KEY `idx_refund_id` (`refund_id`),
    KEY `idx_log_type` (`log_type`),
    KEY `idx_log_level` (`log_level`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付日志表';
```

## 3. 索引设计

### 3.1 支付订单表索引
1. 主键索引：id
2. 唯一索引：payment_no, order_no
3. 普通索引：user_id, status, pay_time, expire_time, created_at

### 3.2 支付渠道表索引
1. 主键索引：id
2. 唯一索引：code
3. 普通索引：type, status, sort

### 3.3 支付渠道配置表索引
1. 主键索引：id
2. 唯一索引：(channel_id, config_key)
3. 普通索引：channel_id

### 3.4 支付交易记录表索引
1. 主键索引：id
2. 唯一索引：transaction_id
3. 普通索引：payment_id, payment_no, status, trade_time

### 3.5 退款订单表索引
1. 主键索引：id
2. 唯一索引：refund_no
3. 普通索引：payment_id, payment_no, order_id, order_no, user_id, status, refund_time, created_at

### 3.6 退款交易记录表索引
1. 主键索引：id
2. 唯一索引：transaction_id
3. 普通索引：refund_id, refund_no, status, trade_time

### 3.7 对账记录表索引
1. 主键索引：id
2. 唯一索引：(channel_id, reconcile_date)
3. 普通索引：channel_code, reconcile_date, status, check_time

### 3.8 支付通知记录表索引
1. 主键索引：id
2. 唯一索引：notify_no
3. 普通索引：payment_id, refund_id, notify_type, status, notify_time, next_notify_time

### 3.9 支付日志表索引
1. 主键索引：id
2. 普通索引：payment_id, refund_id, log_type, log_level, created_at

## 4. 字段说明

### 4.1 通用字段
- id：主键，bigint，自增
- created_at：创建时间，datetime
- updated_at：更新时间，datetime
- status：状态，tinyint
- error_code：错误码，varchar(32)
- error_msg：错误信息，varchar(255)

### 4.2 支付订单表字段
- payment_no：支付单号，varchar(64)
- order_id：订单ID，bigint
- order_no：订单编号，varchar(64)
- user_id：用户ID，bigint
- amount：支付金额，decimal(10,2)
- pay_type：支付方式，tinyint
- pay_channel：支付渠道，varchar(32)
- pay_time：支付时间，datetime
- expire_time：过期时间，datetime
- close_time：关闭时间，datetime
- transaction_id：第三方交易号，varchar(64)
- return_url：返回地址，varchar(255)
- notify_url：通知地址，varchar(255)
- notify_status：通知状态，tinyint
- notify_time：通知时间，datetime
- notify_count：通知次数，int

### 4.3 支付渠道表字段
- code：渠道编码，varchar(32)
- name：渠道名称，varchar(64)
- type：渠道类型，tinyint
- sort：排序，int
- remark：备注，varchar(255)

### 4.4 支付渠道配置表字段
- channel_id：渠道ID，bigint
- config_key：配置键，varchar(64)
- config_value：配置值，text
- remark：备注，varchar(255)

### 4.5 支付交易记录表字段
- payment_id：支付订单ID，bigint
- payment_no：支付单号，varchar(64)
- transaction_id：第三方交易号，varchar(64)
- pay_type：支付方式，tinyint
- pay_channel：支付渠道，varchar(32)
- amount：交易金额，decimal(10,2)
- trade_time：交易时间，datetime
- trade_type：交易类型，varchar(32)
- trade_account：交易账号，varchar(64)
- trade_name：交易名称，varchar(64)
- trade_memo：交易备注，varchar(255)

### 4.6 退款订单表字段
- refund_no：退款单号，varchar(64)
- payment_id：支付订单ID，bigint
- payment_no：支付单号，varchar(64)
- order_id：订单ID，bigint
- order_no：订单编号，varchar(64)
- user_id：用户ID，bigint
- amount：退款金额，decimal(10,2)
- reason：退款原因，varchar(255)
- refund_time：退款时间，datetime
- transaction_id：第三方交易号，varchar(64)
- notify_url：通知地址，varchar(255)
- notify_status：通知状态，tinyint
- notify_time：通知时间，datetime
- notify_count：通知次数，int

### 4.7 退款交易记录表字段
- refund_id：退款订单ID，bigint
- refund_no：退款单号，varchar(64)
- transaction_id：第三方交易号，varchar(64)
- pay_type：支付方式，tinyint
- pay_channel：支付渠道，varchar(32)
- amount：交易金额，decimal(10,2)
- trade_time：交易时间，datetime
- trade_type：交易类型，varchar(32)
- trade_account：交易账号，varchar(64)
- trade_name：交易名称，varchar(64)
- trade_memo：交易备注，varchar(255)

### 4.8 对账记录表字段
- channel_id：渠道ID，bigint
- channel_code：渠道编码，varchar(32)
- reconcile_date：对账日期，date
- total_count：总笔数，int
- total_amount：总金额，decimal(10,2)
- success_count：成功笔数，int
- success_amount：成功金额，decimal(10,2)
- fail_count：失败笔数，int
- fail_amount：失败金额，decimal(10,2)
- check_time：对账时间，datetime
- check_user：对账人，varchar(64)
- remark：备注，varchar(255)

### 4.9 支付通知记录表字段
- notify_no：通知编号，varchar(64)
- payment_id：支付订单ID，bigint
- refund_id：退款订单ID，bigint
- notify_type：通知类型，tinyint
- notify_url：通知地址，varchar(255)
- notify_data：通知数据，text
- notify_time：通知时间，datetime
- notify_count：通知次数，int
- next_notify_time：下次通知时间，datetime

### 4.10 支付日志表字段
- payment_id：支付订单ID，bigint
- refund_id：退款订单ID，bigint
- log_type：日志类型，tinyint
- log_level：日志级别，tinyint
- log_content：日志内容，text
- request_data：请求数据，text
- response_data：响应数据，text

## 5. 更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-xx | v1.0.0 | 初始版本 | - | 