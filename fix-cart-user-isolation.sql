-- 修复购物车用户隔离问题
USE mall_product;

-- 1. 查看当前购物车数据状态
SELECT '当前购物车数据状态:' as info;
SELECT user_id, COUNT(*) as item_count, GROUP_CONCAT(product_name) as products 
FROM cart_item 
GROUP BY user_id;

-- 2. 检查是否有重复或错误的用户ID
SELECT '检查用户ID分布:' as info;
SELECT user_id, COUNT(*) as item_count 
FROM cart_item 
GROUP BY user_id 
ORDER BY user_id;

-- 3. 清理测试数据（可选，谨慎使用）
-- DELETE FROM cart_item WHERE user_id NOT IN (1, 2, 3, 4, 5);

-- 4. 重新插入正确的测试数据
-- 先清空现有数据
TRUNCATE TABLE cart_item;

-- 插入用户1的购物车数据
INSERT INTO cart_item (user_id, product_id, product_name, product_image, product_price, quantity) VALUES
(1, 1, 'Xiaomi 14 Ultra', 'https://via.placeholder.com/200x200', 5999.00, 1),
(1, 2, 'Redmi K70', 'https://via.placeholder.com/200x200', 2499.00, 2);

-- 插入用户2的购物车数据
INSERT INTO cart_item (user_id, product_id, product_name, product_image, product_price, quantity) VALUES
(2, 3, 'MacBook Air', 'https://via.placeholder.com/200x200', 8999.00, 1),
(2, 4, 'iPhone 15 Pro', 'https://via.placeholder.com/200x200, 7999.00, 1);

-- 插入用户5的购物车数据
INSERT INTO cart_item (user_id, product_id, product_name, product_image, product_price, quantity) VALUES
(5, 1, 'Xiaomi 14 Ultra', 'https://via.placeholder.com/200x200', 5999.00, 1);

-- 5. 验证修复结果
SELECT '修复后的购物车数据:' as info;
SELECT user_id, COUNT(*) as item_count, GROUP_CONCAT(product_name) as products 
FROM cart_item 
GROUP BY user_id 
ORDER BY user_id;

-- 6. 检查唯一约束
SELECT '检查用户-商品唯一约束:' as info;
SELECT user_id, product_id, COUNT(*) as duplicate_count
FROM cart_item 
GROUP BY user_id, product_id 
HAVING COUNT(*) > 1;

-- 7. 创建索引确保性能
-- 如果索引不存在，创建它们
-- CREATE INDEX IF NOT EXISTS idx_user_id ON cart_item(user_id);
-- CREATE INDEX IF NOT EXISTS idx_product_id ON cart_item(product_id);
-- CREATE UNIQUE INDEX IF NOT EXISTS uk_user_product ON cart_item(user_id, product_id);

SELECT '购物车用户隔离修复完成！' as result;
