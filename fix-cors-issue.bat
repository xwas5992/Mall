@echo off
echo ========================================
echo    CORS Issue Resolution Script
echo ========================================
echo.

echo [1/4] Checking current backend status...
netstat -ano | findstr :8082
if %errorlevel% equ 0 (
    echo [INFO] Backend is running on port 8082
    echo [ACTION] You need to restart the backend to apply CORS changes
    echo.
    echo Please stop the current backend service (Ctrl+C in the backend terminal)
    echo Then restart it with: mvn spring-boot:run
    echo.
) else (
    echo [INFO] Backend is not running
)

echo [2/4] CORS Configuration Updated
echo - Modified CorsConfig.java to allow all origins
echo - Added support for file:// protocol
echo - Set credentials to 'omit' for file access

echo.
echo [3/4] Available Solutions:
echo.
echo Option 1: Restart Backend (Recommended)
echo   1. Stop current backend (Ctrl+C)
echo   2. Restart: mvn spring-boot:run
echo   3. Test: .\start-frontend-test.bat
echo.
echo Option 2: Use HTTP Server
echo   1. Run: .\start-http-server.bat
echo   2. Access: http://localhost:8080/test-api.html
echo.
echo Option 3: Use Live Server (VS Code)
echo   1. Install Live Server extension
echo   2. Right-click on frontend folder
echo   3. Select "Open with Live Server"

echo.
echo [4/4] Testing Instructions:
echo 1. After applying one of the solutions above
echo 2. Open browser console (F12)
echo 3. Check for CORS errors
echo 4. If successful, you should see API data

echo.
echo Press any key to continue...
pause >nul 