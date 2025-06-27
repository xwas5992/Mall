// 首页商品管理功能
class HomepageProductManager {
    constructor() {
        this.currentProduct = null;
        this.init();
    }

    init() {
        this.bindEvents();
        this.loadHomepageProducts();
        this.loadAvailableProducts();
    }

    bindEvents() {
        // 添加首页商品按钮
        const addHomepageProductBtn = document.getElementById('addHomepageProductBtn');
        if (addHomepageProductBtn) {
            addHomepageProductBtn.addEventListener('click', () => {
                this.showAddHomepageProductModal();
            });
        }

        // 保存首页商品按钮
        const saveHomepageProductBtn = document.getElementById('saveHomepageProductBtn');
        if (saveHomepageProductBtn) {
            saveHomepageProductBtn.addEventListener('click', () => {
                this.saveHomepageProduct();
            });
        }
    }

    // 加载当前首页商品
    async loadHomepageProducts() {
        try {
            const response = await fetch(`${window.API_CONFIG.product.homepageUrl}/products`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const products = await response.json();
            this.renderHomepageProducts(products);
        } catch (error) {
            console.error('加载首页商品失败:', error);
            this.showMessage('加载首页商品失败', 'error');
        }
    }

    // 加载可添加的商品
    async loadAvailableProducts() {
        try {
            const response = await fetch(`${window.API_CONFIG.product.homepageUrl}/products/available`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const products = await response.json();
            this.renderAvailableProducts(products);
        } catch (error) {
            console.error('加载可用商品失败:', error);
            this.showMessage('加载可用商品失败', 'error');
        }
    }

    // 渲染首页商品列表
    renderHomepageProducts(products) {
        const tbody = document.querySelector('#homepageProductsTable tbody');
        if (!tbody) return;

        if (products.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center">暂无首页商品</td></tr>';
            return;
        }

        tbody.innerHTML = products.map(product => `
            <tr>
                <td>
                    <input type="number" class="form-control form-control-sm" 
                           value="${product.homepageSortOrder || 0}" 
                           style="width: 60px;"
                           onchange="homepageProductManager.updateSortOrder(${product.id}, this.value)">
                </td>
                <td>
                    <img src="${product.imageUrl || 'https://via.placeholder.com/50x50?text=No+Image'}" 
                         alt="${product.name}" 
                         style="width: 50px; height: 50px; object-fit: cover;">
                </td>
                <td>${product.name}</td>
                <td>${product.homepageDisplayTitle || product.name}</td>
                <td>¥${product.price}</td>
                <td>
                    <span class="badge bg-success">展示中</span>
                </td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="homepageProductManager.editHomepageProduct(${product.id})">
                        <i class="fas fa-edit"></i> 编辑
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="homepageProductManager.removeFromHomepage(${product.id})">
                        <i class="fas fa-times"></i> 下架
                    </button>
                </td>
            </tr>
        `).join('');
    }

    // 渲染可用商品列表
    renderAvailableProducts(products) {
        const tbody = document.querySelector('#availableProductsTable tbody');
        if (!tbody) return;

        if (products.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center">暂无可用商品</td></tr>';
            return;
        }

        tbody.innerHTML = products.map(product => `
            <tr>
                <td>
                    <img src="${product.imageUrl || 'https://via.placeholder.com/50x50?text=No+Image'}" 
                         alt="${product.name}" 
                         style="width: 50px; height: 50px; object-fit: cover;">
                </td>
                <td>${product.name}</td>
                <td>¥${product.price}</td>
                <td>${product.stock}</td>
                <td>${product.category || '未分类'}</td>
                <td>
                    <button class="btn btn-sm btn-success" onclick="homepageProductManager.addToHomepage(${product.id})">
                        <i class="fas fa-plus"></i> 添加到首页
                    </button>
                </td>
            </tr>
        `).join('');
    }

    // 显示添加首页商品模态框
    showAddHomepageProductModal(product = null) {
        this.currentProduct = product;
        const modal = new bootstrap.Modal(document.getElementById('homepageProductModal'));
        
        if (product) {
            // 编辑模式
            document.getElementById('homepageProductName').value = product.name;
            document.getElementById('homepageDisplayTitle').value = product.homepageDisplayTitle || '';
            document.getElementById('homepageSortOrder').value = product.homepageSortOrder || 1;
            document.getElementById('homepageDisplayDescription').value = product.homepageDisplayDescription || '';
            document.getElementById('featuredOnHomepage').checked = product.featuredOnHomepage !== false;
            document.getElementById('homepageProductImage').src = product.imageUrl || 'https://via.placeholder.com/200x200?text=No+Image';
        } else {
            // 添加模式
            document.getElementById('homepageProductForm').reset();
            document.getElementById('homepageProductName').value = '';
            document.getElementById('homepageProductImage').src = 'https://via.placeholder.com/200x200?text=No+Image';
        }
        
        modal.show();
    }

    // 添加商品到首页
    async addToHomepage(productId) {
        try {
            // 先获取商品详情
            const productResponse = await fetch(`${window.API_CONFIG.product.baseUrl}/${productId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
                }
            });

            if (!productResponse.ok) {
                throw new Error(`HTTP ${productResponse.status}: ${productResponse.statusText}`);
            }

            const product = await productResponse.json();
            this.showAddHomepageProductModal(product);
        } catch (error) {
            console.error('获取商品详情失败:', error);
            this.showMessage('获取商品详情失败', 'error');
        }
    }

    // 编辑首页商品
    async editHomepageProduct(productId) {
        try {
            const response = await fetch(`${window.API_CONFIG.product.baseUrl}/${productId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const product = await response.json();
            this.showAddHomepageProductModal(product);
        } catch (error) {
            console.error('获取商品详情失败:', error);
            this.showMessage('获取商品详情失败', 'error');
        }
    }

    // 保存首页商品设置
    async saveHomepageProduct() {
        if (!this.currentProduct) {
            this.showMessage('请先选择商品', 'error');
            return;
        }

        const requestData = {
            productId: this.currentProduct.id,
            featuredOnHomepage: document.getElementById('featuredOnHomepage').checked,
            homepageSortOrder: parseInt(document.getElementById('homepageSortOrder').value),
            homepageDisplayTitle: document.getElementById('homepageDisplayTitle').value || null,
            homepageDisplayDescription: document.getElementById('homepageDisplayDescription').value || null
        };

        try {
            const response = await fetch(`${window.API_CONFIG.product.homepageUrl}/products/display-info`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
                },
                body: JSON.stringify(requestData)
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const result = await response.json();
            this.showMessage('保存成功', 'success');
            
            // 关闭模态框并刷新数据
            bootstrap.Modal.getInstance(document.getElementById('homepageProductModal')).hide();
            this.loadHomepageProducts();
            this.loadAvailableProducts();
        } catch (error) {
            console.error('保存首页商品设置失败:', error);
            this.showMessage('保存失败', 'error');
        }
    }

    // 更新排序顺序
    async updateSortOrder(productId, sortOrder) {
        try {
            const response = await fetch(`${window.API_CONFIG.product.homepageUrl}/products/${productId}/sort?sortOrder=${sortOrder}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            this.showMessage('排序更新成功', 'success');
            this.loadHomepageProducts();
        } catch (error) {
            console.error('更新排序失败:', error);
            this.showMessage('更新排序失败', 'error');
        }
    }

    // 从首页下架商品
    async removeFromHomepage(productId) {
        if (!confirm('确定要从首页下架这个商品吗？')) {
            return;
        }

        try {
            const response = await fetch(`${window.API_CONFIG.product.homepageUrl}/products/${productId}/feature`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            this.showMessage('商品已从首页下架', 'success');
            this.loadHomepageProducts();
            this.loadAvailableProducts();
        } catch (error) {
            console.error('下架商品失败:', error);
            this.showMessage('下架商品失败', 'error');
        }
    }

    // 显示消息
    showMessage(message, type = 'info') {
        // 创建消息提示
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type === 'error' ? 'danger' : type} alert-dismissible fade show position-fixed`;
        alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        document.body.appendChild(alertDiv);

        // 3秒后自动移除
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, 3000);
    }
}

// 初始化首页商品管理器
let homepageProductManager;
document.addEventListener('DOMContentLoaded', function() {
    homepageProductManager = new HomepageProductManager();
}); 