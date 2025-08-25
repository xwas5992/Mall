@echo off
echo 正在测试User Service状态...
echo.

echo 1. 检查User Service健康状态:
curl -X GET "http://localhost:8085/api/user/health" -H "Content-Type: application/json"
echo.
echo.

echo 2. 检查User Service地址API:
curl -X GET "http://localhost:8085/api/user/address/1" -H "Content-Type: application/json"
echo.
echo.

echo 3. 检查CORS预检请求:
curl -X OPTIONS "http://localhost:8085/api/user/address/1" -H "Origin: http://localhost" -H "Access-Control-Request-Method: GET" -H "Access-Control-Request-Headers: Content-Type,Authorization" -v
echo.
echo.

echo 测试完成！
pause 