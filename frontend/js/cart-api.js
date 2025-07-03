// 购物车API模块
const CartAPI = {
    getToken() {
        const token = localStorage.getItem('token') || '';
        console.log('获取到的token:', token ? token.substring(0, 20) + '...' : 'null');
        return token;
    },
    
    isLoggedIn() {
        const loggedIn = !!this.getToken();
        console.log('用户登录状态:', loggedIn);
        return loggedIn;
    },
    
    getHeaders() {
        const token = this.getToken();
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        };
        console.log('请求头:', headers);
        return headers;
    },
    
    async addToCart(product, quantity = 1) {
        console.log('尝试添加商品到购物车:', { product, quantity });
        
        // 如果未登录，提示用户登录
        if (!this.isLoggedIn()) {
            console.log('用户未登录，无法添加购物车');
            return { success: false, message: '请先登录' };
        }

        try {
            const requestBody = {
                productId: product.id,
                productName: product.name,
                productImage: product.image || product.imageUrl || '',
                productPrice: product.price,
                quantity: quantity
            };
            console.log('请求体:', requestBody);
            
            const res = await fetch(`${window.API_CONFIG.cart.baseUrl}/add`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(requestBody)
            });
            
            console.log('响应状态:', res.status, res.statusText);
            
            if (!res.ok) {
                const errorText = await res.text();
                console.error('添加购物车失败:', res.status, res.statusText, errorText);
                return { success: false, message: `添加购物车失败: ${res.status} ${res.statusText}` };
            }
            
            const result = await res.json();
            console.log('添加购物车成功:', result);
            return result;
        } catch (error) {
            console.error('添加购物车网络错误:', error);
            return { success: false, message: '网络错误: ' + error.message };
        }
    },
    
    async getCart() {
        console.log('尝试获取购物车列表');
        
        // 如果未登录，返回空购物车
        if (!this.isLoggedIn()) {
            console.log('用户未登录，返回空购物车');
            return [];
        }

        try {
            const res = await fetch(`${window.API_CONFIG.cart.baseUrl}/list`, {
                headers: this.getHeaders()
            });
            
            console.log('获取购物车响应状态:', res.status, res.statusText);
            
            if (!res.ok) {
                const errorText = await res.text();
                console.error('获取购物车失败:', res.status, res.statusText, errorText);
                return [];
            }
            
            const data = await res.json();
            console.log('购物车API响应:', data);
            
            // 处理不同的响应格式
            let cartItems = [];
            if (Array.isArray(data)) {
                cartItems = data;
            } else if (data && Array.isArray(data.cart)) {
                cartItems = data.cart;
            } else if (data && Array.isArray(data.content)) {
                cartItems = data.content;
            } else if (data && data.data && Array.isArray(data.data)) {
                cartItems = data.data;
            } else if (data && data.items && Array.isArray(data.items)) {
                cartItems = data.items;
            } else {
                console.warn('购物车数据格式未知:', data);
                cartItems = [];
            }
            
            console.log('解析后的购物车商品:', cartItems);
            return cartItems;
        } catch (error) {
            console.error('获取购物车网络错误:', error);
            return [];
        }
    },
    
    async updateCart(productId, quantity) {
        console.log('尝试更新购物车商品:', { productId, quantity });
        
        // 如果未登录，提示用户登录
        if (!this.isLoggedIn()) {
            console.log('用户未登录，无法更新购物车');
            return { success: false, message: '请先登录' };
        }

        try {
            const res = await fetch(`${window.API_CONFIG.cart.baseUrl}/update?productId=${productId}&quantity=${quantity}`, {
                method: 'POST',
                headers: this.getHeaders()
            });
            
            console.log('更新购物车响应状态:', res.status, res.statusText);
            
            if (!res.ok) {
                const errorText = await res.text();
                console.error('更新购物车失败:', res.status, res.statusText, errorText);
                return { success: false, message: `更新购物车失败: ${res.status} ${res.statusText}` };
            }
            
            const result = await res.json();
            console.log('更新购物车成功:', result);
            return result;
        } catch (error) {
            console.error('更新购物车网络错误:', error);
            return { success: false, message: '网络错误: ' + error.message };
        }
    },
    
    async removeCart(productId) {
        console.log('尝试删除购物车商品:', productId);
        
        // 如果未登录，提示用户登录
        if (!this.isLoggedIn()) {
            console.log('用户未登录，无法移除购物车商品');
            return { success: false, message: '请先登录' };
        }

        try {
            const res = await fetch(`${window.API_CONFIG.cart.baseUrl}/remove?productId=${productId}`, {
                method: 'POST',
                headers: this.getHeaders()
            });
            
            console.log('删除购物车响应状态:', res.status, res.statusText);
            
            if (!res.ok) {
                const errorText = await res.text();
                console.error('移除购物车失败:', res.status, res.statusText, errorText);
                return { success: false, message: `移除购物车失败: ${res.status} ${res.statusText}` };
            }
            
            const result = await res.json();
            console.log('删除购物车成功:', result);
            return result;
        } catch (error) {
            console.error('移除购物车网络错误:', error);
            return { success: false, message: '网络错误: ' + error.message };
        }
    },
    
    async clearCart() {
        console.log('尝试清空购物车');
        
        // 如果未登录，提示用户登录
        if (!this.isLoggedIn()) {
            console.log('用户未登录，无法清空购物车');
            return { success: false, message: '请先登录' };
        }

        try {
            const res = await fetch(`${window.API_CONFIG.cart.baseUrl}/clear`, {
                method: 'POST',
                headers: this.getHeaders()
            });
            
            console.log('清空购物车响应状态:', res.status, res.statusText);
            
            if (!res.ok) {
                const errorText = await res.text();
                console.error('清空购物车失败:', res.status, res.statusText, errorText);
                return { success: false, message: `清空购物车失败: ${res.status} ${res.statusText}` };
            }
            
            const result = await res.json();
            console.log('清空购物车成功:', result);
            return result;
        } catch (error) {
            console.error('清空购物车网络错误:', error);
            return { success: false, message: '网络错误: ' + error.message };
        }
    },
    
    async getCartStats() {
        try {
            const cart = await this.getCart();
            const totalQuantity = cart.reduce((sum, item) => sum + item.quantity, 0);
            const totalPrice = cart.reduce((sum, item) => sum + item.productPrice * item.quantity, 0);
            return {
                totalItems: cart.length,
                totalQuantity: totalQuantity,
                totalPrice: totalPrice,
                selectedCount: cart.length,
                selectedQuantity: totalQuantity,
                selectedPrice: totalPrice
            };
        } catch (error) {
            console.error('获取购物车统计失败:', error);
            return {
                totalItems: 0,
                totalQuantity: 0,
                totalPrice: 0,
                selectedCount: 0,
                selectedQuantity: 0,
                selectedPrice: 0
            };
        }
    }
};

// 确保CartAPI在全局可用
window.CartAPI = CartAPI; 