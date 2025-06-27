@echo off
echo ========================================
echo 测试商城Nginx配置
echo ========================================
echo.

set NGINX_PATH=D:\softinstall\nginx\nginx-1.22.0-tlias
set CONFIG_PATH=D:\Code\Mall\nginx\conf\nginx-mall.conf

echo 步骤1: 检查nginx配置...
cd /d "%NGINX_PATH%"
nginx.exe -t -c "%CONFIG_PATH%"

if %errorlevel% neq 0 (
    echo ERROR: nginx配置有误！
    pause
    exit /b 1
)

echo ✓ nginx配置正确！
echo.

echo 步骤2: 检查服务状态...
echo.

echo 检查认证服务 (8081)...
curl -s http://127.0.0.1:8081/api/auth/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ 认证服务运行正常
) else (
    echo ✗ 认证服务未运行
)

echo 检查商品服务 (8082)...
curl -s http://127.0.0.1:8082/product-service/api/products/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ 商品服务运行正常
) else (
    echo ✗ 商品服务未运行
)

echo 检查用户服务 (8085)...
curl -s http://127.0.0.1:8085/api/user/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ 用户服务运行正常
) else (
    echo ✗ 用户服务未运行
)

echo.
echo 步骤3: 测试nginx代理...
echo.

echo 测试nginx健康检查...
curl -s http://localhost/health

echo.
echo 测试认证服务代理...
curl -s http://localhost/api/auth/health

echo.
echo 测试商品服务代理...
curl -s http://localhost/api/products/health

echo.
echo 测试首页商品API代理...
curl -s http://localhost/api/homepage/products

echo.
echo 测试用户服务代理...
curl -s http://localhost/api/user/health

echo.
echo ========================================
echo 测试完成！
echo ========================================
echo.
echo 如果看到JSON响应，说明nginx代理工作正常。
echo.
echo 访问地址：
echo - 前端商城: http://localhost
echo - 管理员后台: http://localhost/admin
echo.

pause 