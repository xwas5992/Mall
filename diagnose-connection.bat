@echo off
echo ========================================
echo    Frontend-Backend Connection Diagnosis
echo ========================================
echo.

echo [1/6] Checking localhost connectivity...
ping -n 1 localhost >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] localhost is reachable
) else (
    echo [ERROR] localhost is not reachable
)

echo.
echo [2/6] Checking 127.0.0.1 connectivity...
ping -n 1 127.0.0.1 >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] 127.0.0.1 is reachable
) else (
    echo [ERROR] 127.0.0.1 is not reachable
)

echo.
echo [3/6] Checking product-service port...
netstat -ano | findstr :8082
if %errorlevel% equ 0 (
    echo [OK] Port 8082 is listening
) else (
    echo [ERROR] Port 8082 is not listening
)

echo.
echo [4/6] Checking MySQL service and port...
sc query MySQL | findstr "STATE"
if %errorlevel% equ 0 (
    echo [OK] MySQL service is running
) else (
    echo [ERROR] MySQL service is not running
)

netstat -ano | findstr :3306
if %errorlevel% equ 0 (
    echo [OK] MySQL port 3306 is listening
) else (
    echo [WARNING] MySQL port 3306 is not listening
    echo This might cause database connection issues
)

echo.
echo [5/6] Testing API endpoints...
echo Testing localhost:8082...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8082/product-service/api/products?page=0&size=5' -Method GET -TimeoutSec 5; Write-Host '[OK] localhost API Status:' $response.StatusCode } catch { Write-Host '[ERROR] localhost API failed:' $_.Exception.Message }"

echo Testing 127.0.0.1:8082...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://127.0.0.1:8082/product-service/api/products?page=0&size=5' -Method GET -TimeoutSec 5; Write-Host '[OK] 127.0.0.1 API Status:' $response.StatusCode } catch { Write-Host '[ERROR] 127.0.0.1 API failed:' $_.Exception.Message }"

echo.
echo [6/6] Checking hosts file...
findstr /C:"localhost" C:\Windows\System32\drivers\etc\hosts >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] localhost entry found in hosts file
) else (
    echo [WARNING] localhost entry not found in hosts file
)

echo.
echo ========================================
echo           DIAGNOSIS SUMMARY
echo ========================================
echo.
echo If you see ERR_NAME_NOT_RESOLVED errors:
echo.
echo 1. Try using 127.0.0.1 instead of localhost
echo 2. Check if your backend service is running
echo 3. Check if MySQL is properly configured
echo 4. Check firewall settings
echo 5. Try restarting the backend service
echo.
echo To test the fix:
echo 1. Run: start-frontend-test.bat
echo 2. Open browser console (F12)
echo 3. Check for any error messages
echo.
echo Press any key to exit...
pause >nul 