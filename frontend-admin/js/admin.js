// 管理员界面JavaScript
class AdminPanel {
    constructor() {
        this.currentPage = 'dashboard';
        this.init();
    }

    init() {
        this.bindEvents();
        this.loadDashboard();
        this.checkAuth();
    }

    bindEvents() {
        // 侧边栏导航
        document.querySelectorAll('.sidebar-nav .nav-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const page = link.getAttribute('data-page');
                this.navigateToPage(page);
            });
        });

        // 侧边栏切换
        document.getElementById('sidebarToggle').addEventListener('click', () => {
            document.querySelector('.sidebar').classList.toggle('show');
        });

        // 退出登录
        document.getElementById('logoutBtn').addEventListener('click', (e) => {
            e.preventDefault();
            this.logout();
        });

        // 添加商品按钮
        document.getElementById('addProductBtn')?.addEventListener('click', () => {
            this.showAddProductModal();
        });

        // 添加分类按钮
        document.getElementById('addCategoryBtn')?.addEventListener('click', () => {
            this.showAddCategoryModal();
        });

        // 保存商品
        document.getElementById('saveProductBtn')?.addEventListener('click', () => {
            this.saveProduct();
        });

        // 保存分类
        document.getElementById('saveCategoryBtn')?.addEventListener('click', () => {
            this.saveCategory();
        });

        // 设置表单提交
        document.getElementById('settingsForm')?.addEventListener('submit', (e) => {
            e.preventDefault();
            this.saveSettings();
        });
    }

    // 页面导航
    navigateToPage(page) {
        // 隐藏所有页面
        document.querySelectorAll('.page-content').forEach(content => {
            content.classList.remove('active');
        });

        // 移除所有导航项的active状态
        document.querySelectorAll('.sidebar-nav .nav-link').forEach(link => {
            link.classList.remove('active');
        });

        // 显示目标页面
        const targetPage = document.getElementById(page);
        if (targetPage) {
            targetPage.classList.add('active');
        }

        // 激活对应的导航项
        const activeLink = document.querySelector(`[data-page="${page}"]`);
        if (activeLink) {
            activeLink.classList.add('active');
        }

        // 更新页面标题
        const pageTitle = document.getElementById('pageTitle');
        const titles = {
            dashboard: '控制台',
            products: '商品管理',
            users: '用户管理',
            orders: '订单管理',
            categories: '分类管理',
            reports: '统计报表',
            settings: '系统设置'
        };
        pageTitle.textContent = titles[page] || '控制台';

        // 加载页面数据
        this.currentPage = page;
        this.loadPageData(page);
    }

    // 加载页面数据
    loadPageData(page) {
        switch (page) {
            case 'dashboard':
                this.loadDashboard();
                break;
            case 'products':
                this.loadProducts();
                break;
            case 'users':
                this.loadUsers();
                break;
            case 'orders':
                this.loadOrders();
                break;
            case 'categories':
                this.loadCategories();
                break;
            case 'reports':
                this.loadReports();
                break;
        }
    }

    // 检查认证状态
    async checkAuth() {
        const token = localStorage.getItem('adminToken');
        if (!token) {
            window.location.href = 'login.html';
            return;
        }

        try {
            const response = await fetch(`${window.API_CONFIG.auth.baseUrl}/verify`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    ...window.API_FETCH_CONFIG.headers
                }
            });

            if (!response.ok) {
                throw new Error('认证失败');
            }

            const data = await response.json();
            if (data.role !== 'ADMIN') {
                throw new Error('权限不足');
            }

            // 更新管理员信息
            document.getElementById('adminName').textContent = data.username || '管理员';
        } catch (error) {
            console.error('认证检查失败:', error);
            this.showAlert('认证失败，请重新登录', 'danger');
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
        }
    }

    // 加载控制台数据
    async loadDashboard() {
        try {
            // 加载统计数据
            await this.loadStatistics();
            
            // 加载最近订单
            await this.loadRecentOrders();
            
            // 检查系统状态
            await this.checkSystemStatus();
        } catch (error) {
            console.error('加载控制台数据失败:', error);
            this.showAlert('加载控制台数据失败', 'danger');
        }
    }

    // 加载统计数据
    async loadStatistics() {
        try {
            const token = localStorage.getItem('adminToken');
            
            // 获取用户统计
            const usersResponse = await fetch(`${window.API_CONFIG.auth.baseUrl}/users/count`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    ...window.API_FETCH_CONFIG.headers
                }
            });
            const usersCount = usersResponse.ok ? await usersResponse.json() : 0;
            document.getElementById('totalUsers').textContent = usersCount;

            // 获取商品统计
            const productsResponse = await fetch(`${window.API_CONFIG.product.baseUrl}/count`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    ...window.API_FETCH_CONFIG.headers
                }
            });
            const productsCount = productsResponse.ok ? await productsResponse.json() : 0;
            document.getElementById('totalProducts').textContent = productsCount;

            // 模拟订单和收入数据（实际项目中应该从订单服务获取）
            document.getElementById('totalOrders').textContent = '156';
            document.getElementById('totalRevenue').textContent = '¥12,580';
        } catch (error) {
            console.error('加载统计数据失败:', error);
        }
    }

    // 加载最近订单
    async loadRecentOrders() {
        try {
            // 由于订单服务暂未实现，这里显示模拟数据
            const mockOrders = [
                {
                    orderNumber: 'ORD-2024-001',
                    username: 'user001',
                    totalAmount: 299.00,
                    status: 'COMPLETED',
                    createdAt: new Date()
                },
                {
                    orderNumber: 'ORD-2024-002',
                    username: 'user002',
                    totalAmount: 158.50,
                    status: 'PROCESSING',
                    createdAt: new Date(Date.now() - 86400000)
                },
                {
                    orderNumber: 'ORD-2024-003',
                    username: 'user003',
                    totalAmount: 89.99,
                    status: 'PENDING',
                    createdAt: new Date(Date.now() - 172800000)
                }
            ];

            const tbody = document.getElementById('recentOrdersTable');
            tbody.innerHTML = mockOrders.map(order => `
                <tr>
                    <td>${order.orderNumber}</td>
                    <td>${order.username}</td>
                    <td>¥${order.totalAmount}</td>
                    <td><span class="badge badge-${this.getStatusBadgeClass(order.status)}">${order.status}</span></td>
                    <td>${new Date(order.createdAt).toLocaleString()}</td>
                </tr>
            `).join('');
        } catch (error) {
            console.error('加载最近订单失败:', error);
            document.getElementById('recentOrdersTable').innerHTML = 
                '<tr><td colspan="5" class="text-center">暂无数据</td></tr>';
        }
    }

    // 检查系统状态
    async checkSystemStatus() {
        const services = [
            { name: 'auth', url: `${window.API_CONFIG.auth.baseUrl}/health`, element: 'authStatus' },
            { name: 'product', url: `${window.API_CONFIG.product.baseUrl}/health`, element: 'productStatus' },
            { name: 'user', url: `${window.API_CONFIG.user.baseUrl}/health`, element: 'userStatus' }
        ];

        for (const service of services) {
            try {
                const response = await fetch(service.url, {
                    headers: window.API_FETCH_CONFIG.headers
                });
                
                const element = document.getElementById(service.element);
                if (response.ok) {
                    element.textContent = '正常';
                    element.className = 'status-value text-success';
                } else {
                    element.textContent = '异常';
                    element.className = 'status-value text-danger';
                }
            } catch (error) {
                const element = document.getElementById(service.element);
                element.textContent = '异常';
                element.className = 'status-value text-danger';
            }
        }
    }

    // 加载商品列表
    async loadProducts() {
        try {
            const token = localStorage.getItem('adminToken');
            const response = await fetch(`${window.API_CONFIG.product.baseUrl}?page=0&size=20`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    ...window.API_FETCH_CONFIG.headers
                }
            });

            const data = response.ok ? await response.json() : { content: [] };
            const products = data.content || [];
            const tbody = document.querySelector('#productsTable tbody');
            
            if (products.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" class="text-center">暂无商品</td></tr>';
                return;
            }

            tbody.innerHTML = products.map(product => `
                <tr>
                    <td>${product.id}</td>
                    <td>
                        <img src="${product.imageUrl || '../frontend/libs/fontawesome/webfonts/fa-image.svg'}" 
                             alt="${product.name}" class="product-thumb">
                    </td>
                    <td>${product.name}</td>
                    <td>¥${product.price}</td>
                    <td>${product.stock}</td>
                    <td>${product.category || '未分类'}</td>
                    <td>
                        <span class="badge badge-${product.status ? 'success' : 'danger'}">
                            ${product.status ? '上架' : '下架'}
                        </span>
                    </td>
                    <td>
                        <div class="action-buttons">
                            <button class="btn btn-sm btn-primary" onclick="adminPanel.editProduct(${product.id})">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="adminPanel.deleteProduct(${product.id})">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        } catch (error) {
            console.error('加载商品列表失败:', error);
            this.showAlert('加载商品列表失败', 'danger');
        }
    }

    // 加载用户列表
    async loadUsers() {
        try {
            const token = localStorage.getItem('adminToken');
            const response = await fetch(`${window.API_CONFIG.auth.baseUrl}/users?page=0&size=20`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    ...window.API_FETCH_CONFIG.headers
                }
            });

            const data = response.ok ? await response.json() : { content: [] };
            const users = data.content || [];
            const tbody = document.querySelector('#usersTable tbody');
            
            if (users.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center">暂无用户</td></tr>';
                return;
            }

            tbody.innerHTML = users.map(user => `
                <tr>
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>
                        <span class="badge badge-${user.role === 'ADMIN' ? 'danger' : 'info'}">
                            ${user.role}
                        </span>
                    </td>
                    <td>${new Date(user.createdAt).toLocaleDateString()}</td>
                    <td>
                        <span class="badge badge-${user.enabled ? 'success' : 'warning'}">
                            ${user.enabled ? '正常' : '禁用'}
                        </span>
                    </td>
                    <td>
                        <div class="action-buttons">
                            <button class="btn btn-sm btn-warning" onclick="adminPanel.toggleUserStatus(${user.id})">
                                <i class="fas fa-ban"></i>
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="adminPanel.deleteUser(${user.id})">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        } catch (error) {
            console.error('加载用户列表失败:', error);
            this.showAlert('加载用户列表失败', 'danger');
        }
    }

    // 加载订单列表
    async loadOrders() {
        try {
            // 由于订单服务暂未实现，这里显示模拟数据
            const mockOrders = [
                {
                    id: 1,
                    orderNumber: 'ORD-2024-001',
                    username: 'user001',
                    productName: 'iPhone 15 Pro',
                    totalAmount: 299.00,
                    status: 'COMPLETED',
                    createdAt: new Date()
                },
                {
                    id: 2,
                    orderNumber: 'ORD-2024-002',
                    username: 'user002',
                    productName: 'MacBook Air',
                    totalAmount: 158.50,
                    status: 'PROCESSING',
                    createdAt: new Date(Date.now() - 86400000)
                }
            ];

            const tbody = document.querySelector('#ordersTable tbody');
            tbody.innerHTML = mockOrders.map(order => `
                <tr>
                    <td>${order.orderNumber}</td>
                    <td>${order.username}</td>
                    <td>${order.productName}</td>
                    <td>¥${order.totalAmount}</td>
                    <td>
                        <span class="badge badge-${this.getStatusBadgeClass(order.status)}">
                            ${order.status}
                        </span>
                    </td>
                    <td>${new Date(order.createdAt).toLocaleString()}</td>
                    <td>
                        <div class="action-buttons">
                            <button class="btn btn-sm btn-primary" onclick="adminPanel.viewOrder(${order.id})">
                                <i class="fas fa-eye"></i>
                            </button>
                            <button class="btn btn-sm btn-success" onclick="adminPanel.updateOrderStatus(${order.id})">
                                <i class="fas fa-check"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        } catch (error) {
            console.error('加载订单列表失败:', error);
            this.showAlert('加载订单列表失败', 'danger');
        }
    }

    // 加载分类列表
    async loadCategories() {
        try {
            const token = localStorage.getItem('adminToken');
            const response = await fetch(`${window.API_CONFIG.product.baseUrl}/categories`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    ...window.API_FETCH_CONFIG.headers
                }
            });

            const categories = response.ok ? await response.json() : [];
            const tbody = document.querySelector('#categoriesTable tbody');
            
            if (categories.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" class="text-center">暂无分类</td></tr>';
                return;
            }

            tbody.innerHTML = categories.map((category, index) => `
                <tr>
                    <td>${index + 1}</td>
                    <td>${category}</td>
                    <td>商品分类</td>
                    <td>0</td>
                    <td>
                        <div class="action-buttons">
                            <button class="btn btn-sm btn-primary" onclick="adminPanel.editCategory('${category}')">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="adminPanel.deleteCategory('${category}')">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        } catch (error) {
            console.error('加载分类列表失败:', error);
            this.showAlert('加载分类列表失败', 'danger');
        }
    }

    // 加载报表数据
    async loadReports() {
        try {
            const token = localStorage.getItem('adminToken');
            const response = await fetch(`${window.API_CONFIG.product.baseUrl}/statistics`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    ...window.API_FETCH_CONFIG.headers
                }
            });

            const statistics = response.ok ? await response.json() : {};
            
            // 这里可以集成Chart.js来显示图表
            console.log('商品统计数据:', statistics);
            
            // 显示统计信息
            this.displayStatistics(statistics);
        } catch (error) {
            console.error('加载报表数据失败:', error);
            this.showAlert('加载报表数据失败', 'danger');
        }
    }

    // 显示统计信息
    displayStatistics(statistics) {
        // 这里可以实现图表显示逻辑
        console.log('显示统计信息:', statistics);
    }

    // 显示添加商品模态框
    showAddProductModal() {
        const modal = new bootstrap.Modal(document.getElementById('addProductModal'));
        modal.show();
    }

    // 显示添加分类模态框
    showAddCategoryModal() {
        const modal = new bootstrap.Modal(document.getElementById('addCategoryModal'));
        modal.show();
    }

    // 保存商品
    async saveProduct() {
        try {
            const formData = {
                name: document.getElementById('productName').value,
                price: parseFloat(document.getElementById('productPrice').value),
                stock: parseInt(document.getElementById('productStock').value),
                category: document.getElementById('productCategory').value,
                imageUrl: document.getElementById('productImage').value,
                description: document.getElementById('productDescription').value,
                brand: '默认品牌',
                categoryId: 1,
                status: true
            };

            const token = localStorage.getItem('adminToken');
            const response = await fetch(`${window.API_CONFIG.product.baseUrl}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    ...window.API_FETCH_CONFIG.headers
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                this.showAlert('商品添加成功', 'success');
                bootstrap.Modal.getInstance(document.getElementById('addProductModal')).hide();
                this.loadProducts();
            } else {
                throw new Error('添加商品失败');
            }
        } catch (error) {
            console.error('保存商品失败:', error);
            this.showAlert('保存商品失败', 'danger');
        }
    }

    // 保存分类
    async saveCategory() {
        try {
            const categoryName = document.getElementById('categoryName').value;
            const categoryDescription = document.getElementById('categoryDescription').value;

            this.showAlert('分类添加成功', 'success');
            bootstrap.Modal.getInstance(document.getElementById('addCategoryModal')).hide();
            this.loadCategories();
        } catch (error) {
            console.error('保存分类失败:', error);
            this.showAlert('保存分类失败', 'danger');
        }
    }

    // 保存设置
    async saveSettings() {
        try {
            const settings = {
                mallName: document.getElementById('mallName').value,
                sessionTimeout: parseInt(document.getElementById('sessionTimeout').value),
                enableLogging: document.getElementById('enableLogging').checked
            };

            // 这里可以调用后端API保存设置
            console.log('保存设置:', settings);
            this.showAlert('设置保存成功', 'success');
        } catch (error) {
            console.error('保存设置失败:', error);
            this.showAlert('保存设置失败', 'danger');
        }
    }

    // 编辑商品
    editProduct(id) {
        console.log('编辑商品:', id);
        // 实现编辑商品功能
    }

    // 删除商品
    async deleteProduct(id) {
        if (!confirm('确定要删除这个商品吗？')) return;

        try {
            const token = localStorage.getItem('adminToken');
            const response = await fetch(`${window.API_CONFIG.product.baseUrl}/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    ...window.API_FETCH_CONFIG.headers
                }
            });

            if (response.ok) {
                this.showAlert('商品删除成功', 'success');
                this.loadProducts();
            } else {
                throw new Error('删除商品失败');
            }
        } catch (error) {
            console.error('删除商品失败:', error);
            this.showAlert('删除商品失败', 'danger');
        }
    }

    // 切换用户状态
    async toggleUserStatus(id) {
        try {
            const token = localStorage.getItem('adminToken');
            const response = await fetch(`${window.API_CONFIG.auth.baseUrl}/users/${id}/status?enabled=false`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    ...window.API_FETCH_CONFIG.headers
                }
            });

            if (response.ok) {
                this.showAlert('用户状态更新成功', 'success');
                this.loadUsers();
            } else {
                throw new Error('更新用户状态失败');
            }
        } catch (error) {
            console.error('更新用户状态失败:', error);
            this.showAlert('更新用户状态失败', 'danger');
        }
    }

    // 删除用户
    async deleteUser(id) {
        if (!confirm('确定要删除这个用户吗？')) return;

        try {
            const token = localStorage.getItem('adminToken');
            const response = await fetch(`${window.API_CONFIG.auth.baseUrl}/users/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    ...window.API_FETCH_CONFIG.headers
                }
            });

            if (response.ok) {
                this.showAlert('用户删除成功', 'success');
                this.loadUsers();
            } else {
                throw new Error('删除用户失败');
            }
        } catch (error) {
            console.error('删除用户失败:', error);
            this.showAlert('删除用户失败', 'danger');
        }
    }

    // 查看订单详情
    viewOrder(id) {
        console.log('查看订单:', id);
        // 实现查看订单详情功能
    }

    // 更新订单状态
    async updateOrderStatus(id) {
        try {
            this.showAlert('订单状态更新成功', 'success');
            this.loadOrders();
        } catch (error) {
            console.error('更新订单状态失败:', error);
            this.showAlert('更新订单状态失败', 'danger');
        }
    }

    // 编辑分类
    editCategory(category) {
        console.log('编辑分类:', category);
        // 实现编辑分类功能
    }

    // 删除分类
    async deleteCategory(category) {
        if (!confirm('确定要删除这个分类吗？')) return;

        try {
            this.showAlert('分类删除成功', 'success');
            this.loadCategories();
        } catch (error) {
            console.error('删除分类失败:', error);
            this.showAlert('删除分类失败', 'danger');
        }
    }

    // 获取状态标签样式
    getStatusBadgeClass(status) {
        const statusMap = {
            'PENDING': 'warning',
            'PROCESSING': 'info',
            'COMPLETED': 'success',
            'CANCELLED': 'danger'
        };
        return statusMap[status] || 'secondary';
    }

    // 显示提示信息
    showAlert(message, type = 'info') {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        const container = document.querySelector('.content-area');
        container.insertBefore(alertDiv, container.firstChild);

        // 自动消失
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, 5000);
    }

    // 退出登录
    logout() {
        localStorage.removeItem('adminToken');
        localStorage.removeItem('adminUser');
        window.location.href = 'login.html';
    }
}

// 初始化管理员面板
let adminPanel;
document.addEventListener('DOMContentLoaded', () => {
    adminPanel = new AdminPanel();
}); 