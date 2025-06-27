// 登录注册页面逻辑
document.addEventListener('DOMContentLoaded', function() {
    initializeForm();
    setupEventListeners();
    setupPasswordToggles();
    setupFormValidation();
});

// 初始化表单
function initializeForm() {
    // 检查是否有保存的登录状态
    const savedUsername = localStorage.getItem('savedUsername');
    const rememberMe = localStorage.getItem('rememberMe');
    
    if (savedUsername && rememberMe === 'true') {
        document.getElementById('username').value = savedUsername;
        document.getElementById('rememberMe').checked = true;
    }
}

// 设置事件监听器
function setupEventListeners() {
    // 表单切换
    document.getElementById('toRegister').addEventListener('click', function(e) {
        e.preventDefault();
        switchToRegister();
    });
    
    document.getElementById('toLogin').addEventListener('click', function(e) {
        e.preventDefault();
        switchToLogin();
    });
    
    // 表单提交
    document.getElementById('loginForm').addEventListener('submit', handleLogin);
    document.getElementById('registerForm').addEventListener('submit', handleRegister);
    
    // 获取验证码
    document.getElementById('getCodeBtn').addEventListener('click', getVerificationCode);
    
    // 手机号输入验证
    document.getElementById('regPhone').addEventListener('input', validatePhone);
    
    // 密码确认验证
    document.getElementById('regConfirm').addEventListener('input', validatePasswordConfirm);
}

// 设置密码显示/隐藏切换
function setupPasswordToggles() {
    const togglePassword = document.getElementById('togglePassword');
    const password = document.getElementById('password');
    
    const toggleRegPassword = document.getElementById('toggleRegPassword');
    const regPassword = document.getElementById('regPassword');
    
    const toggleRegConfirm = document.getElementById('toggleRegConfirm');
    const regConfirm = document.getElementById('regConfirm');
    
    // 登录密码切换
    if (togglePassword && password) {
        togglePassword.addEventListener('click', function() {
            const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
            password.setAttribute('type', type);
            togglePassword.innerHTML = type === 'password' ? '<i class="fas fa-eye"></i>' : '<i class="fas fa-eye-slash"></i>';
        });
    }
    
    // 注册密码切换
    if (toggleRegPassword && regPassword) {
        toggleRegPassword.addEventListener('click', function() {
            const type = regPassword.getAttribute('type') === 'password' ? 'text' : 'password';
            regPassword.setAttribute('type', type);
            toggleRegPassword.innerHTML = type === 'password' ? '<i class="fas fa-eye"></i>' : '<i class="fas fa-eye-slash"></i>';
        });
    }
    
    // 确认密码切换
    if (toggleRegConfirm && regConfirm) {
        toggleRegConfirm.addEventListener('click', function() {
            const type = regConfirm.getAttribute('type') === 'password' ? 'text' : 'password';
            regConfirm.setAttribute('type', type);
            toggleRegConfirm.innerHTML = type === 'password' ? '<i class="fas fa-eye"></i>' : '<i class="fas fa-eye-slash"></i>';
        });
    }
}

// 设置表单验证
function setupFormValidation() {
    // 获取所有需要验证的表单
    const forms = document.querySelectorAll('.needs-validation');
    
    // 为每个表单添加验证
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
}

// 切换到注册表单
function switchToRegister() {
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'block';
    document.getElementById('formTitle').textContent = '用户注册';
    document.querySelector('.text-muted').textContent = '创建您的账户，开始购物之旅';
    
    // 重置表单验证状态
    document.getElementById('loginForm').classList.remove('was-validated');
    document.getElementById('registerForm').classList.remove('was-validated');
    
    // 隐藏错误信息
    hideError();
}

// 切换到登录表单
function switchToLogin() {
    document.getElementById('registerForm').style.display = 'none';
    document.getElementById('loginForm').style.display = 'block';
    document.getElementById('formTitle').textContent = '用户登录';
    document.querySelector('.text-muted').textContent = '欢迎回来，请登录您的账户';
    
    // 重置表单验证状态
    document.getElementById('loginForm').classList.remove('was-validated');
    document.getElementById('registerForm').classList.remove('was-validated');
    
    // 隐藏错误信息
    hideError();
}

// 处理登录
async function handleLogin(e) {
    e.preventDefault();
    
    const form = e.target;
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
    }
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const rememberMe = document.getElementById('rememberMe').checked;
    
    // 显示加载状态
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>登录中...';
    
    try {
        // 调用登录API
        const response = await login(username, password);
        
        if (response.ok) {
            const data = await response.json();
            // 保存登录状态
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify(data.user));
            
            // 记住用户名
            if (rememberMe) {
                localStorage.setItem('savedUsername', username);
                localStorage.setItem('rememberMe', 'true');
            } else {
                localStorage.removeItem('savedUsername');
                localStorage.removeItem('rememberMe');
            }
            
            showSuccess('登录成功，正在跳转...');
            
            // 跳转到首页
            setTimeout(() => {
                window.location.href = '../home/index.html';
            }, 1000);
        } else {
            let errorMsg = '登录失败';
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const errorData = await response.json();
                errorMsg = errorData.message || errorMsg;
            } else {
                errorMsg = await response.text() || errorMsg;
            }
            showError(errorMsg);
        }
    } catch (error) {
        console.error('登录错误:', error);
        showError('网络错误，请稍后重试');
    } finally {
        // 恢复按钮状态
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalText;
    }
}

// 处理注册
async function handleRegister(e) {
    e.preventDefault();
    
    const form = e.target;
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
    }
    
    const phone = document.getElementById('regPhone').value.trim();
    const code = document.getElementById('regCode').value.trim();
    const password = document.getElementById('regPassword').value;
    const confirm = document.getElementById('regConfirm').value;
    const agreeTerms = document.getElementById('agreeTerms').checked;
    
    // 验证密码一致性
    if (password !== confirm) {
        showError('两次输入的密码不一致');
        return;
    }
    
    // 验证协议同意
    if (!agreeTerms) {
        showError('请同意用户协议和隐私政策');
        return;
    }
    
    // 暂时跳过验证码验证，因为后端没有此接口
    // if (!code) {
    //     showError('请输入验证码');
    //     return;
    // }
    
    // 显示加载状态
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>注册中...';
    
    try {
        // 调用注册API
        const response = await register(phone, code, password);
        
        if (response.ok) {
            const data = await response.json();
            // 注册成功后自动登录
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify(data.user));
            
            showSuccess('注册成功，正在跳转...');
            
            // 跳转到首页
            setTimeout(() => {
                window.location.href = '../home/index.html';
            }, 1000);
        } else {
            let errorMsg = '注册失败';
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const errorData = await response.json();
                errorMsg = errorData.message || errorMsg;
            } else {
                errorMsg = await response.text() || errorMsg;
            }
            showError(errorMsg);
        }
    } catch (error) {
        console.error('注册错误:', error);
        showError('网络错误，请稍后重试');
    } finally {
        // 恢复按钮状态
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalText;
    }
}

// 获取验证码
async function getVerificationCode() {
    const email = document.getElementById('regPhone').value.trim() + '@example.com';
    if (!email) {
        showError('请先输入邮箱/手机号');
        return;
    }
    const btn = document.getElementById('getCodeBtn');
    const originalText = btn.innerHTML;
    try {
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>发送中...';
        // 调用发送验证码API
        const response = await fetch(`${window.API_CONFIG.auth.baseUrl}/send-code?email=${encodeURIComponent(email)}`, {
            method: 'POST'
        });
        if (response.ok) {
            const data = await response.json();
            showSuccess('验证码已发送');
            if (data.code) {
                console.log('【开发调试用】收到验证码：', data.code);
            }
            startCountdown(btn);
        } else {
            let errorMsg = '发送失败';
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const errorData = await response.json();
                errorMsg = errorData.message || errorMsg;
            } else {
                errorMsg = await response.text() || errorMsg;
            }
            showError(errorMsg);
            btn.disabled = false;
            btn.innerHTML = originalText;
        }
    } catch (error) {
        console.error('发送验证码错误:', error);
        showError('网络错误，请稍后重试');
        btn.disabled = false;
        btn.innerHTML = originalText;
    }
}

// 开始倒计时
function startCountdown(btn) {
    let countdown = 60;
    const originalText = btn.innerHTML;
    
    const timer = setInterval(() => {
        btn.innerHTML = `${countdown}秒后重试`;
        countdown--;
        
        if (countdown < 0) {
            clearInterval(timer);
            btn.disabled = false;
            btn.innerHTML = '获取验证码';
        }
    }, 1000);
}

// 验证手机号
function validatePhone() {
    const phone = this.value.trim();
    const isValid = validatePhoneNumber(phone);
    
    if (phone && !isValid) {
        this.setCustomValidity('请输入有效的手机号码');
    } else {
        this.setCustomValidity('');
    }
}

// 验证密码确认
function validatePasswordConfirm() {
    const password = document.getElementById('regPassword').value;
    const confirm = this.value;
    
    if (confirm && password !== confirm) {
        this.setCustomValidity('两次输入的密码不一致');
    } else {
        this.setCustomValidity('');
    }
}

// 验证手机号格式
function validatePhoneNumber(phone) {
    const phoneRegex = /^1[3-9]\d{9}$/;
    return phoneRegex.test(phone);
}

// 显示错误信息
function showError(message) {
    const errorDiv = document.getElementById('formError');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
    
    // 自动隐藏
    setTimeout(() => {
        hideError();
    }, 5000);
}

// 隐藏错误信息
function hideError() {
    const errorDiv = document.getElementById('formError');
    errorDiv.style.display = 'none';
}

// 显示成功信息
function showSuccess(message) {
    // 创建成功提示
    const alertHtml = `
        <div class="alert alert-success alert-dismissible fade show position-fixed" 
             style="top: 20px; right: 20px; z-index: 10000; min-width: 300px;" role="alert">
            <i class="fas fa-check-circle me-2"></i>${message}
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

// API调用函数
async function login(username, password) {
    try {
        const response = await fetch(`${window.API_CONFIG.auth.baseUrl}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });
        
        return response;
    } catch (error) {
        console.error('登录API调用失败:', error);
        throw error;
    }
}

async function register(phone, code, password) {
    try {
        const response = await fetch(`${window.API_CONFIG.auth.baseUrl}/register?code=${encodeURIComponent(code)}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: phone,
                password: password,
                email: phone + '@example.com',
                fullName: '用户' + phone.substring(7)
            })
        });
        return response;
    } catch (error) {
        console.error('注册API调用失败:', error);
        throw error;
    }
}

// 模拟API响应（用于测试）
function mockApiResponse(success, message, data = null) {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({
                success: success,
                message: message,
                data: data
            });
        }, 1000);
    });
}

// 如果API不可用，使用模拟数据
if (typeof fetch === 'undefined' || !navigator.onLine) {
    window.login = function(username, password) {
        return mockApiResponse(true, '登录成功', {
            token: 'mock-token-' + Date.now(),
            user: {
                id: 1,
                username: username,
                nickname: username,
                email: username + '@example.com'
            }
        });
    };
    
    window.register = function(phone, code, password) {
        return mockApiResponse(true, '注册成功');
    };
    
    window.sendVerificationCode = function(phone) {
        return mockApiResponse(true, '验证码已发送');
    };
} 