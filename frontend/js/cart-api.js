// 购物车API模块
const CartAPI = {
    getToken() {
        return localStorage.getItem('token') || '';
    },
    getHeaders() {
        return {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + this.getToken()
        };
    },
    async addToCart(product, quantity = 1) {
        try {
            const res = await fetch(`${window.API_CONFIG.cart.baseUrl}/add`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify({
                    productId: product.id,
                    productName: product.name,
                    productImage: product.image || product.imageUrl || '',
                    productPrice: product.price,
                    quantity: quantity
                })
            });
            if (!res.ok) {
                console.error('添加购物车失败:', res.status, res.statusText);
                return { success: false, message: '添加购物车失败' };
            }
            return await res.json();
        } catch (error) {
            console.error('添加购物车网络错误:', error);
            return { success: false, message: '网络错误' };
        }
    },
    async getCart() {
        try {
            const res = await fetch(`${window.API_CONFIG.cart.baseUrl}/list`, {
                headers: this.getHeaders()
            });
            if (!res.ok) {
                console.error('获取购物车失败:', res.status, res.statusText);
                return []; // 返回空数组而不是错误
            }
            const data = await res.json();
            return Array.isArray(data) ? data : []; // 确保返回数组
        } catch (error) {
            console.error('获取购物车网络错误:', error);
            return []; // 网络错误时返回空数组
        }
    },
    async updateCart(productId, quantity) {
        try {
            const res = await fetch(`${window.API_CONFIG.cart.baseUrl}/update?productId=${productId}&quantity=${quantity}`, {
                method: 'POST',
                headers: this.getHeaders()
            });
            if (!res.ok) {
                console.error('更新购物车失败:', res.status, res.statusText);
                return { success: false, message: '更新购物车失败' };
            }
            return await res.json();
        } catch (error) {
            console.error('更新购物车网络错误:', error);
            return { success: false, message: '网络错误' };
        }
    },
    async removeCart(productId) {
        try {
            const res = await fetch(`${window.API_CONFIG.cart.baseUrl}/remove?productId=${productId}`, {
                method: 'POST',
                headers: this.getHeaders()
            });
            if (!res.ok) {
                console.error('移除购物车失败:', res.status, res.statusText);
                const errorText = await res.text();
                console.error('错误详情:', errorText);
                return { success: false, message: `移除购物车失败: ${res.status} ${res.statusText}` };
            }
            const result = await res.text();
            console.log('删除成功:', result);
            return { success: true, message: result };
        } catch (error) {
            console.error('移除购物车网络错误:', error);
            return { success: false, message: '网络错误' };
        }
    },
    async clearCart() {
        try {
            const res = await fetch(`${window.API_CONFIG.cart.baseUrl}/clear`, {
                method: 'POST',
                headers: this.getHeaders()
            });
            if (!res.ok) {
                console.error('清空购物车失败:', res.status, res.statusText);
                return { success: false, message: '清空购物车失败' };
            }
            return await res.json();
        } catch (error) {
            console.error('清空购物车网络错误:', error);
            return { success: false, message: '网络错误' };
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
window.CartAPI = CartAPI; 