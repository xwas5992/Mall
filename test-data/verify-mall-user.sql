-- mall_user 数据库验证脚本
-- 执行此脚本可以验证 mall_user 数据库的数据是否正确导入

USE mall_user;

-- 1. 检查数据库是否存在
SELECT '=== mall_user 数据库验证 ===' as info;

-- 2. 检查表结构
SHOW TABLES;

-- 3. 检查 users 表结构
DESCRIBE users;

-- 4. 检查用户数据
SELECT '=== 用户数据验证 ===' as info;
SELECT COUNT(*) as user_count FROM users;
SELECT id, username, created_at, updated_at FROM users ORDER BY id;

-- 5. 检查用户名唯一性
SELECT '=== 用户名唯一性检查 ===' as info;
SELECT username, COUNT(*) as count 
FROM users 
GROUP BY username 
HAVING COUNT(*) > 1;

-- 6. 检查时间戳
SELECT '=== 时间戳检查 ===' as info;
SELECT 
    MIN(created_at) as earliest_created,
    MAX(created_at) as latest_created,
    MIN(updated_at) as earliest_updated,
    MAX(updated_at) as latest_updated
FROM users;

-- 7. 总结报告
SELECT '=== 验证完成 ===' as info;
SELECT 
    'users表记录数' as table_name,
    (SELECT COUNT(*) FROM users) as record_count; 