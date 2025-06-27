/**
 * 购物车管理器（已废弃，所有操作请用CartAPI）
 */
class CartManager {
    constructor() {
        console.warn('CartManager已废弃，请使用window.CartAPI进行购物车操作');
    }
    getCart() { return []; }
    saveCart() { }
    addToCart() { return { success: false, message: '请使用CartAPI.addToCart' }; }
    updateItem() { return { success: false, message: '请使用CartAPI.updateCart' }; }
    removeItem() { return { success: false, message: '请使用CartAPI.removeCart' }; }
    clearCart() { return { success: false, message: '请使用CartAPI.clearCart' }; }
    getTotalQuantity() { return 0; }
    getTotalPrice() { return 0; }
    getSelectedItems() { return []; }
    toggleItemSelection() { return { success: false, message: '请使用CartAPI' }; }
    selectAll() { return { success: false, message: '请使用CartAPI' }; }
    isInCart() { return false; }
    getItemQuantity() { return 0; }
    generateItemKey() { return ''; }
    dispatchCartUpdateEvent() { }
    validateProduct() { return { valid: false, message: '请使用CartAPI' }; }
    getCartStats() { return { totalItems: 0, totalQuantity: 0, totalPrice: 0, selectedCount: 0, selectedQuantity: 0, selectedPrice: 0 }; }
}
window.cartManager = new CartManager(); 