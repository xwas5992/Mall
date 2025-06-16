# 商品服务数据库设计

## 1. 数据库概述

### 1.1 数据库信息
- 数据库名：mall_product
- 字符集：utf8mb4
- 排序规则：utf8mb4_general_ci
- 存储引擎：InnoDB

### 1.2 表清单
1. 商品表 (product)
2. 商品SKU表 (product_sku)
3. 商品分类表 (product_category)
4. 商品品牌表 (product_brand)
5. 商品规格表 (product_spec)
6. 商品规格值表 (product_spec_value)
7. 商品图片表 (product_image)
8. 商品评价表 (product_review)
9. 商品库存表 (product_stock)
10. 商品库存记录表 (product_stock_record)

## 2. 表结构设计

### 2.1 商品表 (product)
```sql
CREATE TABLE `product` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name` varchar(128) NOT NULL COMMENT '商品名称',
    `category_id` bigint NOT NULL COMMENT '分类ID',
    `brand_id` bigint DEFAULT NULL COMMENT '品牌ID',
    `price` decimal(10,2) NOT NULL COMMENT '销售价',
    `original_price` decimal(10,2) NOT NULL COMMENT '原价',
    `description` text COMMENT '商品描述',
    `detail` text COMMENT '商品详情',
    `main_image` varchar(255) NOT NULL COMMENT '主图',
    `sales` int NOT NULL DEFAULT '0' COMMENT '销量',
    `stock` int NOT NULL DEFAULT '0' COMMENT '库存',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-下架，1-上架',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_brand_id` (`brand_id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort` (`sort`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
```

### 2.2 商品SKU表 (product_sku)
```sql
CREATE TABLE `product_sku` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
    `product_id` bigint NOT NULL COMMENT '商品ID',
    `sku_code` varchar(64) NOT NULL COMMENT 'SKU编码',
    `specs` json NOT NULL COMMENT '规格JSON',
    `price` decimal(10,2) NOT NULL COMMENT '销售价',
    `original_price` decimal(10,2) NOT NULL COMMENT '原价',
    `stock` int NOT NULL DEFAULT '0' COMMENT '库存',
    `image` varchar(255) DEFAULT NULL COMMENT 'SKU图片',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sku_code` (`sku_code`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';
```

### 2.3 商品分类表 (product_category)
```sql
CREATE TABLE `product_category` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父分类ID',
    `name` varchar(64) NOT NULL COMMENT '分类名称',
    `level` int NOT NULL COMMENT '层级',
    `icon` varchar(255) DEFAULT NULL COMMENT '图标',
    `image` varchar(255) DEFAULT NULL COMMENT '图片',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_level` (`level`),
    KEY `idx_sort` (`sort`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';
```

### 2.4 商品品牌表 (product_brand)
```sql
CREATE TABLE `product_brand` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '品牌ID',
    `name` varchar(64) NOT NULL COMMENT '品牌名称',
    `logo` varchar(255) DEFAULT NULL COMMENT '品牌logo',
    `description` varchar(255) DEFAULT NULL COMMENT '品牌描述',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_sort` (`sort`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品品牌表';
```

### 2.5 商品规格表 (product_spec)
```sql
CREATE TABLE `product_spec` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规格ID',
    `name` varchar(64) NOT NULL COMMENT '规格名称',
    `category_id` bigint NOT NULL COMMENT '分类ID',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_sort` (`sort`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格表';
```

### 2.6 商品规格值表 (product_spec_value)
```sql
CREATE TABLE `product_spec_value` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规格值ID',
    `spec_id` bigint NOT NULL COMMENT '规格ID',
    `value` varchar(64) NOT NULL COMMENT '规格值',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_spec_id` (`spec_id`),
    KEY `idx_sort` (`sort`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格值表';
```

### 2.7 商品图片表 (product_image)
```sql
CREATE TABLE `product_image` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `product_id` bigint NOT NULL COMMENT '商品ID',
    `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
    `url` varchar(255) NOT NULL COMMENT '图片URL',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `type` tinyint NOT NULL DEFAULT '1' COMMENT '类型：1-商品图，2-SKU图',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_sku_id` (`sku_id`),
    KEY `idx_sort` (`sort`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';
```

### 2.8 商品评价表 (product_review)
```sql
CREATE TABLE `product_review` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `product_id` bigint NOT NULL COMMENT '商品ID',
    `sku_id` bigint NOT NULL COMMENT 'SKU ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `order_id` bigint NOT NULL COMMENT '订单ID',
    `rating` tinyint NOT NULL COMMENT '评分：1-5',
    `content` text COMMENT '评价内容',
    `images` json DEFAULT NULL COMMENT '评价图片',
    `reply` text DEFAULT NULL COMMENT '商家回复',
    `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-隐藏，1-显示',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_sku_id` (`sku_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_rating` (`rating`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';
```

### 2.9 商品库存表 (product_stock)
```sql
CREATE TABLE `product_stock` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '库存ID',
    `sku_id` bigint NOT NULL COMMENT 'SKU ID',
    `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
    `stock` int NOT NULL DEFAULT '0' COMMENT '库存数量',
    `locked_stock` int NOT NULL DEFAULT '0' COMMENT '锁定库存',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sku_warehouse` (`sku_id`, `warehouse_id`),
    KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品库存表';
```

### 2.10 商品库存记录表 (product_stock_record)
```sql
CREATE TABLE `product_stock_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `sku_id` bigint NOT NULL COMMENT 'SKU ID',
    `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
    `type` tinyint NOT NULL COMMENT '类型：1-入库，2-出库，3-锁定，4-解锁',
    `quantity` int NOT NULL COMMENT '数量',
    `before_stock` int NOT NULL COMMENT '变动前库存',
    `after_stock` int NOT NULL COMMENT '变动后库存',
    `source` varchar(64) NOT NULL COMMENT '来源',
    `source_id` varchar(64) NOT NULL COMMENT '来源ID',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `operator` varchar(64) NOT NULL COMMENT '操作人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_sku_id` (`sku_id`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_type` (`type`),
    KEY `idx_source` (`source`, `source_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品库存记录表';
```

## 3. 索引设计

### 3.1 商品表索引
1. 主键索引：id
2. 普通索引：category_id, brand_id, status, sort, created_at

### 3.2 商品SKU表索引
1. 主键索引：id
2. 唯一索引：sku_code
3. 普通索引：product_id, status

### 3.3 商品分类表索引
1. 主键索引：id
2. 普通索引：parent_id, level, sort, status

### 3.4 商品品牌表索引
1. 主键索引：id
2. 唯一索引：name
3. 普通索引：sort, status

### 3.5 商品规格表索引
1. 主键索引：id
2. 普通索引：category_id, sort, status

### 3.6 商品规格值表索引
1. 主键索引：id
2. 普通索引：spec_id, sort, status

### 3.7 商品图片表索引
1. 主键索引：id
2. 普通索引：product_id, sku_id, sort, type

### 3.8 商品评价表索引
1. 主键索引：id
2. 普通索引：product_id, sku_id, user_id, order_id, rating, status, created_at

### 3.9 商品库存表索引
1. 主键索引：id
2. 唯一索引：(sku_id, warehouse_id)
3. 普通索引：warehouse_id

### 3.10 商品库存记录表索引
1. 主键索引：id
2. 普通索引：sku_id, warehouse_id, type, (source, source_id), created_at

## 4. 字段说明

### 4.1 通用字段
- id：主键，bigint，自增
- created_at：创建时间，datetime
- updated_at：更新时间，datetime
- deleted_at：删除时间，datetime
- status：状态，tinyint
- sort：排序，int

### 4.2 商品表字段
- name：商品名称，varchar(128)
- category_id：分类ID，bigint
- brand_id：品牌ID，bigint
- price：销售价，decimal(10,2)
- original_price：原价，decimal(10,2)
- description：商品描述，text
- detail：商品详情，text
- main_image：主图，varchar(255)
- sales：销量，int
- stock：库存，int

### 4.3 商品SKU表字段
- product_id：商品ID，bigint
- sku_code：SKU编码，varchar(64)
- specs：规格JSON，json
- price：销售价，decimal(10,2)
- original_price：原价，decimal(10,2)
- stock：库存，int
- image：SKU图片，varchar(255)

### 4.4 商品分类表字段
- parent_id：父分类ID，bigint
- name：分类名称，varchar(64)
- level：层级，int
- icon：图标，varchar(255)
- image：图片，varchar(255)

### 4.5 商品品牌表字段
- name：品牌名称，varchar(64)
- logo：品牌logo，varchar(255)
- description：品牌描述，varchar(255)

### 4.6 商品规格表字段
- name：规格名称，varchar(64)
- category_id：分类ID，bigint

### 4.7 商品规格值表字段
- spec_id：规格ID，bigint
- value：规格值，varchar(64)

### 4.8 商品图片表字段
- product_id：商品ID，bigint
- sku_id：SKU ID，bigint
- url：图片URL，varchar(255)
- type：类型，tinyint

### 4.9 商品评价表字段
- product_id：商品ID，bigint
- sku_id：SKU ID，bigint
- user_id：用户ID，bigint
- order_id：订单ID，bigint
- rating：评分，tinyint
- content：评价内容，text
- images：评价图片，json
- reply：商家回复，text
- reply_time：回复时间，datetime

### 4.10 商品库存表字段
- sku_id：SKU ID，bigint
- warehouse_id：仓库ID，bigint
- stock：库存数量，int
- locked_stock：锁定库存，int

### 4.11 商品库存记录表字段
- sku_id：SKU ID，bigint
- warehouse_id：仓库ID，bigint
- type：类型，tinyint
- quantity：数量，int
- before_stock：变动前库存，int
- after_stock：变动后库存，int
- source：来源，varchar(64)
- source_id：来源ID，varchar(64)
- remark：备注，varchar(255)
- operator：操作人，varchar(64)

## 5. 更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-xx | v1.0.0 | 初始版本 | - | 