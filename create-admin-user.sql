-- 创建管理员用户的SQL脚本
-- 使用正确的BCrypt加密密码

-- 清空现有管理员用户（可选）
-- DELETE FROM auth_user WHERE username = 'admin';

-- 插入管理员用户
-- 密码: 123456
-- BCrypt加密值: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa
INSERT INTO auth_user (username, password, email, full_name, role, enabled, created_at, updated_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@mall.com', '系统管理员', 'ADMIN', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa',
    role = 'ADMIN',
    enabled = 1,
    updated_at = NOW();

-- 验证插入结果
SELECT username, email, full_name, role, enabled, created_at FROM auth_user WHERE username = 'admin';

-- 说明：
-- 1. 用户名: admin
-- 2. 密码: 123456 (明文)
-- 3. 角色: ADMIN
-- 4. 状态: 启用 (enabled = 1)
-- 5. 使用 ON DUPLICATE KEY UPDATE 避免重复插入错误 