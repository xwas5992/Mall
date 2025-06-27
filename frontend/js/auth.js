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
    }

    // 检查是否已登录
    isLoggedIn() {
        return localStorage.getItem(this.tokenKey) !== null;
    }

    // 获取用户信息
    getUser() {
        const userStr = localStorage.getItem(this.userKey);
        return userStr ? JSON.parse(userStr) : null;
    }

    // 获取token
    getToken() {
        return localStorage.getItem(this.tokenKey);
    }

    // 设置登录状态
    setLogin(token, user) {
        localStorage.setItem(this.tokenKey, token);
        localStorage.setItem(this.userKey, JSON.stringify(user));
        this.updateLoginStatus();
    }

    // 清除登录状态
    logout() {
        localStorage.removeItem(this.tokenKey);
        localStorage.removeItem(this.userKey);
        this.updateLoginStatus();
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
                        <div class="user-email">${user.email || ''}</div>
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
            const response = await fetch(`${window.API_CONFIG.auth.baseUrl}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                const data = await response.json();
                this.setLogin(data.token, data.user);
                this.closeModal();
                this.showMessage('登录成功！', 'success');
            } else {
                const errorData = await response.json();
                this.showMessage(errorData.message || '登录失败，请检查用户名和密码', 'error');
            }
        } catch (error) {
            console.error('登录错误:', error);
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
            const response = await fetch(`${window.API_CONFIG.auth.baseUrl}/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ 
                    username, 
                    password,
                    email: username + '@example.com',
                    fullName: '用户' + username
                })
            });

            if (response.ok) {
                const data = await response.json();
                this.setLogin(data.token, data.user);
                this.closeModal();
                this.showMessage('注册成功！', 'success');
            } else {
                const errorData = await response.json();
                this.showMessage(errorData.message || '注册失败，请稍后重试', 'error');
            }
        } catch (error) {
            console.error('注册错误:', error);
            this.showMessage('网络错误，请稍后重试', 'error');
        }
    }

    // 显示登录表单
    showLoginForm() {
        const loginForm = document.getElementById('loginForm');
        const registerForm = document.getElementById('registerForm');
        
        if (loginForm) loginForm.style.display = 'block';
        if (registerForm) registerForm.style.display = 'none';
    }

    // 显示注册表单
    showRegisterForm() {
        const loginForm = document.getElementById('loginForm');
        const registerForm = document.getElementById('registerForm');
        
        if (loginForm) loginForm.style.display = 'none';
        if (registerForm) registerForm.style.display = 'block';
    }

    // 显示模态框
    showModal(content) {
        // 移除现有的模态框
        this.closeModal();

        const modal = document.createElement('div');
        modal.id = 'authModal';
        modal.className = 'modal show';
        modal.innerHTML = content;
        
        // 点击外部关闭
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                this.closeModal();
            }
        });

        document.body.appendChild(modal);
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
        const messageDiv = document.createElement('div');
        messageDiv.className = `message message-${type}`;
        messageDiv.textContent = message;
        messageDiv.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 12px 20px;
            border-radius: 4px;
            color: white;
            font-weight: bold;
            z-index: 10000;
            background: ${type === 'success' ? '#52c41a' : type === 'error' ? '#ff4d4f' : '#1890ff'};
        `;

        document.body.appendChild(messageDiv);

        setTimeout(() => {
            messageDiv.remove();
        }, 3000);
    }

    // 页面跳转方法
    goToProfile() {
        this.closeModal();
        // TODO: 跳转到个人中心页面
        this.showMessage('个人中心功能开发中...', 'info');
    }

    goToOrders() {
        this.closeModal();
        // TODO: 跳转到订单页面
        this.showMessage('订单页面功能开发中...', 'info');
    }

    goToCart() {
        this.closeModal();
        // TODO: 跳转到购物车页面
        this.showMessage('购物车功能开发中...', 'info');
    }

    goToFavorites() {
        this.closeModal();
        // TODO: 跳转到收藏页面
        this.showMessage('收藏功能开发中...', 'info');
    }
}

// 创建全局实例
const authManager = new AuthManager();

// 全局函数，供HTML调用
function showLoginModal() {
    authManager.showLoginModal();
}

function showUserMenu() {
    authManager.showUserMenu();
}

function logout() {
    authManager.logout();
    authManager.showMessage('已退出登录', 'success');
} 