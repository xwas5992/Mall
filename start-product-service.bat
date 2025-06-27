@echo off
echo Starting Product Service...
echo.

echo Building project...
cd /d "%~dp0product-service"
call mvn clean compile

echo.
echo Starting Product Service...
call mvn spring-boot:run

echo.
echo Product Service started successfully!
echo Swagger UI: http://localhost:8082/product-service/swagger-ui.html
echo API Docs: http://localhost:8082/product-service/api-docs
echo.
pause 