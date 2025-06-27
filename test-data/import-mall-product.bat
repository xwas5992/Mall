@echo off
echo ========================================
echo mall_product 数据库导入脚本 (Windows)
echo ========================================

REM 设置MySQL连接参数
set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PASSWORD=123456

echo.
echo 正在创建 mall_product 数据库和表结构...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% < mall_product_tables.sql
if %errorlevel% neq 0 (
    echo 错误：创建数据库和表结构失败！
    pause
    exit /b 1
)

echo.
echo 正在导入 mall_product 测试数据...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% mall_product < mall_product_test_data.sql
if %errorlevel% neq 0 (
    echo 错误：导入测试数据失败！
    pause
    exit /b 1
)

echo.
echo ========================================
echo mall_product 数据库导入完成！
echo ========================================
echo.
echo 导入的内容包括：
echo 1. mall_product 数据库
echo 2. product 表结构
echo 3. 32个测试商品数据
echo.
echo 商品分类：
echo - 新鲜水果 (4个商品)
echo - 有机蔬菜 (4个商品)
echo - 海鲜水产 (4个商品)
echo - 肉禽蛋品 (4个商品)
echo - 粮油调味 (4个商品)
echo - 乳品饮料 (4个商品)
echo - 休闲零食 (4个商品)
echo - 酒水茶饮 (4个商品)
echo.
echo 价格范围：8.90 - 1499.00元
echo 库存范围：20 - 500件
echo.
echo 验证命令：
echo mysql -uroot -p123456 -e "USE mall_product; SELECT COUNT(*) as product_count FROM product;"
echo.
pause 