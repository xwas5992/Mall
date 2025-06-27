@echo off
echo Testing Cart Modal Function...

echo.
echo Starting HTTP server for frontend testing...
cd frontend
python -m http.server 8000

echo.
echo Frontend server started at http://localhost:8000
echo Please open http://localhost:8000/cart/cart.html in your browser
echo.
echo To test the modal:
echo 1. Add some items to cart from home page
echo 2. Go to cart page
echo 3. Click the delete button (X) on any item
echo 4. You should see a beautiful modal instead of browser confirm
echo.
pause 