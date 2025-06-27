document.addEventListener('DOMContentLoaded', function () {
    const authManager = new AuthManager();

    // Check login status. Redirect to login page if not logged in.
    if (!authManager.isUserLoggedIn()) {
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
            const greeting = getGreeting();
            document.getElementById('user-greeting').innerHTML = `${greeting}, <span class="username">${user.username}</span>`;
            
            // Update header user menu
            authManager.updateUserMenu(true);
        }
    }

    updateUserDisplay();
}); 