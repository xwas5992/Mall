@echo off
echo ========================================
echo mall_auth 数据库导入脚本 (Windows)
echo ========================================

REM 设置MySQL连接参数
set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PASSWORD=123456

echo.
echo 正在创建 mall_auth 数据库和表结构...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% < auth_user_tables.sql
if %errorlevel% neq 0 (
    echo 错误：创建数据库和表结构失败！
    pause
    exit /b 1
)

echo.
echo 正在导入 auth_user 测试数据...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% mall_auth < auth_user_test_data.sql
if %errorlevel% neq 0 (
    echo 错误：导入测试数据失败！
    pause
    exit /b 1
)

echo.
echo ========================================
echo mall_auth 数据库导入完成！
echo ========================================
echo.
echo 导入的内容包括：
echo 1. mall_auth 数据库
echo 2. auth_user 表结构
echo 3. 10个测试用户数据
echo.
echo 测试账号：
echo 管理员账号：
echo - admin / 123456 (系统管理员)
echo - manager / 123456 (店铺经理)
echo.
echo 普通用户账号：
echo - user001 / 123456 (张三)
echo - user002 / 123456 (李四)
echo - user003 / 123456 (王五)
echo - testuser / 123456 (测试用户)
echo - zhangwei / 123456 (张伟)
echo - lixiaomei / 123456 (李小美)
echo - wangdali / 123456 (王大立)
echo - vipuser / 123456 (VIP用户)
echo.
echo 验证命令：
echo mysql -uroot -p123456 -e "USE mall_auth; SELECT COUNT(*) as user_count FROM auth_user;"
echo.
pause 