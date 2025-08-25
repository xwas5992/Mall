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
    window.showAddressManager = async function() {
        document.getElementById('dashboard-content').style.display = 'none';
        document.getElementById('profile-edit-content').style.display = 'none';
        document.getElementById('address-manager-content').style.display = 'block';
        // 更新侧边栏高亮
        document.querySelectorAll('.list-group-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector('.list-group-item[onclick="showAddressManager()"]')?.classList.add('active');
        // 渲染卡片区
        await renderAddressCards();
    };

    // 全局地址列表
    window.addresses = [];

    async function renderAddressCards() {
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        if (!user || !user.id) return;
        const userId = user.id;
        const container = document.getElementById('address-manager-content');
        container.innerHTML = `
            <h2 style="color:#888;font-weight:400;">收货地址</h2>
            <div id="address-list-wrapper" style="position:relative;">
                <div id="address-card-list" style="display:flex;gap:48px;flex-wrap:wrap;margin-top:32px;"></div>
                <div id="address-fade" style="display:none;position:absolute;left:0;right:0;bottom:0;height:60px;background:linear-gradient(to bottom, rgba(255,255,255,0), rgba(255,255,255,1));pointer-events:none;"></div>
            </div>`;
        const cardList = document.getElementById('address-card-list');
        const listWrapper = document.getElementById('address-list-wrapper');
        const fadeEl = document.getElementById('address-fade');
        // 查询地址
        window.addresses = [];
        try {
            const res = await fetch(`${window.API_CONFIG.user.baseUrl}/address/${userId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + authManager.getToken()
                }
            });
            if (res.ok) {
                window.addresses = await res.json();
                console.log('获取地址成功:', window.addresses);
            } else {
                console.error('获取地址失败:', res.status, res.statusText);
            }
        } catch(e) { 
            console.error('获取地址异常:', e);
            window.addresses = []; 
        }
        // 添加新地址卡片
        cardList.innerHTML = `<div class="address-card add-card" style="width:320px;height:240px;display:flex;flex-direction:column;align-items:center;justify-content:center;border:1px solid #ddd;border-radius:4px;color:#bbb;cursor:pointer;" onclick="showAddressFormModal()">
            <div style="font-size:2.5rem;"><i class="fas fa-plus-circle"></i></div>
            <div style="margin-top:12px;font-size:1.2rem;">添加新地址</div>
        </div>`;
        // 若超过3条，开启内部滚动
        const enableScroll = (window.addresses?.length || 0) > 3;
        if (enableScroll) {
            cardList.style.maxHeight = '560px';
            cardList.style.overflowY = 'auto';
            cardList.style.overflowX = 'hidden';
            cardList.style.paddingRight = '8px';
            if (listWrapper) listWrapper.style.paddingBottom = '0px';
            if (fadeEl) fadeEl.style.display = '';
            // 绑定滚动事件控制渐隐显隐
            const toggleFade = () => {
                if (!fadeEl) return;
                const atBottom = cardList.scrollTop + cardList.clientHeight >= cardList.scrollHeight - 1;
                fadeEl.style.display = atBottom ? 'none' : '';
            };
            cardList.removeEventListener('scroll', cardList._fadeHandler || (()=>{}));
            cardList._fadeHandler = toggleFade;
            cardList.addEventListener('scroll', toggleFade);
            // 初始判断一次
            toggleFade();
        } else {
            cardList.style.maxHeight = '';
            cardList.style.overflowY = '';
            cardList.style.overflowX = '';
            cardList.style.paddingRight = '';
            if (fadeEl) fadeEl.style.display = 'none';
        }

        // 渲染已有地址卡片
        window.addresses.forEach(addr => {
            cardList.innerHTML += `<div class="address-card" style="width:320px;height:240px;display:flex;flex-direction:column;border:1px solid #ddd;border-radius:4px;padding:24px 20px 60px 20px;position:relative;">
                <div style="display:flex;justify-content:space-between;align-items:center;">
                    <div style="font-size:1.4rem;font-weight:600;">${addr.recipientName}</div>
                </div>
                <div style="margin-top:4px;font-size:1.1rem;">${addr.recipientPhone}</div>
                <div style="margin-top:4px;color:#666;">${addr.province} ${addr.city} ${addr.district} ${addr.addressDetail}</div>
                <div style="position:absolute;bottom:12px;right:20px;display:flex;gap:16px;">
                    <span style="color:orange;cursor:pointer;" onclick="window.showEditAddressModal(${addr.id})">修改</span>
                    <span style="color:orange;cursor:pointer;" onclick="deleteAddress(${addr.id})">删除</span>
                </div>
                <div style="position:absolute;bottom:12px;left:20px;">
                    ${addr.isDefault ? '<span style="color:#ff6700;font-weight:600;">默认</span>' : `<a href="#" style="color:#198754;" onclick="setDefaultAddress(${addr.id},${userId});return false;">设为默认</a>`}
                </div>
            </div>`;
        });
    }

    // 弹窗表单
    window.showAddressFormModal = function() {
        const modal = document.createElement('div');
        modal.className = 'address-modal-mask';
        modal.innerHTML = `<div class="address-modal" style="background:#fff;width:600px;max-width:95vw;padding:32px 36px 24px 36px;border-radius:8px;box-shadow:0 8px 32px rgba(0,0,0,0.18);position:fixed;top:50%;left:50%;transform:translate(-50%,-50%);z-index:2000;">
            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:24px;">
                <div style="font-size:1.5rem;font-weight:500;">添加收货地址</div>
                <div style="font-size:2rem;cursor:pointer;color:#aaa;" onclick="this.closest('.address-modal-mask').remove()">&times;</div>
            </div>
            <form id="address-modal-form">
                <div class="row mb-3">
                    <div class="col"><input type="text" class="form-control" name="recipientName" placeholder="姓名" required></div>
                    <div class="col"><input type="text" class="form-control" name="recipientPhone" placeholder="手机号" required></div>
                </div>
                <div class="mb-3"><input type="text" class="form-control" name="region" placeholder="选择省 / 市 / 区 / 街道" required></div>
                <input type="hidden" name="province" value="">
                <input type="hidden" name="city" value="">
                <input type="hidden" name="district" value="">
                <div class="mb-3"><textarea class="form-control" name="addressDetail" rows="2" placeholder="详细地址" required></textarea></div>
                <div class="form-check mb-3"><input class="form-check-input" type="checkbox" name="isDefault" id="isDefaultModal"><label class="form-check-label" for="isDefaultModal">设为默认地址</label></div>
                <div class="d-flex gap-2 justify-content-end"><button type="button" class="btn btn-secondary" onclick="this.closest('.address-modal-mask').remove()">取消</button><button type="submit" class="btn btn-warning">确定</button></div>
            </form>
        </div>`;
        document.body.appendChild(modal);
        // 同步隐藏省市区：监听region输入变化
        (function(){
            const form = document.getElementById('address-modal-form');
            const regionEl = form.querySelector('input[name="region"]');
            const pEl = form.querySelector('input[name="province"]');
            const cEl = form.querySelector('input[name="city"]');
            const dEl = form.querySelector('input[name="district"]');
            const syncHidden = () => {
                const parts = (regionEl.value||'').trim().split(/\s+/);
                pEl.value = parts[0] || '';
                cEl.value = parts[1] || '';
                dEl.value = parts[2] || '';
            };
            regionEl.addEventListener('input', syncHidden);
            syncHidden();
        })();
        document.getElementById('address-modal-form').onsubmit = async function(e) {
            e.preventDefault();
            const user = JSON.parse(localStorage.getItem('user') || '{}');
            const fd = new FormData(e.target);
            // 优先取隐藏字段，其次回退解析region文本
            const province = (fd.get('province') || '').toString().trim() || (fd.get('region')||'').split(/\s+/)[0] || '';
            const city = (fd.get('city') || '').toString().trim() || (fd.get('region')||'').split(/\s+/)[1] || '';
            const district = (fd.get('district') || '').toString().trim() || (fd.get('region')||'').split(/\s+/)[2] || '';
            const data = {
                userId: user.id,
                recipientName: fd.get('recipientName'),
                recipientPhone: fd.get('recipientPhone'),
                province: province||'',
                city: city||'',
                district: district||'',
                addressDetail: fd.get('addressDetail'),
                isDefault: !!fd.get('isDefault')
            };
            const response = await fetch(`${window.API_CONFIG.user.baseUrl}/address`, {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + authManager.getToken()
                },
                body: JSON.stringify(data)
            });
            
            if (response.ok) {
                showToast('地址添加成功', 'success');
            } else {
                const errorData = await response.json().catch(() => ({}));
                showToast(errorData.message || '添加地址失败', 'error');
                console.error('添加地址失败:', response.status, errorData);
            }
            modal.remove();
            await renderAddressCards();
        };
    };

    // 删除地址
    window.deleteAddress = async function(id) {
        if (!confirm('确定要删除该地址吗？')) return;
        try {
            const response = await fetch(`${window.API_CONFIG.user.baseUrl}/address/${id}`, { 
                method: 'DELETE',
                headers: {
                    'Authorization': 'Bearer ' + authManager.getToken()
                }
            });
            if (response.ok) {
                showToast('地址删除成功', 'success');
            } else {
                showToast('删除地址失败', 'error');
            }
        } catch (error) {
            console.error('删除地址异常:', error);
            showToast('删除地址失败', 'error');
        }
        await renderAddressCards();
    };
    // 设为默认
    window.setDefaultAddress = async function(id, userId) {
        try {
            // 先查出该地址
            const res = await fetch(`${window.API_CONFIG.user.baseUrl}/address/detail/${id}`, {
                headers: {
                    'Authorization': 'Bearer ' + authManager.getToken()
                }
            });
            if (!res.ok) {
                showToast('获取地址信息失败', 'error');
                return;
            }
            const addr = await res.json();
            addr.isDefault = true;
            const response = await fetch(`${window.API_CONFIG.user.baseUrl}/address`, {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + authManager.getToken()
                },
                body: JSON.stringify(addr)
            });
            if (response.ok) {
                showToast('设置默认地址成功', 'success');
            } else {
                showToast('设置默认地址失败', 'error');
            }
        } catch (error) {
            console.error('设置默认地址异常:', error);
            showToast('设置默认地址失败', 'error');
        }
        await renderAddressCards();
    };

    // 编辑弹窗
    window.showEditAddressModal = function(addressId) {
        const addr = window.addresses.find(a => a.id === addressId);
        const modal = document.createElement('div');
        modal.className = 'address-modal-mask';
        modal.innerHTML = `<div class="address-modal" style="background:#fff;width:600px;max-width:95vw;padding:32px 36px 24px 36px;border-radius:8px;box-shadow:0 8px 32px rgba(0,0,0,0.18);position:fixed;top:50%;left:50%;transform:translate(-50%,-50%);z-index:2000;">
            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:24px;">
                <div style="font-size:1.5rem;font-weight:500;">修改收货地址</div>
                <div style="font-size:2rem;cursor:pointer;color:#aaa;" onclick="this.closest('.address-modal-mask').remove()">&times;</div>
            </div>
            <form id="address-edit-form">
                <div class="row mb-3">
                    <div class="col"><input type="text" class="form-control" name="recipientName" placeholder="姓名" value="${addr.recipientName||''}" required></div>
                    <div class="col"><input type="text" class="form-control" name="recipientPhone" placeholder="手机号" value="${addr.recipientPhone||''}" required style="border-color: orange;"></div>
                </div>
                <div class="mb-3"><input type="text" class="form-control" name="region" placeholder="省 市 区 街道" value="${[addr.province,addr.city,addr.district].join(' ')}" required></div>
                <input type="hidden" name="province" value="${addr.province||''}">
                <input type="hidden" name="city" value="${addr.city||''}">
                <input type="hidden" name="district" value="${addr.district||''}">
                <div class="mb-3"><textarea class="form-control" name="addressDetail" rows="2" placeholder="详细地址" required>${addr.addressDetail||''}</textarea></div>
                <div class="form-check mb-3"><input class="form-check-input" type="checkbox" name="isDefault" id="isDefaultEditModal" ${addr.isDefault?'checked':''}><label class="form-check-label" for="isDefaultEditModal">设为默认地址</label></div>
                <div class="d-flex gap-2 justify-content-end"><button type="button" class="btn btn-secondary" onclick="this.closest('.address-modal-mask').remove()">取消</button><button type="submit" class="btn btn-warning">保存</button></div>
            </form>
        </div>`;
        document.body.appendChild(modal);
        // 同步隐藏省市区：监听region输入变化
        (function(){
            const form = document.getElementById('address-edit-form');
            const regionEl = form.querySelector('input[name="region"]');
            const pEl = form.querySelector('input[name="province"]');
            const cEl = form.querySelector('input[name="city"]');
            const dEl = form.querySelector('input[name="district"]');
            const syncHidden = () => {
                const parts = (regionEl.value||'').trim().split(/\s+/);
                pEl.value = parts[0] || '';
                cEl.value = parts[1] || '';
                dEl.value = parts[2] || '';
            };
            regionEl.addEventListener('input', syncHidden);
            syncHidden();
        })();
        document.getElementById('address-edit-form').onsubmit = async function(e) {
            e.preventDefault();
            const user = JSON.parse(localStorage.getItem('user') || '{}');
            const fd = new FormData(e.target);
            // 编辑时也优先取隐藏字段（若表单含有），否则解析region
            const province = (fd.get('province') || '').toString().trim() || (fd.get('region')||'').split(/\s+/)[0] || '';
            const city = (fd.get('city') || '').toString().trim() || (fd.get('region')||'').split(/\s+/)[1] || '';
            const district = (fd.get('district') || '').toString().trim() || (fd.get('region')||'').split(/\s+/)[2] || '';
            const data = {
                id: addr.id,
                userId: user.id,
                recipientName: fd.get('recipientName'),
                recipientPhone: fd.get('recipientPhone'),
                province: province||'',
                city: city||'',
                district: district||'',
                addressDetail: fd.get('addressDetail'),
                isDefault: !!fd.get('isDefault')
            };
            const response = await fetch(`${window.API_CONFIG.user.baseUrl}/address/${addr.id}`, {
                method: 'PUT',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + authManager.getToken()
                },
                body: JSON.stringify(data)
            });
            if (response.ok) {
                showToast('地址修改成功', 'success');
            } else {
                const errorData = await response.json().catch(() => ({}));
                showToast(errorData.message || '修改地址失败', 'error');
                console.error('修改地址失败:', response.status, errorData);
            }
            modal.remove();
            await renderAddressCards();
        };
    };

    // 地区选择器事件（支持主表单与弹窗表单复用）
    let currentRegionTarget = null; // { regionInput, pHidden, cHidden, dHidden }
    document.body.addEventListener('click', function(e) {
      // 主表单按钮
      if (e.target && e.target.id === 'chooseRegionBtn') {
        const modal = document.getElementById('regionPickerModal');
        if (modal) {
          currentRegionTarget = {
            regionInput: document.getElementById('regionInput'),
            pHidden: document.getElementById('provinceHidden'),
            cHidden: document.getElementById('cityHidden'),
            dHidden: document.getElementById('districtHidden')
          };
          modal.style.display = 'flex';
          setTimeout(() => {
            if (window.$ && window.$('#distpicker').length > 0) {
              try { window.$('#distpicker').distpicker('destroy'); } catch(_) {}
              const value = (currentRegionTarget.regionInput && currentRegionTarget.regionInput.value ? currentRegionTarget.regionInput.value.trim() : '');
              const parts = value ? value.split(/\s+/) : [];
              window.$('#distpicker').distpicker({
                province: parts[0] || '---- 省 ----',
                city: parts[1] || '---- 市 ----',
                district: parts[2] || '---- 区 ----'
              });
            }
          }, 100);
        }
      }

      // 任意表单内点击region输入框
      if (e.target && e.target.matches('input[name="region"]')) {
        const input = e.target;
        const form = input.closest('form');
        if (form) {
          currentRegionTarget = {
            regionInput: input,
            pHidden: form.querySelector('input[name="province"]'),
            cHidden: form.querySelector('input[name="city"]'),
            dHidden: form.querySelector('input[name="district"]')
          };
          const modal = document.getElementById('regionPickerModal');
          if (modal) {
            modal.style.display = 'flex';
            setTimeout(() => {
              if (window.$ && window.$('#distpicker').length > 0) {
                try { window.$('#distpicker').distpicker('destroy'); } catch(_) {}
                const value = (input && input.value ? input.value.trim() : '');
                const parts = value ? value.split(/\s+/) : [];
                window.$('#distpicker').distpicker({
                  province: parts[0] || '---- 省 ----',
                  city: parts[1] || '---- 市 ----',
                  district: parts[2] || '---- 区 ----'
                });
              }
            }, 100);
          }
        }
      }

      if (e.target && e.target.id === 'cancelRegionBtn') {
        const modal = document.getElementById('regionPickerModal');
        if (modal) {
          modal.style.display = 'none';
        }
        currentRegionTarget = null;
      }

      if (e.target && e.target.id === 'confirmRegionBtn') {
        const province = window.$('#distpicker select[data-province]').val();
        const city = window.$('#distpicker select[data-city]').val();
        const district = window.$('#distpicker select[data-district]').val();
        if (province && province !== '---- 省 ----' && city && city !== '---- 市 ----' && district && district !== '---- 区 ----') {
          if (currentRegionTarget && currentRegionTarget.regionInput) {
            currentRegionTarget.regionInput.value = [province, city, district].join(' ');
            if (currentRegionTarget.pHidden) currentRegionTarget.pHidden.value = province;
            if (currentRegionTarget.cHidden) currentRegionTarget.cHidden.value = city;
            if (currentRegionTarget.dHidden) currentRegionTarget.dHidden.value = district;
          } else {
            const regionInput = document.getElementById('regionInput');
            if (regionInput) regionInput.value = [province, city, district].join(' ');
            const pHidden = document.getElementById('provinceHidden');
            const cHidden = document.getElementById('cityHidden');
            const dHidden = document.getElementById('districtHidden');
            if (pHidden) pHidden.value = province;
            if (cHidden) cHidden.value = city;
            if (dHidden) dHidden.value = district;
          }
          const modal = document.getElementById('regionPickerModal');
          if (modal) {
            modal.style.display = 'none';
          }
          currentRegionTarget = null;
        } else {
          alert('请选择完整的省市区信息');
        }
      }
    });

    // 初始化地区选择器
    function initRegionPicker() {
      if (window.$ && window.$('#distpicker').length > 0) {
        try { window.$('#distpicker').distpicker('destroy'); } catch(_) {}
        window.$('#distpicker').distpicker({
          province: '---- 省 ----',
          city: '---- 市 ----',
          district: '---- 区 ----'
        });
      }
    }

    // 添加事件监听器处理菜单点击
    document.addEventListener('click', function(e) {
        const action = e.target.getAttribute('data-action');
        if (action) {
            e.preventDefault();
            switch (action) {
                case 'dashboard':
                    showDashboard();
                    break;
                case 'profile':
                    showProfileEdit();
                    break;
                case 'address':
                    showAddressManager();
                    break;
            }
        }
    });

    // Initialize
    updateUserDisplay();
    showBindedPhone();
    
    // 延迟初始化地区选择器，确保jQuery和distpicker都加载完成
    setTimeout(initRegionPicker, 1000);
}); 