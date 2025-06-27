// 全局变量
let currentPage = 0;
let currentKeyword = '';
let currentFilters = {};
let currentView = 'grid'; // grid 或 list
let totalPages = 0;
let totalElements = 0;

// DOM加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    initializePage();
    setupEventListeners();
    loadCategories();
    updateLoginStatus(); // 初始化登录状态
    
    // 从URL参数初始化搜索
    const urlParams = new URLSearchParams(window.location.search);
    const keyword = urlParams.get('keyword');
    const categoryId = urlParams.get('category');
    
    let needsSearch = false;

    if (keyword) {
        document.getElementById('searchInput').value = keyword;
        needsSearch = true;
    }
    
    if (categoryId) {
        const categoryRadio = document.querySelector(`input[name="category"][value="${categoryId}"]`);
        if (categoryRadio) {
            categoryRadio.checked = true;
        }
        needsSearch = true;
    }

    // 默认展示所有商品，除非有特定的搜索条件
    if (needsSearch) {
        performSearch();
    } else {
        // 默认加载所有商品
        loadAllProducts();
    }
});

// 初始化页面
function initializePage() {
    // 设置默认视图
    setView('grid');
    
    // 初始化价格范围
    const priceRange = document.getElementById('priceRange');
    const priceValue = document.getElementById('priceValue');
    if (priceRange && priceValue) {
        priceRange.addEventListener('input', function() {
            priceValue.textContent = '¥' + this.value;
        });
    }
}

// 设置事件监听器
function setupEventListeners() {
    // 搜索按钮点击事件
    const searchBtn = document.getElementById('searchBtn');
    if (searchBtn) {
        searchBtn.addEventListener('click', performSearch);
    }
    
    // 搜索框回车事件
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch();
            }
        });
    }
    
    // 筛选条件变化事件
    const categoryRadios = document.querySelectorAll('input[name="category"]');
    categoryRadios.forEach(radio => {
        radio.addEventListener('change', performSearch);
    });
    
    const priceRadios = document.querySelectorAll('input[name="price"]');
    priceRadios.forEach(radio => {
        radio.addEventListener('change', performSearch);
    });
    
    const sortRadios = document.querySelectorAll('input[name="sort"]');
    sortRadios.forEach(radio => {
        radio.addEventListener('change', performSearch);
    });
    
    // 视图切换按钮
    const viewGrid = document.getElementById('viewGrid');
    const viewList = document.getElementById('viewList');
    
    if (viewGrid) {
        viewGrid.addEventListener('click', () => setView('grid'));
    }
    if (viewList) {
        viewList.addEventListener('click', () => setView('list'));
    }
    
    // 应用筛选按钮
    const applyFilters = document.getElementById('applyFilters');
    if (applyFilters) {
        applyFilters.addEventListener('click', performSearch);
    }
    
    // 登录按钮点击事件
    const loginBtn = document.getElementById('loginBtn');
    if (loginBtn) {
        loginBtn.addEventListener('click', function() {
            const token = localStorage.getItem('token');
            if (token) {
                showUserMenu();
            } else {
                showLoginModal();
            }
        });
    }
    
    // 登录弹框相关事件
    setupLoginModal();
}

// 更新登录状态
function updateLoginStatus() {
    const token = localStorage.getItem('token');
    const loginArea = document.getElementById('loginArea');
    
    if (loginArea) {
        if (token) {
            const user = JSON.parse(localStorage.getItem('user') || '{}');
            loginArea.innerHTML = `
                <div class="dropdown">
                    <button class="btn btn-outline-success dropdown-toggle" type="button" data-bs-toggle="dropdown">
                        <i class="fas fa-user"></i> ${user.nickname || user.username}
                    </button>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="../portal/portal.html">个人中心</a></li>
                        <li><a class="dropdown-item" href="../cart/cart.html">购物车</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#" onclick="logout()">退出登录</a></li>
                    </ul>
                </div>
            `;
        } else {
            loginArea.innerHTML = `
                <a href="../login/login.html" class="btn btn-outline-success">
                    <i class="fas fa-user"></i> 登录
                </a>
            `;
        }
    }
}

// 显示用户菜单
function showUserMenu() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    alert(`欢迎，${user.nickname || user.username}！\n这里可以显示用户菜单`);
}

// 加载分类数据
async function loadCategories() {
    try {
        // 使用模拟数据，因为后端没有专门的分类API
        const categories = [
            { id: 1, name: '新鲜水果' },
            { id: 2, name: '有机蔬菜' },
            { id: 3, name: '粮油调味' },
            { id: 4, name: '肉禽蛋奶' },
            { id: 5, name: '水产海鲜' },
            { id: 6, name: '休闲零食' }
        ];
        populateCategoryFilter(categories);
    } catch (error) {
        console.error('加载分类出错:', error);
    }
}

// 填充分类筛选器
function populateCategoryFilter(categories) {
    // 分类已经在HTML中硬编码，这里不需要动态填充
    console.log('分类数据已加载:', categories);
}

// 格式化价格显示
function formatPrice(price) {
    if (price === null || price === undefined) {
        return '0.00';
    }
    // 确保price是数字类型
    const numPrice = typeof price === 'string' ? parseFloat(price) : price;
    return isNaN(numPrice) ? '0.00' : numPrice.toFixed(2);
}

// 加载所有商品（默认展示）
async function loadAllProducts() {
    showLoading();
    
    try {
        // 构建查询参数 - 不添加任何筛选条件，获取所有商品
        const params = new URLSearchParams();
        params.append('page', 0);
        params.append('size', 12);
        
        console.log('加载所有商品，API地址:', `${window.API_CONFIG.product.baseUrl}/search/filters?${params.toString()}`);
        
        const response = await fetch(`${window.API_CONFIG.product.baseUrl}/search/filters?${params.toString()}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        });
        
        console.log('响应状态:', response.status);
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error('API错误响应:', errorText);
            throw new Error(`加载商品失败: ${response.status} - ${errorText}`);
        }
        
        const data = await response.json();
        console.log('所有商品数据:', data);
        
        // 更新当前搜索条件为空
        currentKeyword = '';
        currentFilters = {
            categoryId: null,
            minPrice: null,
            maxPrice: null,
            sortBy: 'default'
        };
        
        displaySearchResults(data);
        
    } catch (error) {
        console.error('加载所有商品出错:', error);
        showError('加载商品失败，请稍后重试');
        // 显示模拟数据作为备用
        displayMockResults();
    } finally {
        hideLoading();
    }
}

// 执行搜索
async function performSearch() {
    const searchInput = document.getElementById('searchInput');
    const categoryRadios = document.querySelectorAll('input[name="category"]:checked');
    const priceRadios = document.querySelectorAll('input[name="price"]:checked');
    const sortRadios = document.querySelectorAll('input[name="sort"]:checked');
    
    const keyword = searchInput ? searchInput.value.trim() : '';
    const categoryId = categoryRadios.length > 0 ? categoryRadios[0].value : '';
    const priceRange = priceRadios.length > 0 ? priceRadios[0].value : '';
    const sortBy = sortRadios.length > 0 ? sortRadios[0].value : 'default';
    
    // 解析价格范围
    let minPrice = null;
    let maxPrice = null;
    if (priceRange && priceRange !== '') {
        if (priceRange.includes('-')) {
            const [min, max] = priceRange.split('-');
            minPrice = parseFloat(min);
            maxPrice = parseFloat(max);
        } else if (priceRange.endsWith('+')) {
            minPrice = parseFloat(priceRange.replace('+', ''));
        }
    }

    // 更新当前搜索条件
    currentKeyword = keyword;
    currentFilters = {
        categoryId: categoryId || null,
        minPrice: minPrice,
        maxPrice: maxPrice,
        sortBy: sortBy || 'default'
    };
    
    // 重置页码
    currentPage = 0;
    
    showLoading();
    
    try {
        // 构建查询参数
        const params = new URLSearchParams();
        if (keyword) params.append('keyword', keyword);
        if (categoryId) params.append('categoryId', categoryId);
        if (minPrice !== null) params.append('minPrice', minPrice);
        if (maxPrice !== null) params.append('maxPrice', maxPrice);
        params.append('page', currentPage);
        params.append('size', 12);
        
        console.log('搜索参数:', params.toString());
        console.log('API地址:', `${window.API_CONFIG.product.baseUrl}/search/filters?${params.toString()}`);
        
        // 使用正确的API配置
        const response = await fetch(`${window.API_CONFIG.product.baseUrl}/search/filters?${params.toString()}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        });
        
        console.log('响应状态:', response.status);
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error('API错误响应:', errorText);
            throw new Error(`搜索失败: ${response.status} - ${errorText}`);
        }
        
        const data = await response.json();
        console.log('搜索结果数据:', data);
        displaySearchResults(data);
        
    } catch (error) {
        console.error('搜索出错:', error);
        showError('搜索失败，请稍后重试');
        // 显示模拟数据作为备用
        displayMockResults();
    } finally {
        hideLoading();
    }
}

// 显示搜索结果
function displaySearchResults(data) {
    const productList = document.getElementById('productList');
    const resultCount = document.getElementById('resultCount');
    const noResults = document.getElementById('noResults');
    const searchTitle = document.getElementById('searchTitle'); // 添加搜索标题元素
    
    if (!productList) return;
    
    // 更新结果统计
    if (resultCount) {
        resultCount.textContent = data.totalElements || 0;
    }
    
    // 更新搜索标题和面包屑
    if (searchTitle) {
        if (currentKeyword || currentFilters.categoryId || currentFilters.minPrice !== null || currentFilters.maxPrice !== null) {
            searchTitle.textContent = '搜索结果';
            updateBreadcrumb('商品搜索');
            updatePageTitle('商品搜索');
        } else {
            searchTitle.textContent = '所有商品';
            updateBreadcrumb('所有商品');
            updatePageTitle('所有商品');
        }
    }
    
    // 更新分页信息
    totalPages = data.totalPages || 0;
    totalElements = data.totalElements || 0;
    
    // 清空现有结果
    productList.innerHTML = '';
    
    if (data.content && data.content.length > 0) {
        // 显示商品列表
        data.content.forEach(product => {
            const productElement = createProductElement(product);
            productList.appendChild(productElement);
        });
        
        // 显示分页
        displayPagination();
        
        // 隐藏无结果提示
        if (noResults) {
            noResults.style.display = 'none';
        }
    } else {
        // 显示无结果提示
        if (noResults) {
            noResults.style.display = 'block';
        }
    }
}

// 显示模拟结果（当API不可用时）
function displayMockResults() {
    const productList = document.getElementById('productList');
    const resultCount = document.getElementById('resultCount');
    
    if (!productList) return;
    
    // 清空现有结果
    productList.innerHTML = '';
    
    // 生成模拟数据
    const mockProducts = [
        {
            id: 1,
            name: '新鲜苹果',
            description: '红富士苹果，甜脆可口',
            price: 15.80,
            imageUrl: 'https://picsum.photos/300/200?random=1',
            category: '新鲜水果',
            brand: '优质品牌',
            stock: 100
        },
        {
            id: 2,
            name: '有机胡萝卜',
            description: '无农药有机胡萝卜',
            price: 8.50,
            imageUrl: 'https://picsum.photos/300/200?random=2',
            category: '有机蔬菜',
            brand: '有机农场',
            stock: 50
        }
    ];
    
    // 更新结果统计
    if (resultCount) {
        resultCount.textContent = mockProducts.length;
    }
    
    // 显示商品列表
    mockProducts.forEach(product => {
        const productElement = createProductElement(product);
        productList.appendChild(productElement);
    });
}

// 创建商品元素
function createProductElement(product) {
    const productDiv = document.createElement('div');
    productDiv.className = `col-md-6 col-lg-4 mb-4`;
    
    const imageUrl = product.imageUrl || 'https://picsum.photos/300/200?text=商品图片';
    const price = formatPrice(product.price);
    const stock = product.stock || 0;
    const stockStatus = stock > 0 ? '有货' : '缺货';
    const stockClass = stock > 0 ? 'text-success' : 'text-danger';
    
    productDiv.innerHTML = `
        <div class="card h-100 product-card">
            <img src="${imageUrl}" class="card-img-top" alt="${product.name}" 
                 style="height: 200px; object-fit: cover;"
                 onerror="this.src='https://picsum.photos/300/200?text=商品图片'">
            <div class="card-body d-flex flex-column">
                <h5 class="card-title">${product.name}</h5>
                <p class="card-text text-muted flex-grow-1">${product.description || '暂无描述'}</p>
                <div class="product-meta mb-2">
                    <small class="text-muted">
                        <i class="fas fa-tag me-1"></i>${product.category || '未分类'}
                        <i class="fas fa-copyright ms-2 me-1"></i>${product.brand || '未知品牌'}
                    </small>
                </div>
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span class="fs-5 text-danger fw-bold">¥${price}</span>
                    <small class="${stockClass}">
                        <i class="fas fa-box me-1"></i>${stockStatus}
                    </small>
                </div>
                <div class="d-grid gap-2">
                    <button class="btn btn-success btn-sm" onclick="addToCart(${product.id})">
                        <i class="fas fa-shopping-cart me-1"></i>加入购物车
                    </button>
                    <a href="../detail/detail.html?id=${product.id}" class="btn btn-outline-success btn-sm">
                        <i class="fas fa-eye me-1"></i>查看详情
                    </a>
                </div>
            </div>
        </div>
    `;
    
    return productDiv;
}

// 显示分页
function displayPagination() {
    const paginationContainer = document.getElementById('pagination');
    if (!paginationContainer || totalPages <= 1) {
        if (paginationContainer) {
            paginationContainer.innerHTML = '';
        }
        return;
    }
    
    let paginationHTML = '<nav aria-label="搜索结果分页"><ul class="pagination justify-content-center">';
    
    // 上一页按钮
    if (currentPage > 0) {
        paginationHTML += `<li class="page-item">
            <a class="page-link" href="#" onclick="goToPage(${currentPage - 1})">
                <i class="fas fa-chevron-left"></i> 上一页
            </a>
        </li>`;
    }
    
    // 页码按钮
    const startPage = Math.max(0, currentPage - 2);
    const endPage = Math.min(totalPages - 1, currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        const activeClass = i === currentPage ? 'active' : '';
        paginationHTML += `<li class="page-item ${activeClass}">
            <a class="page-link" href="#" onclick="goToPage(${i})">${i + 1}</a>
        </li>`;
    }
    
    // 下一页按钮
    if (currentPage < totalPages - 1) {
        paginationHTML += `<li class="page-item">
            <a class="page-link" href="#" onclick="goToPage(${currentPage + 1})">
                下一页 <i class="fas fa-chevron-right"></i>
            </a>
        </li>`;
    }
    
    paginationHTML += '</ul></nav>';
    paginationContainer.innerHTML = paginationHTML;
}

// 跳转到指定页面
async function goToPage(page) {
    currentPage = page;
    await performSearch();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// 设置视图模式
function setView(view) {
    currentView = view;
    const productList = document.getElementById('productList');
    const viewGrid = document.getElementById('viewGrid');
    const viewList = document.getElementById('viewList');
    
    if (productList) {
        if (view === 'list') {
            productList.className = 'row g-4 list-view';
        } else {
            productList.className = 'row g-4';
        }
    }
    
    // 更新按钮状态
    if (viewGrid && viewList) {
        if (view === 'grid') {
            viewGrid.classList.add('active');
            viewList.classList.remove('active');
        } else {
            viewList.classList.add('active');
            viewGrid.classList.remove('active');
        }
    }
}

// 显示加载状态
function showLoading() {
    const productList = document.getElementById('productList');
    if (productList) {
        productList.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="spinner-border text-success" role="status">
                    <span class="visually-hidden">加载中...</span>
                </div>
                <p class="mt-2 text-muted">正在搜索商品...</p>
            </div>
        `;
    }
}

// 隐藏加载状态
function hideLoading() {
    // 加载状态会在displaySearchResults中清除
}

// 显示错误信息
function showError(message) {
    const productList = document.getElementById('productList');
    if (productList) {
        productList.innerHTML = `
            <div class="col-12 text-center py-5">
                <i class="fas fa-exclamation-triangle fa-3x text-warning mb-3"></i>
                <h5 class="text-warning">搜索出错</h5>
                <p class="text-muted">${message}</p>
                <button class="btn btn-success" onclick="performSearch()">
                    <i class="fas fa-redo me-2"></i>重试
                </button>
            </div>
        `;
    }
}

// 加入购物车
async function addToCart(productId) {
    if (!isUserLoggedIn()) {
        showMessage('请先登录');
        return;
    }
    // 获取商品卡片信息
    const productCard = document.querySelector(`button[onclick="addToCart(${productId})"]`).closest('.product-card');
    const name = productCard.querySelector('.card-title').textContent;
    const price = parseFloat(productCard.querySelector('.fs-5.text-danger.fw-bold').textContent.replace('¥', ''));
    const image = productCard.querySelector('img').src;
    const stockText = productCard.querySelector('.product-meta + .d-flex small').textContent;
    const stock = stockText.includes('有货') ? 99 : 0;
    const product = {
        id: productId,
        name: name,
        price: price,
        image: image,
        stock: stock
    };
    try {
        const res = await window.CartAPI.addToCart(product, 1);
        if (res && res.id) {
            showMessage('商品已加入购物车', 'success');
            updateCartBadge();
        } else {
            showMessage(res.message || '加入购物车失败', 'error');
        }
    } catch (error) {
        showMessage('加入购物车失败，请稍后重试', 'error');
    }
}

// 查看商品详情
function viewProduct(productId) {
    window.location.href = `../detail/detail.html?id=${productId}`;
}

// 检查用户是否登录
function isUserLoggedIn() {
    return localStorage.getItem('token') !== null;
}

// 显示消息提示
function showMessage(message, type = 'info') {
    const alertClass = {
        'success': 'alert-success',
        'warning': 'alert-warning',
        'error': 'alert-danger',
        'info': 'alert-info'
    }[type] || 'alert-info';

    const alertHtml = `
        <div class="alert ${alertClass} alert-dismissible fade show position-fixed" 
             style="top: 20px; right: 20px; z-index: 10000; min-width: 300px;" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;

    const alertElement = document.createElement('div');
    alertElement.innerHTML = alertHtml;
    document.body.appendChild(alertElement.firstElementChild);

    // 自动消失
    setTimeout(() => {
        const alert = document.querySelector('.alert');
        if (alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }
    }, 3000);
}

// 退出登录
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    showMessage('已退出登录', 'success');
    window.location.reload();
}

// 设置登录弹框
function setupLoginModal() {
    // 这里可以添加登录弹框的逻辑
    console.log('登录弹框已设置');
}

// 显示登录弹框
function showLoginModal() {
    window.location.href = '../login/login.html';
}

// 隐藏登录弹框
function hideLoginModal() {
    // 实现隐藏登录弹框的逻辑
}

// 显示登录表单
function showLoginForm() {
    // 实现显示登录表单的逻辑
}

// 显示注册表单
function showRegisterForm() {
    // 实现显示注册表单的逻辑
}

// 处理登录
async function handleLogin(e) {
    e.preventDefault();
    // 实现登录逻辑
}

// 处理注册
async function handleRegister(e) {
    e.preventDefault();
    // 实现注册逻辑
}

// 更新购物车徽章
async function updateCartBadge() {
    if (window.CartAPI) {
        const cart = await window.CartAPI.getCart();
        const totalQuantity = cart.reduce((sum, item) => sum + item.quantity, 0);
        const cartBadge = document.getElementById('cartBadge');
        if (cartBadge) {
            cartBadge.textContent = totalQuantity;
            cartBadge.style.display = totalQuantity > 0 ? 'inline' : 'none';
        }
    }
}

// 更新面包屑导航
function updateBreadcrumb(text) {
    const breadcrumbItem = document.querySelector('.breadcrumb-item.active');
    if (breadcrumbItem) {
        breadcrumbItem.textContent = text;
    }
}

// 更新页面标题
function updatePageTitle(text) {
    document.title = `${text} - 农产品商城`;
} 