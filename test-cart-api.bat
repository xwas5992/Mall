@echo off
echo Testing Cart API...

echo.
echo 1. Testing GET /api/cart with token...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/api/cart' -Headers @{'Authorization'='Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTczNDU5NzI5MCwiZXhwIjoxNzM0NjgzNjkwfQ.Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8'} -Method GET; Write-Host 'Status:' $response.StatusCode; Write-Host 'Response:' $response.Content } catch { Write-Host 'Error:' $_.Exception.Message }"

echo.
echo 2. Testing POST /api/cart/add with token...
powershell -Command "try { $body = @{productId=1;quantity=1} | ConvertTo-Json; $response = Invoke-WebRequest -Uri 'http://localhost:8081/api/cart/add' -Headers @{'Authorization'='Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTczNDU5NzI5MCwiZXhwIjoxNzM0NjgzNjkwfQ.Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8';'Content-Type'='application/json'} -Method POST -Body $body; Write-Host 'Status:' $response.StatusCode; Write-Host 'Response:' $response.Content } catch { Write-Host 'Error:' $_.Exception.Message }"

echo.
echo 3. Testing GET /api/cart again to see if item was added...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/api/cart' -Headers @{'Authorization'='Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTczNDU5NzI5MCwiZXhwIjoxNzM0NjgzNjkwfQ.Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8Ej8'} -Method GET; Write-Host 'Status:' $response.StatusCode; Write-Host 'Response:' $response.Content } catch { Write-Host 'Error:' $_.Exception.Message }"

pause 