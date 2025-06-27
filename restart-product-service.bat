@echo off
echo Restarting Product Service...
echo.

echo 1. Stopping existing service...
taskkill /F /IM java.exe /FI "WINDOWTITLE eq product-service*" >nul 2>&1
timeout /t 2 /nobreak >nul

echo 2. Starting product service...
cd /d "D:\Code\Mall\product-service"
start "Product Service" cmd /k "mvn spring-boot:run"

echo.
echo Product service is restarting...
echo Please wait for the service to fully start (about 30 seconds)
echo.
echo You can check the status by running: diagnose-connection.bat
echo.
pause 