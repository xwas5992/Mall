@echo off
echo ========================================
echo 停止Nginx反向代理服务器
echo ========================================
echo.

echo 正在停止nginx...
taskkill /f /im nginx.exe 2>nul

if %errorlevel% equ 0 (
    echo ✓ nginx已停止！
) else (
    echo nginx未运行或已停止。
)

echo.
pause 