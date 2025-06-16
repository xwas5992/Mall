# 订单服务数据库设计

## 1. 数据库概述

### 1.1 数据库信息
- 数据库名：mall_order
- 字符集：utf8mb4
- 排序规则：utf8mb4_general_ci
- 存储引擎：InnoDB

### 1.2 表清单
1. 订单表 (order)
2. 订单明细表 (order_item)
3. 购物车表 (cart)
4. 购物车明细表 (cart_item)
5. 优惠券表 (coupon)
6. 优惠券领取表 (coupon_user)
7. 优惠券使用记录表 (coupon_usage)
8. 订单操作日志表 (order_operation_log)
9. 订单支付记录表 (order_payment)
10. 订单退款记录表 (order_refund)

## 2. 表结构设计

### 2.1 订单表 (order)
```sql
CREATE TABLE `order` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` varchar(64) NOT NULL COMMENT '订单编号',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
    `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
    `freight_amount` decimal(10,2) NOT NULL COMMENT '运费金额',
    `discount_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠金额',
    `coupon_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠券金额',
    `pay_type` tinyint DEFAULT NULL COMMENT '支付方式：1-微信，2-支付宝，3-银联',
    `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
    `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
    `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
    `comment_time` datetime DEFAULT NULL COMMENT '评价时间',
    `status` tinyint NOT NULL COMMENT '订单状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消，5-已退款',
    `receiver` varchar(64) NOT NULL COMMENT '收货人',
    `phone` varchar(32) NOT NULL COMMENT '手机号',
    `province` varchar(64) NOT NULL COMMENT '省份',
    `city` varchar(64) NOT NULL COMMENT '城市',
    `district` varchar(64) NOT NULL COMMENT '区县',
    `detail` varchar(255) NOT NULL COMMENT '详细地址',
    `note` varchar(255) DEFAULT NULL COMMENT '订单备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
```

### 2.2 订单明细表 (order_item)
```sql
CREATE TABLE `order_item` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id` bigint NOT NULL COMMENT '订单ID',
    `order_no` varchar(64) NOT NULL COMMENT '订单编号',
    `product_id` bigint NOT NULL COMMENT '商品ID',
    `sku_id` bigint NOT NULL COMMENT 'SKU ID',
    `product_name` varchar(128) NOT NULL COMMENT '商品名称',
    `product_image` varchar(255) NOT NULL COMMENT '商品图片',
    `sku_code` varchar(64) NOT NULL COMMENT 'SKU编码',
    `specs` json NOT NULL COMMENT '规格JSON',
    `price` decimal(10,2) NOT NULL COMMENT '销售价',
    `quantity` int NOT NULL COMMENT '购买数量',
    `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
    `discount_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠金额',
    `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';
```

### 2.3 购物车表 (cart)
```sql
CREATE TABLE `cart` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `selected` tinyint NOT NULL DEFAULT '1' COMMENT '是否全选：0-否，1-是',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';
```

### 2.4 购物车明细表 (cart_item)
```sql
CREATE TABLE `cart_item` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `cart_id` bigint NOT NULL COMMENT '购物车ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `product_id` bigint NOT NULL COMMENT '商品ID',
    `sku_id` bigint NOT NULL COMMENT 'SKU ID',
    `quantity` int NOT NULL COMMENT '购买数量',
    `selected` tinyint NOT NULL DEFAULT '1' COMMENT '是否选中：0-否，1-是',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_sku` (`user_id`, `sku_id`),
    KEY `idx_cart_id` (`cart_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车明细表';
```

### 2.5 优惠券表 (coupon)
```sql
CREATE TABLE `coupon` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
    `name` varchar(64) NOT NULL COMMENT '优惠券名称',
    `type` tinyint NOT NULL COMMENT '类型：1-满减券，2-折扣券，3-无门槛券',
    `amount` decimal(10,2) NOT NULL COMMENT '优惠金额/折扣率',
    `min_point` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '使用门槛',
    `start_time` datetime NOT NULL COMMENT '开始时间',
    `end_time` datetime NOT NULL COMMENT '结束时间',
    `per_limit` int NOT NULL DEFAULT '1' COMMENT '每人限领数量',
    `total` int NOT NULL COMMENT '发行数量',
    `used` int NOT NULL DEFAULT '0' COMMENT '已使用数量',
    `received` int NOT NULL DEFAULT '0' COMMENT '已领取数量',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';
```

### 2.6 优惠券领取表 (coupon_user)
```sql
CREATE TABLE `coupon_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '领取ID',
    `coupon_id` bigint NOT NULL COMMENT '优惠券ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-未使用，1-已使用，2-已过期',
    `get_time` datetime NOT NULL COMMENT '领取时间',
    `use_time` datetime DEFAULT NULL COMMENT '使用时间',
    `order_id` bigint DEFAULT NULL COMMENT '订单ID',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_coupon` (`user_id`, `coupon_id`),
    KEY `idx_coupon_id` (`coupon_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_get_time` (`get_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券领取表';
```

### 2.7 优惠券使用记录表 (coupon_usage)
```sql
CREATE TABLE `coupon_usage` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `coupon_id` bigint NOT NULL COMMENT '优惠券ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `order_id` bigint NOT NULL COMMENT '订单ID',
    `amount` decimal(10,2) NOT NULL COMMENT '优惠金额',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_coupon_id` (`coupon_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券使用记录表';
```

### 2.8 订单操作日志表 (order_operation_log)
```sql
CREATE TABLE `order_operation_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `order_id` bigint NOT NULL COMMENT '订单ID',
    `order_no` varchar(64) NOT NULL COMMENT '订单编号',
    `operator` varchar(64) NOT NULL COMMENT '操作人',
    `operation` varchar(64) NOT NULL COMMENT '操作类型',
    `status` tinyint NOT NULL COMMENT '订单状态',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_operator` (`operator`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作日志表';
```

### 2.9 订单支付记录表 (order_payment)
```sql
CREATE TABLE `order_payment` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付ID',
    `order_id` bigint NOT NULL COMMENT '订单ID',
    `order_no` varchar(64) NOT NULL COMMENT '订单编号',
    `payment_no` varchar(64) NOT NULL COMMENT '支付单号',
    `pay_type` tinyint NOT NULL COMMENT '支付方式',
    `pay_channel` varchar(32) NOT NULL COMMENT '支付渠道',
    `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
    `status` tinyint NOT NULL COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败',
    `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
    `transaction_id` varchar(64) DEFAULT NULL COMMENT '第三方交易号',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_status` (`status`),
    KEY `idx_pay_time` (`pay_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单支付记录表';
```

### 2.10 订单退款记录表 (order_refund)
```sql
CREATE TABLE `order_refund` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '退款ID',
    `order_id` bigint NOT NULL COMMENT '订单ID',
    `order_no` varchar(64) NOT NULL COMMENT '订单编号',
    `payment_id` bigint NOT NULL COMMENT '支付ID',
    `refund_no` varchar(64) NOT NULL COMMENT '退款单号',
    `amount` decimal(10,2) NOT NULL COMMENT '退款金额',
    `reason` varchar(255) NOT NULL COMMENT '退款原因',
    `status` tinyint NOT NULL COMMENT '退款状态：0-待处理，1-退款中，2-退款成功，3-退款失败',
    `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
    `transaction_id` varchar(64) DEFAULT NULL COMMENT '第三方交易号',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_payment_id` (`payment_id`),
    KEY `idx_status` (`status`),
    KEY `idx_refund_time` (`refund_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单退款记录表';
```

## 3. 索引设计

### 3.1 订单表索引
1. 主键索引：id
2. 唯一索引：order_no
3. 普通索引：user_id, status, created_at

### 3.2 订单明细表索引
1. 主键索引：id
2. 普通索引：order_id, order_no, product_id, sku_id

### 3.3 购物车表索引
1. 主键索引：id
2. 唯一索引：user_id

### 3.4 购物车明细表索引
1. 主键索引：id
2. 唯一索引：(user_id, sku_id)
3. 普通索引：cart_id, user_id, product_id, sku_id

### 3.5 优惠券表索引
1. 主键索引：id
2. 普通索引：status, (start_time, end_time)

### 3.6 优惠券领取表索引
1. 主键索引：id
2. 唯一索引：(user_id, coupon_id)
3. 普通索引：coupon_id, user_id, status, get_time

### 3.7 优惠券使用记录表索引
1. 主键索引：id
2. 普通索引：coupon_id, user_id, order_id, created_at

### 3.8 订单操作日志表索引
1. 主键索引：id
2. 普通索引：order_id, order_no, operator, created_at

### 3.9 订单支付记录表索引
1. 主键索引：id
2. 唯一索引：payment_no
3. 普通索引：order_id, order_no, status, pay_time

### 3.10 订单退款记录表索引
1. 主键索引：id
2. 唯一索引：refund_no
3. 普通索引：order_id, order_no, payment_id, status, refund_time

## 4. 字段说明

### 4.1 通用字段
- id：主键，bigint，自增
- created_at：创建时间，datetime
- updated_at：更新时间，datetime
- deleted_at：删除时间，datetime
- status：状态，tinyint

### 4.2 订单表字段
- order_no：订单编号，varchar(64)
- user_id：用户ID，bigint
- total_amount：订单总金额，decimal(10,2)
- pay_amount：实付金额，decimal(10,2)
- freight_amount：运费金额，decimal(10,2)
- discount_amount：优惠金额，decimal(10,2)
- coupon_amount：优惠券金额，decimal(10,2)
- pay_type：支付方式，tinyint
- pay_time：支付时间，datetime
- delivery_time：发货时间，datetime
- receive_time：收货时间，datetime
- comment_time：评价时间，datetime
- receiver：收货人，varchar(64)
- phone：手机号，varchar(32)
- province：省份，varchar(64)
- city：城市，varchar(64)
- district：区县，varchar(64)
- detail：详细地址，varchar(255)
- note：订单备注，varchar(255)

### 4.3 订单明细表字段
- order_id：订单ID，bigint
- order_no：订单编号，varchar(64)
- product_id：商品ID，bigint
- sku_id：SKU ID，bigint
- product_name：商品名称，varchar(128)
- product_image：商品图片，varchar(255)
- sku_code：SKU编码，varchar(64)
- specs：规格JSON，json
- price：销售价，decimal(10,2)
- quantity：购买数量，int
- total_amount：总金额，decimal(10,2)
- discount_amount：优惠金额，decimal(10,2)
- pay_amount：实付金额，decimal(10,2)

### 4.4 购物车表字段
- user_id：用户ID，bigint
- selected：是否全选，tinyint

### 4.5 购物车明细表字段
- cart_id：购物车ID，bigint
- user_id：用户ID，bigint
- product_id：商品ID，bigint
- sku_id：SKU ID，bigint
- quantity：购买数量，int
- selected：是否选中，tinyint

### 4.6 优惠券表字段
- name：优惠券名称，varchar(64)
- type：类型，tinyint
- amount：优惠金额/折扣率，decimal(10,2)
- min_point：使用门槛，decimal(10,2)
- start_time：开始时间，datetime
- end_time：结束时间，datetime
- per_limit：每人限领数量，int
- total：发行数量，int
- used：已使用数量，int
- received：已领取数量，int

### 4.7 优惠券领取表字段
- coupon_id：优惠券ID，bigint
- user_id：用户ID，bigint
- status：状态，tinyint
- get_time：领取时间，datetime
- use_time：使用时间，datetime
- order_id：订单ID，bigint

### 4.8 优惠券使用记录表字段
- coupon_id：优惠券ID，bigint
- user_id：用户ID，bigint
- order_id：订单ID，bigint
- amount：优惠金额，decimal(10,2)

### 4.9 订单操作日志表字段
- order_id：订单ID，bigint
- order_no：订单编号，varchar(64)
- operator：操作人，varchar(64)
- operation：操作类型，varchar(64)
- status：订单状态，tinyint
- remark：备注，varchar(255)

### 4.10 订单支付记录表字段
- order_id：订单ID，bigint
- order_no：订单编号，varchar(64)
- payment_no：支付单号，varchar(64)
- pay_type：支付方式，tinyint
- pay_channel：支付渠道，varchar(32)
- amount：支付金额，decimal(10,2)
- status：支付状态，tinyint
- pay_time：支付时间，datetime
- transaction_id：第三方交易号，varchar(64)

### 4.11 订单退款记录表字段
- order_id：订单ID，bigint
- order_no：订单编号，varchar(64)
- payment_id：支付ID，bigint
- refund_no：退款单号，varchar(64)
- amount：退款金额，decimal(10,2)
- reason：退款原因，varchar(255)
- status：退款状态，tinyint
- refund_time：退款时间，datetime
- transaction_id：第三方交易号，varchar(64)

## 5. 更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-xx | v1.0.0 | 初始版本 | - | 