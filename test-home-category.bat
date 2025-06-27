@echo off
echo Testing Home Page Category Display Functionality...
echo.

echo Starting HTTP server for frontend testing...
cd /d "%~dp0frontend"
python -m http.server 8080

echo.
echo Home page should be available at: http://localhost:8080/home/index.html
echo.
echo Testing category display functionality:
echo 1. Check if category sections are loaded
echo 2. Verify API calls to product service
echo 3. Test fallback to mock data if API fails
echo.
echo Press any key to stop the server...
pause > nul

taskkill /f /im python.exe 2>nul
echo Server stopped. 