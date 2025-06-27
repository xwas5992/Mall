document.addEventListener('DOMContentLoaded', function() {
  // 分类、轮播、商品数据（可后续对接后端）
  const categories = [
    { id: 1, name: '新鲜水果' },
    { id: 2, name: '有机蔬菜' },
    { id: 3, name: '海鲜水产' },
    { id: 4, name: '肉禽蛋品' },
    { id: 5, name: '粮油调味' },
    { id: 6, name: '休闲零食' }
  ];
  const sliderList = [
    { id: 1, img: 'https://img1.imgtp.com/2023/07/10/1.jpg', title: '热卖水果' },
    { id: 2, img: 'https://img1.imgtp.com/2023/07/10/2.jpg', title: '有机蔬菜' },
    { id: 3, img: 'https://img1.imgtp.com/2023/07/10/3.jpg', title: '优质粮油' },
  ];
  const productList = document.getElementById('product-list');

  // 登录注册/我的弹框逻辑
  let isLogin = localStorage.getItem('token') !== null;
  const loginArea = document.getElementById('loginArea');
  
  function renderLoginArea() {
    if (!loginArea) return;
    
    loginArea.innerHTML = '';
    if (!isLogin) {
      const loginLink = document.createElement('a');
      loginLink.className = 'nav-link';
      loginLink.href = '../login/login.html';
      loginLink.innerHTML = '<i class="fas fa-sign-in-alt me-1"></i>登录/注册';
      loginArea.appendChild(loginLink);

    } else {
      const user = JSON.parse(localStorage.getItem('user') || '{}');
      const userMenu = document.createElement('div');
      userMenu.className = 'dropdown';
      userMenu.innerHTML = `
        <a href="#" class="nav-link dropdown-toggle" id="userDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            <i class="fas fa-user"></i> ${user.nickname || user.username}
        </a>
        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
            <li><a class="dropdown-item" href="../portal/portal.html"><i class="fas fa-user-circle me-2"></i>个人中心</a></li>
            <li><a class="dropdown-item" href="#"><i class="fas fa-shopping-bag me-2"></i>我的订单</a></li>
            <li><a class="dropdown-item" href="../cart/cart.html"><i class="fas fa-shopping-cart me-2"></i>购物车</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item text-danger" href="#" onclick="logout()"><i class="fas fa-sign-out-alt me-2"></i>退出登录</a></li>
        </ul>
      `;
      loginArea.appendChild(userMenu);
    }
  }
  
  renderLoginArea();

  // 搜索功能
  const searchInput = document.getElementById('searchInput');
  const searchBtn = document.getElementById('searchBtn');
  if (searchBtn && searchInput) {
    searchBtn.onclick = function() {
      const val = searchInput.value.trim();
      if (val) {
        // 跳转到搜索页面
        window.location.href = `../search/search.html?keyword=${encodeURIComponent(val)}`;
      }
    };
    
    // 回车搜索
    searchInput.onkeypress = function(e) {
      if (e.key === 'Enter') {
        const val = searchInput.value.trim();
        if (val) {
          window.location.href = `../search/search.html?keyword=${encodeURIComponent(val)}`;
        }
      }
    };
  }

  // 轮播滑窗
  let sliderIndex = 0;
  const slider = document.getElementById('slider');
  
  function renderSlider() {
    if (!slider) return;
    
    // 移除已有item
    slider.querySelectorAll('.slider-item').forEach(e => e.remove());
    // 当前只显示一个
    sliderList.forEach((item, idx) => {
      const div = document.createElement('div');
      div.className = 'slider-item' + (idx === sliderIndex ? ' active' : '');
      div.innerHTML = `<img class="slider-img" src="${item.img}" alt="${item.title}"><div class="slider-title">${item.title}</div>`;
      slider.appendChild(div);
    });
  }
  
  if (slider) {
    renderSlider();
    // 自动轮播，每15秒切换下一张
    setInterval(() => {
      sliderIndex = (sliderIndex + 1) % sliderList.length;
      renderSlider();
    }, 15000);
    
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    
    if (prevBtn) {
      prevBtn.onclick = function() {
        sliderIndex = (sliderIndex - 1 + sliderList.length) % sliderList.length;
        renderSlider();
      };
    }
    
    if (nextBtn) {
      nextBtn.onclick = function() {
        sliderIndex = (sliderIndex + 1) % sliderList.length;
        renderSlider();
      };
    }
  }

  // 商品分类展示 - 优化后的数据获取逻辑
  const categorySectionsContainer = document.getElementById('category-sections');
  if (categorySectionsContainer) {
    // 格式化价格显示（与搜索页保持一致）
    function formatPrice(price) {
      if (price === null || price === undefined) {
        return '0.00';
      }
      const numPrice = typeof price === 'string' ? parseFloat(price) : price;
      return isNaN(numPrice) ? '0.00' : numPrice.toFixed(2);
    }

    // 获取分类商品数据（参考搜索页的API调用方式）
    async function fetchProductsForCategory(categoryId) {
      try {
        console.log(`正在获取分类ID "${categoryId}" 的商品...`);
        
        // 构建查询参数（与搜索页保持一致）
        const params = new URLSearchParams();
        params.append('categoryId', categoryId);
        params.append('page', 0);
        params.append('size', 8);
        
        const apiUrl = `${window.API_CONFIG.product.baseUrl}/search/filters?${params.toString()}`;
        console.log(`API地址: ${apiUrl}`);
        
        const response = await fetch(apiUrl, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        });
        
        console.log(`响应状态: ${response.status}`);
        
        if (!response.ok) {
          const errorText = await response.text();
          console.error(`API错误响应: ${errorText}`);
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const data = await response.json();
        console.log(`分类ID "${categoryId}" 的商品数据:`, data);
        
        // 处理分页数据结构（与搜索页保持一致）
        const products = data.content || data || [];
        console.log(`处理后的商品数组:`, products);
        
        return products;
      } catch (error) {
        console.error(`获取分类ID "${categoryId}" 商品失败:`, error);
        // 返回模拟数据作为备用
        return generateMockProducts(categoryId);
      }
    }

    // 生成模拟商品数据
    function generateMockProducts(categoryId) {
      const categoryNames = {
        1: '新鲜水果',
        2: '有机蔬菜', 
        3: '海鲜水产',
        4: '肉禽蛋品',
        5: '粮油调味',
        6: '休闲零食'
      };
      
      const categoryName = categoryNames[categoryId] || '其他商品';
      const mockProducts = [];
      
      for (let i = 1; i <= 4; i++) {
        mockProducts.push({
          id: `${categoryId}_${i}`,
          name: `${categoryName}示例${i}`,
          description: `这是${categoryName}的示例商品描述`,
          price: Math.floor(Math.random() * 100) + 10,
          imageUrl: `https://picsum.photos/300/200?random=${categoryId}${i}`,
          category: categoryName,
          brand: '示例品牌',
          stock: Math.floor(Math.random() * 100) + 10
        });
      }
      
      return mockProducts;
    }

    function createProductCard(product) {
      const price = formatPrice(product.price);
      const imageUrl = product.imageUrl || `https://picsum.photos/300/200?random=${Math.floor(Math.random() * 1000)}`;
      const description = product.description || '暂无描述';
      const stock = product.stock || 0;
      const stockStatus = stock > 0 ? '有货' : '缺货';
      const stockClass = stock > 0 ? 'text-success' : 'text-danger';
      
      return `
        <div class="col-md-6 col-lg-3 mb-4">
          <div class="card h-100 product-card">
            <img src="${imageUrl}" class="card-img-top" alt="${product.name}" 
                 style="height: 200px; object-fit: cover;"
                 onerror="this.src='https://picsum.photos/300/200?text=商品图片'">
            <div class="card-body d-flex flex-column">
              <h5 class="card-title">${product.name}</h5>
              <p class="card-text text-muted flex-grow-1">${description}</p>
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
                <button class="btn btn-success btn-sm" onclick="addToCartFromHome(${product.id}, '${product.name}', ${product.price}, '${imageUrl}', ${stock})">
                  <i class="fas fa-shopping-cart me-1"></i>加入购物车
                </button>
                <a href="../detail/detail.html?id=${product.id}" class="btn btn-outline-success btn-sm">
                  <i class="fas fa-eye me-1"></i>查看详情
                </a>
              </div>
            </div>
          </div>
        </div>
      `;
    }

    // 加载分类商品数据
    async function loadProductsByCategory() {
      categorySectionsContainer.innerHTML = '';
      
      // 显示加载状态
      categorySectionsContainer.innerHTML = `
        <div class="text-center py-5">
          <div class="spinner-border text-success" role="status">
            <span class="visually-hidden">加载中...</span>
          </div>
          <p class="mt-3 text-muted">正在加载商品数据...</p>
        </div>
      `;
      
      const sections = [];
      
      for (const category of categories) {
        try {
          const products = await fetchProductsForCategory(category.id);
          
          // 如果没有商品，跳过这个分类
          if (!products || products.length === 0) {
            console.log(`分类 "${category.name}" 没有商品数据`);
            continue;
          }
          
          const section = `
            <section class="mb-5">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="category-title">${category.name}</h2>
                <a href="../search/search.html?category=${category.id}" class="btn btn-outline-primary">
                  了解更多 <i class="fas fa-arrow-right"></i>
                </a>
              </div>
              <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
                ${products.slice(0, 8).map(product => createProductCard(product)).join('')}
              </div>
            </section>
          `;
          
          sections.push(section);
        } catch (error) {
          console.error(`加载分类 "${category.name}" 商品失败:`, error);
          // 即使出错也显示分类标题，但显示错误信息
          const errorSection = `
            <section class="mb-5">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="category-title">${category.name}</h2>
                <a href="../search/search.html?category=${category.id}" class="btn btn-outline-primary">
                  了解更多 <i class="fas fa-arrow-right"></i>
                </a>
              </div>
              <div class="alert alert-warning" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                该分类商品加载失败，请稍后重试
              </div>
            </section>
          `;
          sections.push(errorSection);
        }
      }
      
      // 更新容器内容
      if (sections.length > 0) {
        categorySectionsContainer.innerHTML = sections.join('');
      } else {
        categorySectionsContainer.innerHTML = `
          <div class="text-center py-5">
            <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
            <h4 class="text-muted">暂无商品数据</h4>
            <p class="text-muted">请稍后重试或联系客服</p>
          </div>
        `;
      }
    }
    
    // 启动加载
    loadProductsByCategory();
  }

  // 全局函数：查看分类更多商品
  window.viewMoreCategory = function(categoryId, categoryName) {
    // 跳转到搜索页面，传递分类参数
    window.location.href = `../search/search.html?category=${categoryId}&keyword=${encodeURIComponent(categoryName)}`;
  };

  // Back to Top button logic
  const backToTopButton = document.getElementById('back-to-top');

  if (backToTopButton) {
    window.addEventListener('scroll', () => {
      if (window.pageYOffset > 100) { // Show button after scrolling 100px
        backToTopButton.style.display = 'flex';
      } else {
        backToTopButton.style.display = 'none';
      }
    });

    backToTopButton.addEventListener('click', (e) => {
      e.preventDefault();
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      });
    });

    // Initial check in case the page is already scrolled
    if (window.pageYOffset > 100) {
      backToTopButton.style.display = 'flex';
    }
  }
});

// 初始化页面
function initializePage() {
  // 初始化Bootstrap轮播图
  const carousel = new bootstrap.Carousel(document.getElementById('heroCarousel'), {
    interval: 5000,
    wrap: true
  });
}

// 设置事件监听器
function setupEventListeners() {
  // 搜索功能
  const searchBtn = document.getElementById('searchBtn');
  const searchInput = document.getElementById('searchInput');
  
  if (searchBtn && searchInput) {
    searchBtn.addEventListener('click', performSearch);
    searchInput.addEventListener('keypress', function(e) {
      if (e.key === 'Enter') {
        performSearch();
      }
    });
  }

  // 回到顶部按钮
  const backToTopBtn = document.getElementById('backToTop');
  if (backToTopBtn) {
    window.addEventListener('scroll', function() {
      if (window.pageYOffset > 300) {
        backToTopBtn.style.display = 'block';
      } else {
        backToTopBtn.style.display = 'none';
      }
    });

    backToTopBtn.addEventListener('click', function() {
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      });
    });
  }
}

// 渲染分类导航
function renderCategoryNav() {
  const categoryNav = document.getElementById('categoryNav');
  if (!categoryNav) return;

  categoryNav.innerHTML = categories.map(category => `
    <div class="col-md-4 col-lg-2">
      <a href="../search/search.html?category=${category.id}" class="category-card">
        <i class="${category.icon}"></i>
        <h5>${category.name}</h5>
        <p>${category.description}</p>
      </a>
    </div>
  `).join('');
}

// 渲染商品分类展示
function renderProductSections() {
  const categorySections = document.getElementById('categorySections');
  if (!categorySections) return;

  categorySections.innerHTML = categories.map(category => {
    const categoryProducts = products.filter(p => p.category === category.id).slice(0, 4);
    
    if (categoryProducts.length === 0) return '';

    return `
      <div class="category-section" id="category-${category.id}">
        <div class="category-header">
          <h3 class="category-title">${category.name}</h3>
          <a href="../search/search.html?category=${category.id}" class="more-link">
            查看更多 <i class="fas fa-arrow-right"></i>
          </a>
        </div>
        <div class="row g-4">
          ${categoryProducts.map(product => createProductCard(product)).join('')}
        </div>
      </div>
    `;
  }).join('');
}

// 获取分类名称
function getCategoryName(categoryId) {
  const category = categories.find(c => c.id === categoryId);
  return category ? category.name : '其他';
}

// 搜索功能
function performSearch() {
  const searchInput = document.getElementById('searchInput');
  const keyword = searchInput.value.trim();
  
  if (keyword) {
    window.location.href = `../search/search.html?keyword=${encodeURIComponent(keyword)}`;
  } else {
    showMessage('请输入搜索关键词', 'warning');
  }
}

// 从首页加入购物车
async function addToCartFromHome(productId, productName, productPrice, productImage, productStock) {
  try {
    // 检查用户登录状态
    if (!isUserLoggedIn()) {
      showLoginModal();
      return;
    }

    // 构建商品信息
    const product = {
      id: productId,
      name: productName,
      price: productPrice,
      image: productImage,
      stock: productStock
    };

    // 添加到购物车（后端）
    const res = await window.CartAPI.addToCart(product, 1);
    if (res && res.id) {
      showMessage('商品已加入购物车', 'success');
      updateCartBadge();
    } else {
      showMessage(res.message || '加入购物车失败', 'error');
    }
  } catch (error) {
    console.error('加入购物车失败:', error);
    showMessage('加入购物车失败，请稍后重试', 'error');
  }
}

// 加入购物车（兼容旧版本）
function addToCart(productId) {
  addToCartFromHome(productId);
}

// 立即购买
function buyNow(productId) {
  if (!isUserLoggedIn()) {
    showLoginModal();
    return;
  }
  
  // 跳转到商品详情页进行购买
  window.location.href = `../detail/detail.html?id=${productId}`;
}

// 检查用户是否登录
function isUserLoggedIn() {
  return localStorage.getItem('token') !== null;
}

// 显示登录弹窗
function showLoginModal() {
  // 使用统一的登录管理
  if (typeof authManager !== 'undefined') {
    authManager.showLoginModal();
  } else {
    window.location.href = '../login/login.html';
  }
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

// 监听登录状态变化
window.addEventListener('storage', function(e) {
  if (e.key === 'token' || e.key === 'user') {
    isLogin = localStorage.getItem('token') !== null;
    renderLoginArea();
  }
});

// 监听购物车更新事件
window.addEventListener('cartUpdated', function(e) {
  updateCartBadge();
});

// 平滑滚动到指定元素
function scrollToElement(elementId) {
  const element = document.getElementById(elementId);
  if (element) {
    element.scrollIntoView({
      behavior: 'smooth',
      block: 'start'
    });
  }
}

// 页面加载完成后初始化购物车徽章
document.addEventListener('DOMContentLoaded', function() {
  updateCartBadge();
});
