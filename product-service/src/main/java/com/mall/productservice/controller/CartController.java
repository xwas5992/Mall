package com.mall.productservice.controller;

import com.mall.productservice.model.CartItem;
import com.mall.productservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.mall.productservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final JwtUtil jwtUtil;
    @Autowired
    private HttpServletRequest request;

    private Long getUserId() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("未登录或token缺失");
        }
        return jwtUtil.getUserIdFromToken(authHeader);
    }

    @GetMapping("/list")
    public List<CartItem> list() {
        return cartService.getCartByUserId(getUserId());
    }

    @PostMapping("/add")
    public CartItem add(@RequestBody CartItem item) {
        return cartService.addToCart(getUserId(), item);
    }

    @PostMapping("/update")
    public void update(@RequestParam Long productId, @RequestParam Integer quantity) {
        cartService.updateCartItem(getUserId(), productId, quantity);
    }

    @PostMapping("/remove")
    public String remove(@RequestParam Long productId) {
        try {
            cartService.removeCartItem(getUserId(), productId);
            return "删除成功";
        } catch (Exception e) {
            throw new RuntimeException("删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/clear")
    public void clear() {
        cartService.clearCart(getUserId());
    }
} 