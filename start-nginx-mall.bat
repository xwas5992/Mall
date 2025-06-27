@echo off
echo ========================================
echo 启动商城Nginx反向代理
echo ========================================
echo.

set NGINX_PATH=D:\softinstall\nginx\nginx-1.22.0-tlias
set CONFIG_PATH=D:\Code\Mall\nginx\conf\nginx-mall.conf

echo 检查nginx路径: %NGINX_PATH%
if not exist "%NGINX_PATH%\nginx.exe" (
    echo ERROR: nginx.exe 未找到！
    echo 请检查路径: %NGINX_PATH%
    pause
    exit /b 1
)

echo 检查配置文件: %CONFIG_PATH%
if not exist "%CONFIG_PATH%" (
    echo ERROR: 配置文件未找到！
    echo 请检查路径: %CONFIG_PATH%
    pause
    exit /b 1
)

echo.
echo 检查nginx是否已运行...
tasklist /fi "imagename eq nginx.exe" 2>nul | find /i "nginx.exe" >nul
if %errorlevel% equ 0 (
    echo nginx已在运行，正在停止...
    "%NGINX_PATH%\nginx.exe" -s stop
    timeout /t 3 /nobreak >nul
)

echo.
echo 启动nginx...
cd /d "%NGINX_PATH%"
start "Mall Nginx" nginx.exe -c "%CONFIG_PATH%"

if %errorlevel% equ 0 (
    echo.
    echo ✓ nginx启动成功！
    echo.
    echo 服务地址：
    echo - 前端商城: http://localhost
    echo - 管理员后台: http://localhost/admin
    echo - 健康检查: http://localhost/health
    echo.
    echo API代理：
    echo - 认证服务: http://localhost/api/auth/
    echo - 商品服务: http://localhost/api/products/
    echo - 首页商品: http://localhost/api/homepage/
    echo - 用户服务: http://localhost/api/user/
    echo - 购物车: http://localhost/api/cart/
    echo.
    echo 日志文件：
    echo - 访问日志: %NGINX_PATH%\logs\mall-access.log
    echo - 错误日志: %NGINX_PATH%\logs\mall-error.log
    echo.
) else (
    echo.
    echo ✗ nginx启动失败！
    echo 请检查：
    echo 1. nginx路径是否正确
    echo 2. 配置文件是否正确
    echo 3. 端口80是否被占用
    echo.
)

pause 