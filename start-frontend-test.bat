@echo off
echo Starting Frontend API Test...
echo.
echo Please make sure your backend services are running:
echo - Product Service: http://localhost:8082
echo - Auth Service: http://localhost:8081
echo.
echo Opening test page in browser...
start "" "frontend/test-api.html"
echo.
echo Test page opened. Check the browser console for detailed API connection logs.
echo.
pause 