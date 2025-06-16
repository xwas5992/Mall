

import com.mall.orderservice.dto.CartItemRequest;
import com.mall.orderservice.model.CartItem;
import com.mall.orderservice.model.Order;
import com.mall.orderservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 添加商品到购物车
     */
    @PostMapping("/items")
    public ResponseEntity<CartItem> addToCart(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userId, request));
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/items/{cartItemId}/quantity")
    public ResponseEntity<CartItem> updateQuantity(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(userId, cartItemId, quantity));
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/items")
    public ResponseEntity<Void> removeFromCart(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam List<Long> cartItemIds) {
        cartService.removeFromCart(userId, cartItemIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新购物车商品选中状态
     */
    @PutMapping("/items/selected")
    public ResponseEntity<Void> updateSelected(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam List<Long> cartItemIds,
            @RequestParam Boolean selected) {
        cartService.updateSelected(userId, cartItemIds, selected);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取购物车列表
     */
    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCartItems(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    /**
     * 获取选中的购物车商品
     */
    @GetMapping("/items/selected")
    public ResponseEntity<List<CartItem>> getSelectedCartItems(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.getSelectedCartItems(userId));
    }

    /**
     * 一键购买（直接购买）
     */
    @PostMapping("/direct-buy")
    public ResponseEntity<Order> directBuy(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.directBuy(userId, request));
    }

    /**
     * 购物车结算
     */
    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam List<Long> cartItemIds) {
        return ResponseEntity.ok(cartService.checkout(userId, cartItemIds));
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
} 