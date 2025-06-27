@echo off
echo Starting Local HTTP Server...
echo.
echo This will serve the frontend files on http://localhost:8080
echo This avoids CORS issues when accessing files directly
echo.

cd /d "D:\Code\Mall\frontend"

echo Starting Python HTTP server...
python -m http.server 8080

echo.
echo If Python is not available, trying Node.js...
node -e "const http = require('http'); const fs = require('fs'); const path = require('path'); http.createServer((req, res) => { let filePath = '.' + req.url; if (filePath === './') filePath = './home/index.html'; fs.readFile(filePath, (err, data) => { if (err) { res.writeHead(404); res.end('File not found'); } else { res.writeHead(200); res.end(data); } }); }).listen(8080, () => console.log('Server running at http://localhost:8080/'));"

echo.
echo Local server started at http://localhost:8080
echo Now you can access the frontend without CORS issues
echo.
pause 