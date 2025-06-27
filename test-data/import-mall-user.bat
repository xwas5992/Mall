@echo off
echo ========================================
echo mall_user 数据库导入脚本 (Windows)
echo ========================================

REM 设置MySQL连接参数
set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PASSWORD=123456

echo.
echo 正在创建 mall_user 数据库和表结构...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% < mall_user_tables.sql
if %errorlevel% neq 0 (
    echo 错误：创建数据库和表结构失败！
    pause
    exit /b 1
)

echo.
echo 正在导入 mall_user 测试数据...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% mall_user < mall_user_test_data.sql
if %errorlevel% neq 0 (
    echo 错误：导入测试数据失败！
    pause
    exit /b 1
)

echo.
echo ========================================
echo mall_user 数据库导入完成！
echo ========================================
echo.
echo 导入的内容包括：
echo 1. mall_user 数据库
echo 2. users 表结构
echo 3. 8个测试用户数据
echo.
echo 测试账号：
echo 用户名: admin, user001, user002, user003, testuser, zhangwei, lixiaomei, wangdali
echo 密码: 123456
echo.
echo 验证命令：
echo mysql -uroot -p123456 -e "USE mall_user; SELECT COUNT(*) as user_count FROM users;"
echo.
pause 