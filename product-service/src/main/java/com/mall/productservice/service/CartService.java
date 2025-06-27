package com.mall.productservice.service;

import com.mall.productservice.model.CartItem;
import com.mall.productservice.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;

    public List<CartItem> getCartByUserId(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public CartItem addToCart(Long userId, CartItem item) {
        // 检查是否已存在相同商品
        List<CartItem> existing = cartItemRepository.findByUserId(userId);
        for (CartItem ci : existing) {
            if (ci.getProductId().equals(item.getProductId())) {
                // 已存在，更新数量
                ci.setQuantity(ci.getQuantity() + item.getQuantity());
                return cartItemRepository.save(ci);
            }
        }
        // 不存在，添加新商品
        item.setUserId(userId);
        return cartItemRepository.save(item);
    }

    public void updateCartItem(Long userId, Long productId, Integer quantity) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        for (CartItem ci : items) {
            if (ci.getProductId().equals(productId)) {
                if (quantity <= 0) {
                    // 数量为0或负数，删除商品
                    cartItemRepository.delete(ci);
                } else {
                    // 更新数量
                    ci.setQuantity(quantity);
                    cartItemRepository.save(ci);
                }
                return;
            }
        }
        // 商品不存在，忽略操作
    }

    public void removeCartItem(Long userId, Long productId) {
        // 先查找要删除的商品
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        for (CartItem item : items) {
            if (item.getProductId().equals(productId)) {
                cartItemRepository.delete(item);
                return;
            }
        }
        // 如果没找到，抛出异常或记录日志
        throw new RuntimeException("购物车中未找到该商品");
    }

    public void clearCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        cartItemRepository.deleteAll(items);
    }
} 