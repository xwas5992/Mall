/**
 * 统一的登录状态管理工具
 * 确保所有页面使用相同的登录状态管理方式
 */

class AuthManager {
    constructor() {
        this.tokenKey = 'token';
        this.userKey = 'user';
        this.init();
    }

    // 初始化
    init() {
        // 监听storage变化，实现跨页面同步
        window.addEventListener('storage', (e) => {
            if (e.key === this.tokenKey || e.key === this.userKey) {
                this.updateLoginStatus();
            }
        });
        
        // 验证现有token的有效性
        this.validateExistingToken();
    }

    // 验证现有token
    async validateExistingToken() {
        const token = this.getToken();
        if (token) {
            console.log('验证现有token...');
            try {
                const response = await fetch(`${window.API_CONFIG.auth.baseUrl}/validate`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + token
                    }
                });
                
                if (!response.ok) {
                    console.warn('Token验证失败，清除登录状态');
                    this.logout();
                } else {
                    console.log('Token验证成功');
                }
            } catch (error) {
                console.error('Token验证出错:', error);
                // 网络错误时不自动清除token，可能是临时网络问题
            }
        }
    }

    // 检查是否已登录
    isLoggedIn() {
        const token = this.getToken();
        const hasToken = !!token;
        console.log('检查登录状态:', hasToken ? '已登录' : '未登录');
        return hasToken;
    }

    // 获取用户信息
    getUser() {
        const userStr = localStorage.getItem(this.userKey);
        const user = userStr ? JSON.parse(userStr) : null;
        console.log('获取用户信息:', user);
        return user;
    }

    // 获取token
    getToken() {
        const token = localStorage.getItem(this.tokenKey);
        console.log('获取token:', token ? token.substring(0, 20) + '...' : 'null');
        return token;
    }

    // 设置登录状态
    setLogin(token, user) {
        console.log('设置登录状态:', { token: token.substring(0, 20) + '...', user });
        localStorage.setItem(this.tokenKey, token);
        localStorage.setItem(this.userKey, JSON.stringify(user));
        this.updateLoginStatus();
        
        // 触发自定义事件，通知其他组件登录状态变化
        window.dispatchEvent(new CustomEvent('loginStateChanged', {
            detail: { isLoggedIn: true, user: user }
        }));
    }

    // 清除登录状态
    logout() {
        console.log('清除登录状态');
        localStorage.removeItem(this.tokenKey);
        localStorage.removeItem(this.userKey);
        this.updateLoginStatus();
        
        // 触发自定义事件，通知其他组件登录状态变化
        window.dispatchEvent(new CustomEvent('loginStateChanged', {
            detail: { isLoggedIn: false, user: null }
        }));
    }

    // 更新页面登录状态显示
    updateLoginStatus() {
        const loginBtn = document.getElementById('loginBtn');
        if (!loginBtn) return;

        if (this.isLoggedIn()) {
            const user = this.getUser();
            loginBtn.innerHTML = `<i class="fas fa-user"></i> ${user?.nickname || user?.username || '用户'}`;
        } else {
            loginBtn.innerHTML = '<i class="fas fa-user"></i> 登录';
        }
    }

    // 显示用户菜单
    showUserMenu() {
        const user = this.getUser();
        if (!user) return;

        // 创建用户菜单弹窗
        const menuHtml = `
            <div class="user-menu">
                <div class="user-info">
                    <img src="https://img.jd.com/avatar.png" alt="头像" class="user-avatar">
                    <div class="user-details">
                        <div class="user-name">${user.nickname || user.username}</div>
                    </div>
                </div>
                <div class="menu-items">
                    <div class="menu-item" onclick="authManager.goToProfile()">
                        <i class="fas fa-user-circle"></i>
                        <span>个人中心</span>
                    </div>
                    <div class="menu-item" onclick="authManager.goToOrders()">
                        <i class="fas fa-shopping-bag"></i>
                        <span>我的订单</span>
                    </div>
                    <div class="menu-item" onclick="authManager.goToCart()">
                        <i class="fas fa-shopping-cart"></i>
                        <span>购物车</span>
                    </div>
                    <div class="menu-item" onclick="authManager.goToFavorites()">
                        <i class="fas fa-heart"></i>
                        <span>我的收藏</span>
                    </div>
                    <div class="menu-divider"></div>
                    <div class="menu-item logout" onclick="authManager.logout()">
                        <i class="fas fa-sign-out-alt"></i>
                        <span>退出登录</span>
                    </div>
                </div>
            </div>
        `;

        // 显示菜单
        this.showModal(menuHtml);
    }

    // 显示登录弹窗
    showLoginModal() {
        const modalHtml = `
            <div class="auth-modal">
                <div class="modal-header">
                    <h2>用户登录</h2>
                    <button class="modal-close" onclick="authManager.closeModal()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                
                <div class="modal-body">
                    <form id="loginForm" class="auth-form">
                        <div class="form-group">
                            <label for="username">用户名</label>
                            <input type="text" id="username" required>
                        </div>
                        <div class="form-group">
                            <label for="password">密码</label>
                            <input type="password" id="password" required>
                        </div>
                        <button type="submit" class="btn btn-primary">登录</button>
                        <p class="form-switch">
                            还没有账号？<a href="#" onclick="authManager.showRegisterForm()">立即注册</a>
                        </p>
                    </form>
                    
                    <form id="registerForm" class="auth-form" style="display: none;">
                        <div class="form-group">
                            <label for="regUsername">用户名</label>
                            <input type="text" id="regUsername" required>
                        </div>
                        <div class="form-group">
                            <label for="regPassword">密码</label>
                            <input type="password" id="regPassword" required>
                        </div>
                        <div class="form-group">
                            <label for="confirmPassword">确认密码</label>
                            <input type="password" id="confirmPassword" required>
                        </div>
                        <button type="submit" class="btn btn-primary">注册</button>
                        <p class="form-switch">
                            已有账号？<a href="#" onclick="authManager.showLoginForm()">立即登录</a>
                        </p>
                    </form>
                </div>
            </div>
        `;

        this.showModal(modalHtml);
        this.setupAuthForms();
    }

    // 设置认证表单事件
    setupAuthForms() {
        const loginForm = document.getElementById('loginForm');
        const registerForm = document.getElementById('registerForm');

        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        if (registerForm) {
            registerForm.addEventListener('submit', (e) => this.handleRegister(e));
        }
    }

    // 处理登录
    async handleLogin(e) {
        e.preventDefault();
        
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        if (!username || !password) {
            this.showMessage('请填写完整的登录信息', 'error');
            return;
        }

        try {
            console.log('尝试登录:', username);
            const response = await fetch(`${window.API_CONFIG.auth.baseUrl}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            console.log('登录响应状态:', response.status);

            if (response.ok) {
                const data = await response.json();
                console.log('登录成功:', data);
                this.setLogin(data.token, data.user);
                this.closeModal();
                this.showMessage('登录成功', 'success');
                
                // 刷新页面或更新UI
                if (window.location.pathname.includes('login.html')) {
                    window.location.href = '../home/index.html';
                } else {
                    window.location.reload();
                }
            } else {
                const errorData = await response.json();
                console.error('登录失败:', errorData);
                this.showMessage(errorData.message || '登录失败', 'error');
            }
        } catch (error) {
            console.error('登录网络错误:', error);
            this.showMessage('网络错误，请稍后重试', 'error');
        }
    }

    // 处理注册
    async handleRegister(e) {
        e.preventDefault();
        
        const username = document.getElementById('regUsername').value;
        const password = document.getElementById('regPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (!username || !password || !confirmPassword) {
            this.showMessage('请填写完整的注册信息', 'error');
            return;
        }

        if (password !== confirmPassword) {
            this.showMessage('两次输入的密码不一致', 'error');
            return;
        }

        try {
            console.log('尝试注册:', username);
            const response = await fetch(`${window.API_CONFIG.auth.baseUrl}/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            console.log('注册响应状态:', response.status);

            if (response.ok) {
                const data = await response.json();
                console.log('注册成功:', data);
                this.showMessage('注册成功，请登录', 'success');
                this.showLoginForm();
            } else {
                const errorData = await response.json();
                console.error('注册失败:', errorData);
                this.showMessage(errorData.message || '注册失败', 'error');
            }
        } catch (error) {
            console.error('注册网络错误:', error);
            this.showMessage('网络错误，请稍后重试', 'error');
        }
    }

    // 显示登录表单
    showLoginForm() {
        document.getElementById('loginForm').style.display = 'block';
        document.getElementById('registerForm').style.display = 'none';
    }

    // 显示注册表单
    showRegisterForm() {
        document.getElementById('loginForm').style.display = 'none';
        document.getElementById('registerForm').style.display = 'block';
    }

    // 显示模态框
    showModal(content) {
        // 移除现有的模态框
        const existingModal = document.getElementById('authModal');
        if (existingModal) {
            existingModal.remove();
        }

        // 创建新的模态框
        const modal = document.createElement('div');
        modal.id = 'authModal';
        modal.className = 'modal-overlay';
        modal.innerHTML = `
            <div class="modal-content">
                ${content}
            </div>
        `;

        document.body.appendChild(modal);

        // 点击背景关闭模态框
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                this.closeModal();
            }
        });
    }

    // 关闭模态框
    closeModal() {
        const modal = document.getElementById('authModal');
        if (modal) {
            modal.remove();
        }
    }

    // 显示消息
    showMessage(message, type = 'info') {
        // 创建消息元素
        const messageEl = document.createElement('div');
        messageEl.className = `message message-${type}`;
        messageEl.textContent = message;

        // 添加到页面
        document.body.appendChild(messageEl);

        // 3秒后自动移除
        setTimeout(() => {
            if (messageEl.parentNode) {
                messageEl.parentNode.removeChild(messageEl);
            }
        }, 3000);
    }

    // 跳转到个人中心
    goToProfile() {
        this.closeModal();
        window.location.href = '../portal/portal.html';
    }

    // 跳转到订单页面
    goToOrders() {
        this.closeModal();
        this.showMessage('订单功能开发中...', 'info');
    }

    // 跳转到购物车
    goToCart() {
        this.closeModal();
        window.location.href = '../cart/cart.html';
    }

    // 跳转到收藏页面
    goToFavorites() {
        this.closeModal();
        this.showMessage('收藏功能开发中...', 'info');
    }
}

// 全局函数
function showLoginModal() {
    if (window.authManager) {
        window.authManager.showLoginModal();
    }
}

function showUserMenu() {
    if (window.authManager) {
        window.authManager.showUserMenu();
    }
}

function logout() {
    if (window.authManager) {
        window.authManager.logout();
        window.location.reload();
    }
} 