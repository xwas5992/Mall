document.addEventListener('DOMContentLoaded', function () {
    const authManager = new AuthManager();

    // Check login status. Redirect to login page if not logged in.
    if (!authManager.isLoggedIn()) {
        window.location.href = '../login/login.html?redirect=' + encodeURIComponent(window.location.pathname);
        return;
    }

    function getGreeting() {
        const hour = new Date().getHours();
        if (hour < 6) return "凌晨好";
        if (hour < 9) return "早上好";
        if (hour < 12) return "上午好";
        if (hour < 14) return "中午好";
        if (hour < 17) return "下午好";
        if (hour < 19) return "傍晚好";
        return "晚上好";
    }

    // Update user display
    function updateUserDisplay() {
        const user = authManager.getUser();
        if (user) {
            const userNameEl = document.getElementById('user-name');
            if (userNameEl) userNameEl.textContent = user.nickname || user.username || user.phone;
            const userRoleEl = document.getElementById('user-role');
            if (userRoleEl) userRoleEl.textContent = user.role || '用户';
            const userPhoneEl = document.getElementById('user-phone');
            if (userPhoneEl && user.phone) userPhoneEl.textContent = maskPhone(user.phone);
            const userAvatarEl = document.getElementById('user-avatar');
            if (userAvatarEl && user.avatar) userAvatarEl.src = user.avatar;
        }
    }

    // Mask phone number for privacy
    function maskPhone(phone) {
        if (!phone || phone.length < 7) return phone;
        return phone.substring(0, 3) + '****' + phone.substring(phone.length - 4);
    }

    // Load user profile data
    async function loadUserProfile() {
        const user = authManager.getUser();
        if (!user) return;

        try {
            const response = await fetch(`${window.API_CONFIG.user.baseUrl}/profile/${user.username}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + authManager.getToken()
                }
            });

            if (response.ok) {
                const userData = await response.json();
                fillProfileForm(userData);
            } else {
                console.error('Failed to load user profile');
            }
        } catch (error) {
            console.error('Error loading user profile:', error);
        }
    }

    // Fill profile form with user data
    function fillProfileForm(userData) {
        document.getElementById('nickname').value = userData.nickname || '';
        document.getElementById('phone').value = userData.phone || '';
        document.getElementById('gender').value = userData.gender || '';
        document.getElementById('birthday').value = userData.birthday ? userData.birthday.split('T')[0] : '';
        document.getElementById('avatar').value = userData.avatar || '';
    }

    // Show dashboard content
    window.showDashboard = function() {
        document.getElementById('dashboard-content').style.display = 'block';
        document.getElementById('profile-edit-content').style.display = 'none';
        document.getElementById('address-manager-content').style.display = 'none';
        // Update active menu item
        document.querySelectorAll('.list-group-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector('.list-group-item[onclick="showDashboard()"]').classList.add('active');
    };

    // Show profile edit content
    window.showProfileEdit = function() {
        document.getElementById('dashboard-content').style.display = 'none';
        document.getElementById('profile-edit-content').style.display = 'block';
        
        // Update active menu item
        document.querySelectorAll('.list-group-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector('.list-group-item[onclick="showProfileEdit()"]').classList.add('active');
        
        // Load user profile data
        loadUserProfile();
    };

    // Handle profile form submission
    document.getElementById('profile-edit-form').addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const user = authManager.getUser();
        if (!user) {
            showToast('用户未登录', 'error');
            return;
        }

        const formData = new FormData(e.target);
        const updateData = {
            nickname: formData.get('nickname'),
            phone: formData.get('phone'),
            gender: formData.get('gender'),
            birthday: formData.get('birthday') ? new Date(formData.get('birthday')).toISOString() : null,
            avatar: formData.get('avatar')
        };

        try {
            console.log('Updating user info for user ID:', user.id);
            console.log('Update data:', updateData);
            
            const response = await fetch(`${window.API_CONFIG.user.baseUrl}/${user.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + authManager.getToken()
                },
                body: JSON.stringify(updateData)
            });

            console.log('Response status:', response.status);
            console.log('Response headers:', response.headers);

            if (response.ok) {
                const updatedUser = await response.json();
                console.log('Updated user data:', updatedUser);
                
                // Update local storage
                localStorage.setItem('user', JSON.stringify(updatedUser));
                
                showToast('个人信息更新成功', 'success');
                
                // Update display
                updateUserDisplay();
                
                // Return to dashboard
                setTimeout(() => {
                    showDashboard();
                }, 1500);
            } else {
                // 尝试解析错误响应
                let errorMessage = '更新失败';
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorData.error || '更新失败';
                    console.log('Error response:', errorData);
                } catch (parseError) {
                    console.log('Failed to parse error response:', parseError);
                    // 如果无法解析JSON，使用状态码信息
                    if (response.status === 400) {
                        errorMessage = '请求参数错误';
                    } else if (response.status === 404) {
                        errorMessage = '用户不存在';
                    } else if (response.status === 401) {
                        errorMessage = '未授权访问';
                    } else if (response.status === 500) {
                        errorMessage = '服务器内部错误';
                    }
                }
                
                showToast(errorMessage, 'error');
                console.error('Update failed with status:', response.status);
            }
        } catch (error) {
            console.error('Error updating profile:', error);
            showToast('网络错误，请稍后重试', 'error');
        }
    });

    // Show toast message
    function showToast(message, type = 'info') {
        const toastContainer = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast align-items-center text-white bg-${type} border-0`;
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');

        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'} me-2"></i>
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

        // Auto remove
        toast.addEventListener('hidden.bs.toast', () => {
            toastContainer.removeChild(toast);
        });
    }

    // 动态显示绑定手机号
    function showBindedPhone() {
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        const phoneSpan = document.getElementById('binded-phone');
        if (phoneSpan) {
            if (user.phone) {
                phoneSpan.innerText = maskPhone(user.phone);
            } else {
                phoneSpan.innerText = '未绑定';
            }
        }
    }

    // Show address manager content
    window.showAddressManager = function() {
        document.getElementById('dashboard-content').style.display = 'none';
        document.getElementById('profile-edit-content').style.display = 'none';
        document.getElementById('address-manager-content').style.display = 'block';
        // 更新侧边栏高亮
        document.querySelectorAll('.list-group-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector('.list-group-item[onclick="showAddressManager()"]')?.classList.add('active');
        // 渲染表单内容
        renderAddressForm();
    };

    function renderAddressForm() {
        const html = `
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h4><i class="fas fa-map-marker-alt me-2"></i>收货地址管理</h4>
                <button class="btn btn-outline-secondary" onclick="showDashboard()">
                    <i class="fas fa-arrow-left me-1"></i>返回
                </button>
            </div>
            <form id="address-form">
                <div class="mb-3">
                    <label class="form-label">* 收货人：</label>
                    <input type="text" class="form-control" name="consignee" placeholder="请输入收货人姓名" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">* 手机号码：</label>
                    <input type="tel" class="form-control" name="phone" placeholder="请输入11位手机号码" pattern="^1[3-9]\\d{9}$" required>
                </div>
                <div class="mb-3 position-relative">
                    <label class="form-label">* 所在地区：</label>
                    <input type="text" class="form-control" name="region" id="region-input" placeholder="点击此处选择地区" required readonly style="background:#fff;cursor:pointer;">
                    <div id="region-modal" class="region-modal" style="display:none;position:absolute;top:48px;left:0;width:100%;z-index:10;background:#fff;border:1px solid #a94442;border-radius:4px;padding:16px;">
                        <div class="d-flex align-items-center mb-2">
                            <input type="text" class="form-control" id="region-search" placeholder="输入街道、乡镇、小区或商圈名称" style="flex:1;">
                            <button type="button" class="btn btn-link text-secondary ms-2" id="region-modal-close" style="font-size:1.2rem;">&times;</button>
                        </div>
                        <div class="mb-2 text-muted" style="font-size:0.95rem;">例如：深圳 天安云谷</div>
                        <div class="mb-2"><a href="#" id="region-direct-select" style="color:#d35400;">直接选择地址&gt;</a></div>
                        <div id="region-list"></div>
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label">* 详细地址：</label>
                    <textarea class="form-control" name="detail" rows="3" placeholder="请输入详细地址" required></textarea>
                    <div class="form-text text-danger d-none" id="address-detail-error">请填写详细地址</div>
                </div>
                <div class="form-check mb-3">
                    <input class="form-check-input" type="checkbox" name="isDefault" id="isDefault">
                    <label class="form-check-label" for="isDefault">设为默认地址</label>
                </div>
                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-warning">添加新地址</button>
                    <button type="reset" class="btn btn-outline-secondary">清空</button>
                </div>
            </form>
        `;
        document.getElementById('address-manager-content').innerHTML = html;

        // 地区输入框弹窗交互
        const regionInput = document.getElementById('region-input');
        const regionModal = document.getElementById('region-modal');
        const regionModalClose = document.getElementById('region-modal-close');
        regionInput.addEventListener('click', function(e) {
            regionModal.style.display = 'block';
        });
        regionModalClose.addEventListener('click', function() {
            regionModal.style.display = 'none';
        });
        // 点击弹窗外关闭
        document.addEventListener('mousedown', function(e) {
            if(regionModal.style.display==='block' && !regionModal.contains(e.target) && e.target!==regionInput) {
                regionModal.style.display = 'none';
            }
        });
    }

    // Initialize
    updateUserDisplay();
    showBindedPhone();
}); 