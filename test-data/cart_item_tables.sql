-- 购物车表
USE mall_product;

-- 删除已存在的表
DROP TABLE IF EXISTS cart_item;

-- 创建购物车表
CREATE TABLE cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(255) NOT NULL COMMENT '商品名称',
    product_image VARCHAR(500) COMMENT '商品图片',
    product_price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    UNIQUE KEY uk_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 插入测试数据
INSERT INTO cart_item (user_id, product_id, product_name, product_image, product_price, quantity) VALUES
(1, 1, 'Xiaomi 14 Ultra', 'https://via.placeholder.com/200x200', 5999.00, 1),
(1, 2, 'Redmi K70', 'https://via.placeholder.com/200x200', 2499.00, 2),
(5, 1, 'Xiaomi 14 Ultra', 'https://via.placeholder.com/200x200', 5999.00, 1);

-- 查看表结构
DESCRIBE cart_item;

-- 查看测试数据
SELECT * FROM cart_item; 