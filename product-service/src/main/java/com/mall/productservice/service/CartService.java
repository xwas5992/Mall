package com.mall.productservice.service;

import com.mall.productservice.model.CartItem;
import com.mall.productservice.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final JdbcTemplate jdbcTemplate;

    public List<CartItem> getCartByUserId(Integer userId) {
        log.info("获取用户 {} 的购物车", userId);
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        log.info("用户 {} 的购物车包含 {} 个商品", userId, items.size());
        return items;
    }

    public CartItem addToCart(Integer userId, CartItem item) {
        log.info("用户 {} 添加商品到购物车: productId={}, quantity={}", userId, item.getProductId(), item.getQuantity());
        
        // 验证用户ID
        if (userId == null || userId <= 0) {
            throw new RuntimeException("无效的用户ID: " + userId);
        }

        // 用户存在性校验（直接查 mall_user.users）
        Boolean exists = jdbcTemplate.queryForObject(
            "SELECT EXISTS(SELECT 1 FROM mall_user.users WHERE id = ?)",
            Boolean.class,
            userId
        );
        if (exists == null || !exists) {
            throw new RuntimeException("用户不存在: " + userId);
        }
        
        // 检查是否已存在相同商品
        List<CartItem> existing = cartItemRepository.findByUserId(userId);
        for (CartItem ci : existing) {
            if (ci.getProductId().equals(item.getProductId())) {
                // 已存在，更新数量
                int newQuantity = ci.getQuantity() + item.getQuantity();
                ci.setQuantity(newQuantity);
                log.info("更新购物车商品数量: productId={}, 新数量={}", item.getProductId(), newQuantity);
                return cartItemRepository.save(ci);
            }
        }
        
        // 不存在，添加新商品
        item.setUserId(userId);
        CartItem savedItem = cartItemRepository.save(item);
        log.info("成功添加商品到购物车: userId={}, productId={}, itemId={}", userId, item.getProductId(), savedItem.getId());
        return savedItem;
    }

    public void updateCartItem(Integer userId, Long productId, Integer quantity) {
        log.info("用户 {} 更新购物车商品: productId={}, quantity={}", userId, productId, quantity);
        
        // 验证用户ID
        if (userId == null || userId <= 0) {
            throw new RuntimeException("无效的用户ID: " + userId);
        }
        
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        for (CartItem ci : items) {
            if (ci.getProductId().equals(productId)) {
                if (quantity <= 0) {
                    // 数量为0或负数，删除商品
                    cartItemRepository.delete(ci);
                    log.info("删除购物车商品: userId={}, productId={}", userId, productId);
                } else {
                    // 更新数量
                    ci.setQuantity(quantity);
                    cartItemRepository.save(ci);
                    log.info("更新购物车商品数量: userId={}, productId={}, 新数量={}", userId, productId, quantity);
                }
                return;
            }
        }
        log.warn("购物车中未找到商品: userId={}, productId={}", userId, productId);
    }

    public void removeCartItem(Integer userId, Long productId) {
        log.info("用户 {} 删除购物车商品: productId={}", userId, productId);
        
        // 验证用户ID
        if (userId == null || userId <= 0) {
            throw new RuntimeException("无效的用户ID: " + userId);
        }
        
        // 先查找要删除的商品
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        for (CartItem item : items) {
            if (item.getProductId().equals(productId)) {
                cartItemRepository.delete(item);
                log.info("成功删除购物车商品: userId={}, productId={}", userId, productId);
                return;
            }
        }
        log.warn("购物车中未找到要删除的商品: userId={}, productId={}", userId, productId);
        throw new RuntimeException("购物车中未找到该商品");
    }

    public void clearCart(Integer userId) {
        log.info("用户 {} 清空购物车", userId);
        
        // 验证用户ID
        if (userId == null || userId <= 0) {
            throw new RuntimeException("无效的用户ID: " + userId);
        }
        
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        cartItemRepository.deleteAll(items);
        log.info("成功清空用户 {} 的购物车，删除了 {} 个商品", userId, items.size());
    }
} 