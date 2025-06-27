-- mall_product 数据库验证脚本
-- 执行此脚本可以验证 mall_product 数据库的数据是否正确导入

USE mall_product;

-- 1. 检查数据库是否存在
SELECT '=== mall_product 数据库验证 ===' as info;

-- 2. 检查表结构
SHOW TABLES;

-- 3. 检查 product 表结构
DESCRIBE product;

-- 4. 检查商品数据
SELECT '=== 商品数据验证 ===' as info;
SELECT COUNT(*) as product_count FROM product;
SELECT id, name, price, stock, brand, category, status FROM product ORDER BY id LIMIT 10;

-- 5. 按分类统计商品数量
SELECT '=== 分类统计 ===' as info;
SELECT 
    category,
    COUNT(*) as product_count,
    AVG(price) as avg_price,
    MIN(price) as min_price,
    MAX(price) as max_price,
    SUM(stock) as total_stock
FROM product 
GROUP BY category 
ORDER BY product_count DESC;

-- 6. 按品牌统计商品数量
SELECT '=== 品牌统计 ===' as info;
SELECT 
    brand,
    COUNT(*) as product_count,
    AVG(price) as avg_price
FROM product 
GROUP BY brand 
ORDER BY product_count DESC;

-- 7. 检查价格范围
SELECT '=== 价格范围检查 ===' as info;
SELECT 
    MIN(price) as min_price,
    MAX(price) as max_price,
    AVG(price) as avg_price
FROM product;

-- 8. 检查库存状态
SELECT '=== 库存状态检查 ===' as info;
SELECT 
    MIN(stock) as min_stock,
    MAX(stock) as max_stock,
    AVG(stock) as avg_stock,
    SUM(stock) as total_stock
FROM product;

-- 9. 检查商品状态
SELECT '=== 商品状态检查 ===' as info;
SELECT 
    status,
    COUNT(*) as count
FROM product 
GROUP BY status;

-- 10. 检查数据完整性
SELECT '=== 数据完整性检查 ===' as info;

-- 检查是否有空名称的商品
SELECT COUNT(*) as null_name_count FROM product WHERE name IS NULL OR name = '';

-- 检查是否有无效价格的商品
SELECT COUNT(*) as invalid_price_count FROM product WHERE price <= 0;

-- 检查是否有无效库存的商品
SELECT COUNT(*) as invalid_stock_count FROM product WHERE stock < 0;

-- 11. 总结报告
SELECT '=== 验证完成 ===' as info;
SELECT 
    'product表记录数' as table_name,
    (SELECT COUNT(*) FROM product) as record_count; 