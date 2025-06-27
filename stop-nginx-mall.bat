@echo off
echo ========================================
echo 停止商城Nginx反向代理
echo ========================================
echo.

set NGINX_PATH=D:\softinstall\nginx\nginx-1.22.0-tlias

echo 检查nginx路径: %NGINX_PATH%
if not exist "%NGINX_PATH%\nginx.exe" (
    echo ERROR: nginx.exe 未找到！
    echo 请检查路径: %NGINX_PATH%
    pause
    exit /b 1
)

echo.
echo 正在停止nginx...
cd /d "%NGINX_PATH%"
nginx.exe -s stop

if %errorlevel% equ 0 (
    echo ✓ nginx已停止！
) else (
    echo nginx未运行或已停止。
)

echo.
pause 