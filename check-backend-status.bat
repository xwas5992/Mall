@echo off
echo Checking Backend Service Status...
echo.

echo 1. Checking if product-service is running...
netstat -ano | findstr :8082
if %errorlevel% equ 0 (
    echo [OK] Product service is running on port 8082
) else (
    echo [ERROR] Product service is not running on port 8082
)

echo.
echo 2. Checking MySQL service...
sc query MySQL | findstr "STATE"
if %errorlevel% equ 0 (
    echo [OK] MySQL service is running
) else (
    echo [ERROR] MySQL service is not running
)

echo.
echo 3. Checking MySQL port...
netstat -ano | findstr :3306
if %errorlevel% equ 0 (
    echo [OK] MySQL is listening on port 3306
) else (
    echo [ERROR] MySQL is not listening on port 3306
    echo This might be the cause of the connection issue
)

echo.
echo 4. Testing API endpoint with PowerShell...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8082/product-service/api/products?page=0&size=5' -Method GET -TimeoutSec 10; Write-Host '[OK] API Response Status:' $response.StatusCode; if ($response.Content.Length -gt 0) { Write-Host '[OK] API returned data' } else { Write-Host '[WARNING] API returned empty response' } } catch { Write-Host '[ERROR] API test failed:' $_.Exception.Message; if ($_.Exception.Response) { Write-Host 'HTTP Status:' $_.Exception.Response.StatusCode } }"

echo.
echo 5. Common solutions for ERR_NAME_NOT_RESOLVED:
echo    - Check if backend service is running
echo    - Check if database is accessible
echo    - Check firewall settings
echo    - Try using 127.0.0.1 instead of localhost
echo    - Check if the service is bound to the correct interface

echo.
echo Test completed. Press any key to exit...
pause >nul 