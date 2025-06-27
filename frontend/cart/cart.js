document.addEventListener('DOMContentLoaded', async function() {
    const authManager = new AuthManager();
    // const cartManager = new CartManager(); // 移除本地cartManager

    if (!authManager.isLoggedIn()) {
        window.location.href = '../login/login.html?redirect=' + encodeURIComponent(window.location.pathname);
        return;
    }

    let cartItems = await window.CartAPI.getCart();

    const emptyCartView = document.getElementById('empty-cart-view');
    const cartItemsView = document.getElementById('cart-items-view');
    const cartItemsContainer = document.getElementById('cart-items-container');
    const summaryBar = document.getElementById('cart-summary-bar');
    const selectAllCheckbox = document.getElementById('select-all-checkbox');
    const selectedCountEl = document.getElementById('selected-count');
    const totalPriceEl = document.getElementById('total-price');

    // 弹窗相关元素
    const confirmModal = new bootstrap.Modal(document.getElementById('confirmModal'));
    const deleteProductNameEl = document.getElementById('deleteProductName');
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
    let currentDeleteProductId = null;

    // 初始化确认删除按钮事件
    confirmDeleteBtn.addEventListener('click', async () => {
        if (currentDeleteProductId !== null) {
            try {
                const result = await window.CartAPI.removeCart(currentDeleteProductId);
                if (result.success === false) {
                    showError('删除失败: ' + (result.message || '未知错误'));
                } else {
                    showSuccess('商品删除成功');
                    await renderCart();
                }
            } catch (error) {
                console.error('删除商品时出错:', error);
                showError('删除失败，请重试');
            }
            confirmModal.hide();
            currentDeleteProductId = null;
        }
    });

    // 显示成功消息
    function showSuccess(message) {
        // 创建成功提示
        const toast = document.createElement('div');
        toast.className = 'toast align-items-center text-white bg-success border-0 position-fixed';
        toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999;';
        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    <i class="fas fa-check-circle me-2"></i>${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        `;
        document.body.appendChild(toast);
        
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
        
        // 自动移除
        toast.addEventListener('hidden.bs.toast', () => {
            document.body.removeChild(toast);
        });
    }

    // 显示错误消息
    function showError(message) {
        // 创建错误提示
        const toast = document.createElement('div');
        toast.className = 'toast align-items-center text-white bg-danger border-0 position-fixed';
        toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999;';
        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    <i class="fas fa-exclamation-circle me-2"></i>${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        `;
        document.body.appendChild(toast);
        
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
        
        // 自动移除
        toast.addEventListener('hidden.bs.toast', () => {
            document.body.removeChild(toast);
        });
    }

    async function renderCart() {
        cartItems = await window.CartAPI.getCart(); // Always get the latest cart data

        if (!cartItems || cartItems.length === 0) {
            emptyCartView.style.display = 'block';
            cartItemsView.style.display = 'none';
            summaryBar.style.display = 'none';
        } else {
            emptyCartView.style.display = 'none';
            cartItemsView.style.display = 'block';
            summaryBar.style.display = 'block';
            cartItemsContainer.innerHTML = cartItems.map(item => `
                <div class="cart-item-row d-flex align-items-center p-3" data-id="${item.productId}">
                    <div class="form-check flex-shrink-0">
                        <input class="form-check-input item-checkbox" type="checkbox" checked>
                    </div>
                    <div class="col product-info">
                        <img src="${item.productImage}" alt="${item.productName}">
                        <span>${item.productName}</span>
                    </div>
                    <div class="col-2 text-center">${item.productPrice.toFixed(2)}元</div>
                    <div class="col-2 text-center">
                        <div class="quantity-selector">
                            <button class="btn btn-sm decrease-qty">-</button>
                            <input type="text" class="quantity-input" value="${item.quantity}" readonly>
                            <button class="btn btn-sm increase-qty">+</button>
                        </div>
                    </div>
                    <div class="col-1 text-center item-subtotal">${(item.productPrice * item.quantity).toFixed(2)}元</div>
                    <div class="col-1 text-center">
                        <i class="fas fa-times delete-btn"></i>
                    </div>
                </div>
            `).join('');
            updateSummary();
            addEventListeners();
        }
    }

    function updateSummary() {
        // 只统计全部选中（后端暂不支持选中状态）
        const totalCount = cartItems.reduce((sum, item) => sum + item.quantity, 0);
        const totalPrice = cartItems.reduce((sum, item) => sum + item.productPrice * item.quantity, 0);
        selectedCountEl.textContent = totalCount;
        totalPriceEl.textContent = `${totalPrice.toFixed(2)}元`;
        selectAllCheckbox.checked = cartItems.length > 0;
    }

    function addEventListeners() {
        selectAllCheckbox.addEventListener('change', async (e) => {
            // 后端暂不支持批量选中，忽略
            renderCart();
        });

        document.querySelectorAll('.cart-item-row').forEach(row => {
            const id = parseInt(row.dataset.id);

            // 选中状态（后端暂不支持，默认全选）
            row.querySelector('.item-checkbox').addEventListener('change', (e) => {
                // 忽略
                renderCart();
            });

            row.querySelector('.increase-qty').addEventListener('click', async () => {
                const item = cartItems.find(i => i.productId === id);
                await window.CartAPI.updateCart(id, item.quantity + 1);
                renderCart();
            });

            row.querySelector('.decrease-qty').addEventListener('click', async () => {
                const item = cartItems.find(i => i.productId === id);
                if (item.quantity > 1) {
                    await window.CartAPI.updateCart(id, item.quantity - 1);
                    renderCart();
                }
            });
            
            row.querySelector('.delete-btn').addEventListener('click', async () => {
                // 获取商品信息
                const item = cartItems.find(i => i.productId === id);
                if (item) {
                    // 设置当前要删除的商品ID和名称
                    currentDeleteProductId = id;
                    deleteProductNameEl.textContent = item.productName;
                    // 显示确认删除对话框
                    confirmModal.show();
                }
            });
        });
    }

    function init() {
        renderUserActions();
        loadRecommendations();
        renderCart();
    }

    function renderUserActions() {
        const user = authManager.getUser();
        const userActionsContainer = document.getElementById('user-actions');
        if (user && userActionsContainer) {
            userActionsContainer.innerHTML = `
                <div class="dropdown">
                    <a href="#" class="nav-link dropdown-toggle" id="userDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="fas fa-user"></i> ${user.username}
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                        <li><a class="dropdown-item" href="../portal/portal.html">个人中心</a></li>
                        <li><a class="dropdown-item" href="#">我的订单</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#" id="logout-btn">退出登录</a></li>
                    </ul>
                </div>
            `;
            const logoutBtn = document.getElementById('logout-btn');
            if(logoutBtn) {
                logoutBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    authManager.logout();
                    window.location.reload();
                });
            }
        }
    }

    function loadRecommendations() {
        const recommendations = [
            { name: 'REDMI K80', price: '2499元', image: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTYwIiBoZWlnaHQ9IjE2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjBmMGYwIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPlByb2R1Y3Q8L3RleHQ+PC9zdmc+' },
            { name: '米家方框太阳镜', price: '99元', image: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTYwIiBoZWlnaHQ9IjE2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjBmMGYwIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPlByb2R1Y3Q8L3RleHQ+PC9zdmc+' },
            { name: 'REDMI Watch 5', price: '599元', image: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTYwIiBoZWlnaHQ9IjE2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjBmMGYwIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPlByb2R1Y3Q8L3RleHQ+PC9zdmc+' },
            { name: 'Xiaomi 15', price: '4199元', image: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTYwIiBoZWlnaHQ9IjE2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjBmMGYwIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPlByb2R1Y3Q8L3RleHQ+PC9zdmc+' },
            { name: 'Redmi Note 10 5G', price: '1099元', image: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTYwIiBoZWlnaHQ9IjE2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjBmMGYwIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPlByb2R1Y3Q8L3RleHQ+PC9zdmc+' }
        ];

        const recommendationsList = document.getElementById('recommendations-list');
        recommendationsList.innerHTML = recommendations.map(product => `
            <div class="col">
                <div class="product-card">
                    <img src="${product.image}" alt="${product.name}">
                    <div class="product-name">${product.name}</div>
                    <div class="product-price">${product.price}</div>
                </div>
            </div>
        `).join('');
    }

    init();
}); 