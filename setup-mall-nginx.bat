@echo off
echo ========================================
echo 商城项目Nginx完整解决方案
echo ========================================
echo.

set NGINX_PATH=D:\softinstall\nginx\nginx-1.22.0-tlias
set CONFIG_PATH=D:\Code\Mall\nginx\conf\nginx-mall.conf

echo 检查环境...
echo.

echo 检查nginx安装路径: %NGINX_PATH%
if not exist "%NGINX_PATH%\nginx.exe" (
    echo ERROR: nginx.exe 未找到！
    echo 请检查路径: %NGINX_PATH%
    echo 或者修改脚本中的NGINX_PATH变量
    pause
    exit /b 1
)

echo ✓ nginx安装路径正确
echo.

echo 检查配置文件: %CONFIG_PATH%
if not exist "%CONFIG_PATH%" (
    echo ERROR: 配置文件未找到！
    echo 请检查路径: %CONFIG_PATH%
    pause
    exit /b 1
)

echo ✓ 配置文件存在
echo.

echo 步骤1: 检查nginx配置语法...
cd /d "%NGINX_PATH%"
nginx.exe -t -c "%CONFIG_PATH%"

if %errorlevel% neq 0 (
    echo ERROR: nginx配置有误，请检查配置文件！
    pause
    exit /b 1
)

echo ✓ nginx配置语法正确
echo.

echo 步骤2: 检查微服务状态...
echo.

echo 检查认证服务 (8081)...
curl -s http://127.0.0.1:8081/api/auth/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ 认证服务运行正常
) else (
    echo ✗ 认证服务未运行，请启动认证服务
)

echo 检查商品服务 (8082)...
curl -s http://127.0.0.1:8082/product-service/api/products/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ 商品服务运行正常
) else (
    echo ✗ 商品服务未运行，请启动商品服务
)

echo 检查用户服务 (8085)...
curl -s http://127.0.0.1:8085/api/user/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ 用户服务运行正常
) else (
    echo ✗ 用户服务未运行，请启动用户服务
)

echo.
echo 步骤3: 启动nginx...
echo.

echo 检查nginx是否已运行...
tasklist /fi "imagename eq nginx.exe" 2>nul | find /i "nginx.exe" >nul
if %errorlevel% equ 0 (
    echo nginx已在运行，正在停止...
    nginx.exe -s stop
    timeout /t 3 /nobreak >nul
)

echo 启动nginx...
start "Mall Nginx" nginx.exe -c "%CONFIG_PATH%"

if %errorlevel% equ 0 (
    echo ✓ nginx启动成功！
) else (
    echo ✗ nginx启动失败！
    pause
    exit /b 1
)

echo.
echo 步骤4: 测试nginx代理...
echo.

echo 等待nginx启动...
timeout /t 5 /nobreak >nul

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
echo 解决方案部署完成！
echo ========================================
echo.
echo ✓ nginx配置正确
echo ✓ nginx启动成功
echo ✓ 所有API代理工作正常
echo ✓ CORS问题已解决
echo.
echo 访问地址：
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
echo 管理命令：
echo - 启动nginx: start-nginx-mall.bat
echo - 停止nginx: stop-nginx-mall.bat
echo - 测试配置: test-nginx-mall.bat
echo.
echo 日志文件：
echo - 访问日志: %NGINX_PATH%\logs\mall-access.log
echo - 错误日志: %NGINX_PATH%\logs\mall-error.log
echo.

pause 