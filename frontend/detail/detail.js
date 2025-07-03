// 商品详情页JavaScript
class ProductDetail {
    constructor() {
        this.productId = new URLSearchParams(window.location.search).get('id');
        this.product = null;
        this.selectedSpecs = {};
        this.quantity = 1;
        
        if (!this.productId) {
            this.showError('未找到商品ID，请检查URL参数');
            return;
        }
        
        this.init();
    }

    // 初始化
    async init() {
        this.bindEvents();
        this.updateLoginStatus();
        this.loadCartDisplay();
        await this.loadProductDetail();
    }

    // 绑定事件
    bindEvents() {
        // 搜索功能
        document.getElementById('searchInput').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.searchProducts();
            }
        });

        document.getElementById('searchBtn').addEventListener('click', () => {
            this.searchProducts();
        });

        // 数量控制
        document.getElementById('quantityInput').addEventListener('change', (e) => {
            this.quantity = parseInt(e.target.value) || 1;
            this.updateQuantityButtons();
        });

        // 登录表单
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleLogin();
            });
        }

        // 购买按钮
        const addToCartBtn = document.getElementById('add-to-cart-btn');
        const buyNowBtn = document.getElementById('buy-now-btn');
        const quantityInput = document.getElementById('quantityInput');

        if (addToCartBtn) {
            addToCartBtn.addEventListener('click', () => {
                this.addToCart();
            });
        }

        if (buyNowBtn) {
            buyNowBtn.addEventListener('click', () => {
                this.buyNow();
            });
        }

        // 侧边栏回到顶部按钮
        window.addEventListener('scroll', () => {
            const backToTopBtn = document.getElementById('back-to-top');
            if (backToTopBtn) {
                if (window.pageYOffset > 300) {
                    backToTopBtn.style.display = 'flex';
                } else {
                    backToTopBtn.style.display = 'none';
                }
            }
        });

        // 侧边栏回到顶部按钮点击事件
        const backToTopBtn = document.getElementById('back-to-top');
        if (backToTopBtn) {
            backToTopBtn.addEventListener('click', (e) => {
                e.preventDefault();
                this.scrollToTop();
            });
        }

        // 侧边栏客服按钮点击事件
        const customerServiceBtn = document.querySelector('.sidebar-item[title="联系客服"]');
        if (customerServiceBtn) {
            customerServiceBtn.addEventListener('click', (e) => {
                e.preventDefault();
                this.showCustomerService();
            });
        }
    }

    // 加载商品详情
    async loadProductDetail() {
        try {
            this.showLoading();
            const response = await this.fetchProductDetail(this.productId);
            this.product = response;
            this.renderProductDetail();
            this.hideLoading();
        } catch (error) {
            console.error('加载商品详情失败:', error);
            this.hideLoading();
            this.showError('加载商品详情失败，请稍后重试');
        }
    }

    // 调用后端API获取商品详情
    async fetchProductDetail(productId) {
        const response = await fetch(`${window.API_CONFIG.product.baseUrl}/${productId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 404) {
                throw new Error('商品不存在');
            } else {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
        }

        const product = await response.json();
        
        // 将后端返回的数据转换为前端需要的格式
        return this.transformProductData(product);
    }

    // 转换商品数据格式
    transformProductData(product) {
        return {
            id: product.id,
            name: product.name,
            description: product.description || '暂无描述',
            price: parseFloat(product.price),
            originalPrice: parseFloat(product.price) * 1.2, // 模拟原价
            stock: product.stock || 0,
            brand: product.brand || '未知品牌',
            brandId: product.brandId,
            category: product.category || '其他分类',
            categoryId: product.categoryId,
            imageUrl: product.imageUrl || this.getDefaultImage(),
            images: this.generateProductImages(product.imageUrl),
            specs: this.generateProductSpecs(product),
            detail: this.generateProductDetail(product),
            specsDetail: this.generateSpecsDetail(product),
            reviews: this.generateMockReviews(product),
            status: product.status
        };
    }

    // 生成默认图片
    getDefaultImage() {
        return 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400&h=400&fit=crop';
    }

    // 生成商品图片列表
    generateProductImages(mainImage) {
        if (!mainImage) {
            return [
                'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400&h=400&fit=crop',
                'https://images.unsplash.com/photo-1570913149827-d2ac84ab3f9a?w=400&h=400&fit=crop',
                'https://images.unsplash.com/photo-1557800636-894a64c1696f?w=400&h=400&fit=crop'
            ];
        }
        
        // 基于主图生成多张图片
        return [
            mainImage,
            mainImage.replace('w=400&h=400', 'w=400&h=400&fit=crop'),
            mainImage.replace('w=400&h=400', 'w=400&h=400&fit=cover')
        ];
    }

    // 生成商品规格
    generateProductSpecs(product) {
        const specs = {};
        
        // 分类映射表
        const categoryMap = {
            1: '新鲜水果',
            2: '有机蔬菜',
            3: '海鲜水产',
            4: '肉禽蛋品'
        };
        
        if (product.categoryId) {
            specs['分类'] = [categoryMap[product.categoryId] || '其他分类'];
        } else if (product.category) {
            specs['分类'] = [product.category, '其他分类'];
        }
        
        if (product.brand) {
            specs['品牌'] = [product.brand, '其他品牌'];
        }
        
        specs['规格'] = ['500g', '1kg', '2kg'];
        specs['产地'] = ['山东烟台', '陕西延安', '新疆阿克苏'];
        
        return specs;
    }

    // 生成商品详情
    generateProductDetail(product) {
        return `
            <div class="product-description">
                <h3 class="mb-3">商品详情</h3>
                <div class="row">
                    <div class="col-md-8">
                        <p class="lead">${product.description || '暂无详细描述'}</p>
                        <div class="product-features mt-4">
                            <h5>产品特点：</h5>
                            <ul class="list-unstyled">
                                <li><i class="fas fa-check text-success me-2"></i>新鲜采摘，品质保证</li>
                                <li><i class="fas fa-check text-success me-2"></i>无农药残留，安全健康</li>
                                <li><i class="fas fa-check text-success me-2"></i>产地直供，价格实惠</li>
                                <li><i class="fas fa-check text-success me-2"></i>快速配送，新鲜到家</li>
                            </ul>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="product-info-card bg-light p-3 rounded">
                            <h6>基本信息</h6>
                            <p><strong>品牌：</strong>${product.brand || '未知'}</p>
                            <p><strong>分类：</strong>${product.category || '其他'}</p>
                            <p><strong>库存：</strong>${product.stock || 0}件</p>
                            <p><strong>状态：</strong>${product.status ? '上架' : '下架'}</p>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    // 生成规格详情
    generateSpecsDetail(product) {
        return `
            <div class="product-specifications">
                <h3 class="mb-3">规格参数</h3>
                <table class="table table-striped">
                    <tbody>
                        <tr>
                            <td><strong>商品名称</strong></td>
                            <td>${product.name}</td>
                        </tr>
                        <tr>
                            <td><strong>商品品牌</strong></td>
                            <td>${product.brand || '未知'}</td>
                        </tr>
                        <tr>
                            <td><strong>商品分类</strong></td>
                            <td>${product.category || '其他'}</td>
                        </tr>
                        <tr>
                            <td><strong>商品价格</strong></td>
                            <td>¥${product.price}</td>
                        </tr>
                        <tr>
                            <td><strong>库存数量</strong></td>
                            <td>${product.stock || 0}件</td>
                        </tr>
                        <tr>
                            <td><strong>商品状态</strong></td>
                            <td>${product.status ? '上架' : '下架'}</td>
                        </tr>
                        <tr>
                            <td><strong>创建时间</strong></td>
                            <td>${new Date().toLocaleDateString()}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        `;
    }

    // 生成模拟评价
    generateMockReviews(product) {
        return [
            {
                user: '张**',
                rating: 5,
                date: '2024-01-15',
                content: '商品质量很好，包装也很精美，物流速度快，会继续购买！'
            },
            {
                user: '李**',
                rating: 4,
                date: '2024-01-10',
                content: '价格实惠，品质不错，就是配送稍微慢了一点。'
            },
            {
                user: '王**',
                rating: 5,
                date: '2024-01-08',
                content: '第二次购买了，一如既往的好，推荐给大家！'
            }
        ];
    }

    // 渲染商品详情
    renderProductDetail() {
        if (!this.product) return;

        // 更新页面标题
        document.title = `${this.product.name} - 农产品商城`;

        // 显示商品详情容器
        document.getElementById('product-detail-container').style.display = 'flex';
        document.getElementById('product-tabs-container').style.display = 'block';

        // 更新商品信息
        document.getElementById('productTitle').textContent = this.product.name;
        document.getElementById('currentPrice').textContent = this.product.price.toFixed(2);
        document.getElementById('originalPrice').textContent = this.product.originalPrice.toFixed(2);
        document.getElementById('stockInfo').textContent = `库存: ${this.product.stock}件`;

        // 更新库存状态
        const stockBadge = document.getElementById('stockBadge');
        if (this.product.stock > 0) {
            stockBadge.className = 'badge bg-success me-2';
            stockBadge.innerHTML = '<i class="fas fa-check-circle me-1"></i>有货';
        } else {
            stockBadge.className = 'badge bg-danger me-2';
            stockBadge.innerHTML = '<i class="fas fa-times-circle me-1"></i>缺货';
        }

        // 计算折扣
        const discount = Math.round((1 - this.product.price / this.product.originalPrice) * 100);
        if (discount > 0) {
            document.getElementById('discountTag').textContent = `${discount}折`;
            document.getElementById('discountTag').style.display = 'inline-block';
        } else {
            document.getElementById('discountTag').style.display = 'none';
        }

        // 渲染各个组件
        this.renderProductImages();
        this.renderProductSpecs();
        this.renderProductTabs();
        this.updateQuantityButtons();
    }

    // 渲染商品图片
    renderProductImages() {
        const mainImage = document.getElementById('mainImage');
        const thumbnailList = document.getElementById('thumbnailList');

        mainImage.src = this.product.imageUrl;
        mainImage.alt = this.product.name;

        thumbnailList.innerHTML = '';

        this.product.images.forEach((image, index) => {
            const thumbnail = document.createElement('div');
            thumbnail.className = `thumbnail-item ${index === 0 ? 'active' : ''}`;
            thumbnail.onclick = () => this.switchImage(image, index);

            const img = document.createElement('img');
            img.src = image;
            img.alt = `${this.product.name} - 图片${index + 1}`;
            img.className = 'img-fluid rounded';

            thumbnail.appendChild(img);
            thumbnailList.appendChild(thumbnail);
        });
    }

    // 切换图片
    switchImage(imageUrl, index) {
        document.getElementById('mainImage').src = imageUrl;

        const thumbnails = document.querySelectorAll('.thumbnail-item');
        thumbnails.forEach((thumb, i) => {
            thumb.classList.toggle('active', i === index);
        });
    }

    // 渲染商品规格
    renderProductSpecs() {
        const specOptions = document.getElementById('specOptions');
        specOptions.innerHTML = '';

        if (!this.product.specs) return;

        Object.entries(this.product.specs).forEach(([specName, specValues]) => {
            const specGroup = document.createElement('div');
            specGroup.className = 'spec-group mb-3';
            specGroup.innerHTML = `<h6 class="mb-2">${specName}:</h6>`;

            const optionsContainer = document.createElement('div');
            optionsContainer.className = 'spec-options d-flex gap-2 flex-wrap';

            specValues.forEach((value, index) => {
                const option = document.createElement('button');
                option.className = `btn btn-outline-secondary btn-sm spec-option ${index === 0 ? 'active' : ''}`;
                option.textContent = value;
                option.onclick = () => this.selectSpec(specName, value, option);

                optionsContainer.appendChild(option);
            });

            specGroup.appendChild(optionsContainer);
            specOptions.appendChild(specGroup);

            this.selectedSpecs[specName] = specValues[0];
        });
    }

    // 选择规格
    selectSpec(specName, value, element) {
        const specGroup = element.closest('.spec-group');
        specGroup.querySelectorAll('.spec-option').forEach(option => {
            option.classList.remove('active');
        });
        element.classList.add('active');

        this.selectedSpecs[specName] = value;
        console.log('选中的规格:', this.selectedSpecs);
    }

    // 渲染商品详情标签页
    renderProductTabs() {
        document.getElementById('detailContent').innerHTML = this.product.detail || '暂无详情';
        document.getElementById('specsContent').innerHTML = this.product.specsDetail || '暂无规格参数';
        this.renderReviews();
    }

    // 渲染评价
    renderReviews() {
        const reviewsContent = document.getElementById('reviewsContent');
        
        if (!this.product.reviews || this.product.reviews.length === 0) {
            reviewsContent.innerHTML = '<p class="text-muted">暂无评价</p>';
            return;
        }

        const reviewsHtml = this.product.reviews.map(review => `
            <div class="review-item border-bottom pb-3 mb-3">
                <div class="review-header d-flex justify-content-between align-items-center mb-2">
                    <span class="review-user fw-bold">${review.user}</span>
                    <div class="review-stars text-warning">
                        ${this.renderStars(review.rating)}
                    </div>
                    <span class="review-date text-muted small">${review.date}</span>
                </div>
                <div class="review-content">${review.content}</div>
            </div>
        `).join('');

        reviewsContent.innerHTML = reviewsHtml;
    }

    // 渲染星级
    renderStars(rating) {
        const fullStars = Math.floor(rating);
        const hasHalfStar = rating % 1 !== 0;
        let starsHtml = '';

        for (let i = 0; i < fullStars; i++) {
            starsHtml += '<i class="fas fa-star"></i>';
        }
        if (hasHalfStar) {
            starsHtml += '<i class="fas fa-star-half-alt"></i>';
        }
        const emptyStars = 5 - Math.ceil(rating);
        for (let i = 0; i < emptyStars; i++) {
            starsHtml += '<i class="far fa-star"></i>';
        }

        return starsHtml;
    }

    // 数量控制
    increaseQuantity() {
        if (this.quantity < this.product.stock) {
            this.quantity++;
            document.getElementById('quantityInput').value = this.quantity;
            this.updateQuantityButtons();
        }
    }

    decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
            document.getElementById('quantityInput').value = this.quantity;
            this.updateQuantityButtons();
        }
    }

    updateQuantityButtons() {
        const decreaseBtn = document.querySelector('.quantity-btn:first-child');
        const increaseBtn = document.querySelector('.quantity-btn:last-child');

        if (decreaseBtn && increaseBtn) {
            decreaseBtn.disabled = this.quantity <= 1;
            increaseBtn.disabled = this.quantity >= this.product.stock;
        }
    }

    // 搜索功能
    searchProducts() {
        const keyword = document.getElementById('searchInput').value.trim();
        if (keyword) {
            window.location.href = `../search/search.html?keyword=${encodeURIComponent(keyword)}`;
        }
    }

    // 登录相关
    async handleLogin() {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch(`${window.API_CONFIG.auth.baseUrl}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('token', data.token);
                localStorage.setItem('user', JSON.stringify(data.user));
                
                // 关闭登录模态框
                const loginModal = bootstrap.Modal.getInstance(document.getElementById('loginModal'));
                if (loginModal) {
                    loginModal.hide();
                }
                
                this.updateLoginStatus();
                this.showToast('登录成功');
            } else {
                this.showToast('登录失败，请检查用户名和密码', 'error');
            }
        } catch (error) {
            console.error('登录错误:', error);
            this.showToast('登录失败，请稍后重试', 'error');
        }
    }

    updateLoginStatus() {
        const loginArea = document.getElementById('loginArea');
        const user = JSON.parse(localStorage.getItem('user') || 'null');
        const token = localStorage.getItem('token');

        if (user && token) {
            loginArea.innerHTML = `
                <button class="btn btn-outline-success dropdown-toggle" type="button" data-bs-toggle="dropdown">
                    <i class="fas fa-user me-2"></i>${user.username}
                </button>
                <ul class="dropdown-menu">
                    <li><a class="dropdown-item" href="#"><i class="fas fa-user me-2"></i>个人中心</a></li>
                    <li><a class="dropdown-item" href="#"><i class="fas fa-shopping-cart me-2"></i>我的订单</a></li>
                    <li><hr class="dropdown-divider"></li>
                    <li><a class="dropdown-item" href="#" onclick="logout()"><i class="fas fa-sign-out-alt me-2"></i>退出登录</a></li>
                </ul>
            `;
        } else {
            loginArea.innerHTML = `
                <button class="btn btn-outline-success" type="button" data-bs-toggle="modal" data-bs-target="#loginModal">
                    <i class="fas fa-sign-in-alt me-2"></i>登录
                </button>
            `;
        }
    }

    // 购物车相关
    async addToCart() {
        if (!this.product) {
            this.showToast('商品信息加载失败', 'error');
            return;
        }

        // 检查用户登录状态
        if (!this.isUserLoggedIn()) {
            this.showLoginModal();
            return;
        }

        const quantity = parseInt(document.getElementById('quantityInput').value) || 1;
        // 验证数量
        if (quantity <= 0) {
            this.showToast('请选择有效的购买数量', 'error');
            return;
        }
        if (quantity > this.product.stock) {
            this.showToast(`库存不足，当前库存${this.product.stock}件`, 'error');
            return;
        }

        // 构建商品信息
        const product = {
            id: this.product.id,
            name: this.product.name,
            price: this.product.price,
            image: this.product.imageUrl,
            stock: this.product.stock,
            brand: this.product.brand,
            category: this.product.category
        };

        try {
            const res = await window.CartAPI.addToCart(product, quantity);
            if (res && res.id) {
                this.showToast('商品已加入购物车', 'success');
                this.updateCartBadge();
            } else {
                this.showToast(res.message || '加入购物车失败', 'error');
            }
        } catch (e) {
            this.showToast('加入购物车失败，请稍后重试', 'error');
        }
    }

    buyNow() {
        if (!this.product) {
            this.showToast('商品信息加载失败', 'error');
            return;
        }

        // 检查用户登录状态
        if (!this.isUserLoggedIn()) {
            this.showLoginModal();
            return;
        }

        const quantity = parseInt(document.getElementById('quantityInput').value) || 1;
        
        // 验证数量
        if (quantity <= 0) {
            this.showToast('请选择有效的购买数量', 'error');
            return;
        }

        if (quantity > this.product.stock) {
            this.showToast(`库存不足，当前库存${this.product.stock}件`, 'error');
            return;
        }

        // 直接跳转到结算页面
        const orderData = {
            items: [{
                id: this.product.id,
                name: this.product.name,
                price: this.product.price,
                image: this.product.imageUrl,
                quantity: quantity,
                specs: this.selectedSpecs
            }]
        };

        localStorage.setItem('checkoutData', JSON.stringify(orderData));
        window.location.href = '../cart/cart.html?checkout=true';
    }

    // 检查用户是否登录
    isUserLoggedIn() {
        return localStorage.getItem('token') !== null;
    }

    // 显示登录模态框
    showLoginModal() {
        const loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
        loginModal.show();
    }

    // 加载购物车显示
    async loadCartDisplay() {
        if (!window.CartAPI) return;
        const cartItems = await window.CartAPI.getCart();
        const cartItemsContainer = document.getElementById('cartItems');
        if (!cartItemsContainer) return;
        if (!cartItems || cartItems.length === 0) {
            cartItemsContainer.innerHTML = '<p class="text-muted text-center">购物车为空</p>';
            return;
        }
        const cartHtml = cartItems.map(item => `
            <div class="cart-item d-flex align-items-center p-3 border-bottom">
                <img src="${item.productImage}" alt="${item.productName}" class="cart-item-image me-3" style="width: 60px; height: 60px; object-fit: cover;">
                <div class="cart-item-info flex-grow-1">
                    <h6 class="cart-item-name mb-1">${item.productName}</h6>
                    <p class="cart-item-price text-success mb-0">¥${item.productPrice} × ${item.quantity}</p>
                </div>
                <button class="btn btn-sm btn-outline-danger" onclick="removeFromCart(${item.productId})">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        `).join('');
        cartItemsContainer.innerHTML = cartHtml;
        // 更新总价
        const total = cartItems.reduce((sum, item) => sum + item.productPrice * item.quantity, 0);
        document.getElementById('cartTotalPrice').textContent = `¥${total.toFixed(2)}`;
    }

    // 更新购物车徽章
    updateCartBadge() {
        if (window.CartAPI) {
            window.CartAPI.getCartStats().then(stats => {
                const cartBadge = document.getElementById('cartBadge');
                if (cartBadge) {
                    cartBadge.textContent = stats.totalQuantity;
                    cartBadge.style.display = stats.totalQuantity > 0 ? 'inline' : 'none';
                }
            });
        }
    }

    // 工具方法
    showLoading() {
        document.getElementById('loading-container').style.display = 'block';
        document.getElementById('product-detail-container').style.display = 'none';
        document.getElementById('product-tabs-container').style.display = 'none';
        document.getElementById('error-container').style.display = 'none';
    }

    hideLoading() {
        document.getElementById('loading-container').style.display = 'none';
    }

    showError(message) {
        document.getElementById('loading-container').style.display = 'none';
        document.getElementById('product-detail-container').style.display = 'none';
        document.getElementById('product-tabs-container').style.display = 'none';
        document.getElementById('error-container').style.display = 'block';
        document.getElementById('error-message').textContent = message;
    }

    // 显示Toast消息
    showToast(message, type = 'success') {
        const toastContainer = document.getElementById('toast-container');
        if (!toastContainer) return;

        const toast = document.createElement('div');
        toast.className = `toast align-items-center text-white bg-${type} border-0`;
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');

        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'} me-2"></i>
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        `;

        toastContainer.appendChild(toast);

        const bsToast = new bootstrap.Toast(toast, {
            autohide: true,
            delay: 3000
        });

        bsToast.show();

        // 自动移除
        toast.addEventListener('hidden.bs.toast', () => {
            toastContainer.removeChild(toast);
        });
    }

    // 滚动到顶部
    scrollToTop() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    }

    // 显示客服对话框
    showCustomerService() {
        this.showToast('客服功能正在开发中，请稍后使用', 'info');
    }
}

// 全局函数
function searchProducts() {
    const keyword = document.getElementById('searchInput').value.trim();
    if (keyword) {
        window.location.href = `../search/search.html?keyword=${encodeURIComponent(keyword)}`;
    }
}

function switchTab(tabName) {
    // 移除所有活动状态
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // 添加活动状态到当前标签
    const activeTab = document.getElementById(`${tabName}-tab`);
    if (activeTab) {
        activeTab.classList.add('active');
    }
}

function increaseQuantity() {
    const input = document.getElementById('quantityInput');
    const currentValue = parseInt(input.value) || 1;
    if (currentValue < 99) {
        input.value = currentValue + 1;
        input.dispatchEvent(new Event('change'));
    }
}

function decreaseQuantity() {
    const input = document.getElementById('quantityInput');
    const currentValue = parseInt(input.value) || 1;
    if (currentValue > 1) {
        input.value = currentValue - 1;
        input.dispatchEvent(new Event('change'));
    }
}

function addToCart() {
    if (window.productDetailInstance) {
        window.productDetailInstance.addToCart();
    }
}

function buyNow() {
    if (window.productDetailInstance) {
        window.productDetailInstance.buyNow();
    }
}

function checkout() {
    window.location.href = '../cart/cart.html';
}

function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.reload();
}

function removeFromCart(itemId) {
    if (window.productDetailInstance) {
        window.productDetailInstance.removeFromCart(itemId);
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    window.productDetailInstance = new ProductDetail();
}); 