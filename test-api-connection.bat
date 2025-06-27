@echo off
echo Testing API Connection...
echo.

echo 1. Testing localhost connectivity...
ping -n 1 localhost >nul
if %errorlevel% equ 0 (
    echo [OK] localhost is reachable
) else (
    echo [ERROR] localhost is not reachable
)

echo.
echo 2. Testing port 8082...
netstat -an | findstr :8082 >nul
if %errorlevel% equ 0 (
    echo [OK] Port 8082 is listening
) else (
    echo [ERROR] Port 8082 is not listening
)

echo.
echo 3. Testing API endpoint...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8082/product-service/api/products?page=0&size=5' -Method GET -TimeoutSec 10; Write-Host '[OK] API is responding. Status:' $response.StatusCode; Write-Host 'Response length:' $response.Content.Length } catch { Write-Host '[ERROR] API test failed:' $_.Exception.Message }"

echo.
echo 4. Testing database connection...
echo Please check if MySQL is running on localhost:3306
echo Database: mall_product
echo Username: root
echo Password: 123456

echo.
echo Test completed. Press any key to exit...
pause >nul 