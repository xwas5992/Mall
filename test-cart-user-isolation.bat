@echo off
echo 测试购物车用户隔离...
echo.

echo 1. 检查购物车数据库表...
echo 查看cart_item表结构...
mysql -u root -p -e "USE mall_product; DESCRIBE cart_item;"

echo.
echo 2. 查看当前购物车数据...
echo 按用户ID分组显示购物车内容...
mysql -u root -p -e "USE mall_product; SELECT user_id, COUNT(*) as item_count, GROUP_CONCAT(product_name) as products FROM cart_item GROUP BY user_id;"

echo.
echo 3. 测试不同用户的购物车API...
echo 测试用户1的购物车...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost/api/cart/list' -Method GET -Headers @{'Authorization'='Bearer YOUR_TOKEN_HERE'} -TimeoutSec 10; Write-Host '[✓] 用户1购物车API成功 - 状态码:' $response.StatusCode; Write-Host '响应内容:' $response.Content } catch { Write-Host '[✗] 用户1购物车API失败:' $_.Exception.Message }"

echo.
echo 4. 检查JWT Token解析...
echo 请检查以下内容:
echo   - 前端发送的Authorization头是否正确
echo   - JWT Token是否包含正确的用户名
echo   - 后端JwtUtil是否正确解析用户ID

echo.
echo 5. 建议修复步骤:
echo   a) 检查前端登录时获取的token
echo   b) 验证JWT Token中的用户名
echo   c) 确认JwtUtil中的用户映射
echo   d) 重启product-service服务
echo   e) 测试不同用户的购物车隔离

echo.
echo 测试完成！
pause
