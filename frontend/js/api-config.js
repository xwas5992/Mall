window.API_CONFIG = {
    auth: {
        baseUrl: 'http://localhost/api/auth'
    },
    product: {
        baseUrl: 'http://localhost/api/products',
        homepageUrl: 'http://localhost/api/homepage'
    },
    user: {
        baseUrl: 'http://localhost/api/user'
    },
    cart: {
        baseUrl: 'http://localhost/api/cart'
    }
};

// 全局fetch配置
window.API_FETCH_CONFIG = {
    mode: 'cors',
    credentials: 'include', // 使用include以支持cookie
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
}; 