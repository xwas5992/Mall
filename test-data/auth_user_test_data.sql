-- mall_auth 数据库测试数据
-- 表: auth_user

-- 清空现有数据（可选）
-- DELETE FROM auth_user;

-- 插入测试用户数据
-- 注意：密码都是 '123456' 的BCrypt加密值
INSERT INTO auth_user (username, password, full_name, role, enabled, created_at, updated_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '系统管理员', 'ADMIN', 1, NOW(), NOW()),
('user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '张三', 'USER', 1, NOW(), NOW()),
('user002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '李四', 'USER', 1, NOW(), NOW()),
('user003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '王五', 'USER', 1, NOW(), NOW()),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '测试用户', 'USER', 1, NOW(), NOW()),
('zhangwei', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '张伟', 'USER', 1, NOW(), NOW()),
('lixiaomei', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '李小美', 'USER', 1, NOW(), NOW()),
('wangdali', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '王大立', 'USER', 1, NOW(), NOW()),
('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '店铺经理', 'ADMIN', 1, NOW(), NOW()),
('vipuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'VIP用户', 'USER', 1, NOW(), NOW());

-- 说明：
-- 1. 密码都是加密后的 '123456' (使用BCrypt加密)
-- 2. 创建了10个测试用户，包含2个管理员和8个普通用户
-- 3. 所有用户都是启用状态 (enabled = 1)
-- 4. 时间戳使用NOW()函数自动生成
-- 5. 表名使用 auth_user（与User实体类一致）
-- 6. 用户名是唯一的
-- 7. 角色分为USER和ADMIN两种 