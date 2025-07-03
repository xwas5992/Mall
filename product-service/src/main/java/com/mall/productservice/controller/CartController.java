package com.mall.productservice.controller;

import com.mall.productservice.model.CartItem;
import com.mall.productservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.mall.productservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;
    private final JwtUtil jwtUtil;
    @Autowired
    private HttpServletRequest request;

    private Long getUserId() {
        try {
            String authHeader = request.getHeader("Authorization");
            log.info("收到Authorization头: {}", authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "null");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.error("Authorization头缺失或格式错误: {}", authHeader);
                throw new RuntimeException("未登录或token缺失");
            }
            
            Long userId = jwtUtil.getUserIdFromToken(authHeader);
            log.info("成功获取用户ID: {}", userId);
            return userId;
            
        } catch (Exception e) {
            log.error("获取用户ID失败: {}", e.getMessage());
            throw new RuntimeException("认证失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public List<CartItem> list() {
        try {
            Long userId = getUserId();
            log.info("获取用户 {} 的购物车列表", userId);
            List<CartItem> cartItems = cartService.getCartByUserId(userId);
            log.info("用户 {} 的购物车包含 {} 个商品", userId, cartItems.size());
            return cartItems;
        } catch (Exception e) {
            log.error("获取购物车列表失败: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/add")
    public CartItem add(@RequestBody CartItem item) {
        try {
            Long userId = getUserId();
            log.info("用户 {} 添加商品到购物车: productId={}, quantity={}", userId, item.getProductId(), item.getQuantity());
            CartItem result = cartService.addToCart(userId, item);
            log.info("成功添加商品到购物车: {}", result.getId());
            return result;
        } catch (Exception e) {
            log.error("添加商品到购物车失败: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestParam Long productId, @RequestParam Integer quantity) {
        try {
            Long userId = getUserId();
            log.info("用户 {} 更新购物车商品: productId={}, quantity={}", userId, productId, quantity);
            cartService.updateCartItem(userId, productId, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "更新成功");
            return response;
        } catch (Exception e) {
            log.error("更新购物车失败: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/remove")
    public Map<String, Object> remove(@RequestParam Long productId) {
        try {
            Long userId = getUserId();
            log.info("用户 {} 删除购物车商品: productId={}", userId, productId);
            cartService.removeCartItem(userId, productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "删除成功");
            return response;
        } catch (Exception e) {
            log.error("删除购物车商品失败: {}", e.getMessage());
            throw new RuntimeException("删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/clear")
    public Map<String, Object> clear() {
        try {
            Long userId = getUserId();
            log.info("用户 {} 清空购物车", userId);
            cartService.clearCart(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "清空成功");
            return response;
        } catch (Exception e) {
            log.error("清空购物车失败: {}", e.getMessage());
            throw e;
        }
    }
} 