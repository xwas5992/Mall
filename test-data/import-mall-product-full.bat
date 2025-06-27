@echo off
echo ========================================
echo mall_product 数据库完整导入脚本 (Windows)
echo ========================================
echo.
echo **警告：此脚本将清空并重建 mall_product 数据库！**
echo.
pause

REM 设置MySQL连接参数
set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PASSWORD=123456

echo.
echo 正在删除旧的 mall_product 数据库（如果存在）...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% -e "DROP DATABASE IF EXISTS mall_product;"

echo.
echo 正在创建 mall_product 数据库和表结构...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% < mall_product_schema_full.sql
if %errorlevel% neq 0 (
    echo 错误：创建数据库和表结构失败！
    pause
    exit /b 1
)

echo.
echo 正在导入 mall_product 测试数据...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% mall_product < mall_product_data_full.sql
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
echo 2. product, product_brand, product_category 表结构
echo 3. 8个分类, 8个品牌, 16个商品
echo.
echo 验证命令：
echo mysql -uroot -p123456 -e "USE mall_product; SELECT COUNT(*) as category_count FROM product_category; SELECT COUNT(*) as brand_count FROM product_brand; SELECT COUNT(*) as product_count FROM product;"
echo.
pause 