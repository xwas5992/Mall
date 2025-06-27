@echo off
echo Testing Carousel Fix...
echo.

echo Starting HTTP server for testing...
cd /d "%~dp0frontend"
python -m http.server 8080

echo.
echo Carousel fix completed!
echo Please open http://localhost:8080/home/index.html in your browser
echo Press Ctrl+C to stop the server
pause 