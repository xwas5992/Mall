-- mall_user 数据库测试数据
-- 表: users

-- 清空现有数据（可选）
-- DELETE FROM users;

-- 插入测试用户数据
-- 注意：密码都是 '123456' 的BCrypt加密值
INSERT INTO users (username, password, created_at, updated_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', NOW(), NOW()),
('user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', NOW(), NOW()),
('user002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', NOW(), NOW()),
('user003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', NOW(), NOW()),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', NOW(), NOW()),
('zhangwei', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', NOW(), NOW()),
('lixiaomei', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', NOW(), NOW()),
('wangdali', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', NOW(), NOW());

-- 说明：
-- 1. 密码都是加密后的 '123456' (使用BCrypt加密)
-- 2. 创建了8个测试用户
-- 3. 时间戳使用NOW()函数自动生成
-- 4. 表名使用 users（与User实体类一致） 