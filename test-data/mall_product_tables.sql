-- mall_product 数据库表结构
-- 基于 product-service 的 Product 实体类

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mall_product CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE mall_product;

-- 创建商品表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    description VARCHAR(1000) DEFAULT NULL COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    stock INT NOT NULL COMMENT '库存数量',
    brand VARCHAR(100) DEFAULT NULL COMMENT '品牌',
    category VARCHAR(100) DEFAULT NULL COMMENT '分类',
    image_url VARCHAR(500) DEFAULT NULL COMMENT '商品图片URL',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '商品状态：0-下架，1-上架',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_category (category),
    KEY idx_brand (brand),
    KEY idx_status (status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 说明：
-- 1. 表名使用 product（与Product实体类的@Table注解一致）
-- 2. 字段与Product实体类的属性完全对应
-- 3. 价格使用DECIMAL(10,2)确保精度
-- 4. 状态字段使用TINYINT，1表示上架，0表示下架
-- 5. 添加了常用索引以提高查询性能
-- 6. 使用utf8mb4字符集支持emoji等特殊字符 