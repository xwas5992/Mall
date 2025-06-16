

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.orderservice.dto.CartItemRequest;
import com.mall.orderservice.dto.OrderCreateRequest;
import com.mall.orderservice.exception.BusinessException;
import com.mall.orderservice.model.CartItem;
import com.mall.orderservice.model.Order;
import com.mall.orderservice.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    /**
     * 添加商品到购物车
     */
    @Transactional
    public CartItem addToCart(Long userId, CartItemRequest request) {
        log.info("添加商品到购物车 - 用户ID: {}, 商品ID: {}, SKU: {}", userId, request.getProductId(), request.getProductSku());

        // 1. 获取商品信息
        Map<String, Object> productInfo = productService.getProductInfo(request.getProductId());
        if (productInfo == null) {
            throw new BusinessException("PRODUCT_NOT_FOUND", "商品不存在");
        }

        // 2. 验证商品SKU
        Map<String, Object> skuInfo = productService.getProductSkuInfo(request.getProductId(), request.getProductSku());
        if (skuInfo == null) {
            throw new BusinessException("PRODUCT_SKU_NOT_FOUND", "商品SKU不存在");
        }

        // 3. 验证库存
        Integer stock = (Integer) skuInfo.get("stock");
        if (stock < request.getQuantity()) {
            throw new BusinessException("INSUFFICIENT_STOCK", "商品库存不足");
        }

        // 4. 检查购物车是否已存在该商品
        CartItem existingItem = cartItemRepository.findByUserIdAndProductIdAndProductSku(
            userId, request.getProductId(), request.getProductSku()).orElse(null);

        if (existingItem != null) {
            // 更新数量
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > stock) {
                throw new BusinessException("INSUFFICIENT_STOCK", "商品库存不足");
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setTotalPrice(existingItem.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));
            return cartItemRepository.save(existingItem);
        } else {
            // 创建新的购物车项
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setProductName((String) productInfo.get("name"));
            cartItem.setProductSku(request.getProductSku());
            cartItem.setProductImage((String) productInfo.get("image"));
            cartItem.setQuantity(request.getQuantity());
            cartItem.setUnitPrice(new BigDecimal(skuInfo.get("price").toString()));
            cartItem.setTotalPrice(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItem.setSelected(true);
            if (request.getProperties() != null) {
                try {
                    cartItem.setProperties(objectMapper.writeValueAsString(request.getProperties()));
                } catch (Exception e) {
                    log.error("序列化商品属性失败", e);
                }
            }
            return cartItemRepository.save(cartItem);
        }
    }

    /**
     * 更新购物车商品数量
     */
    @Transactional
    public CartItem updateQuantity(Long userId, Long cartItemId, Integer quantity) {
        log.info("更新购物车商品数量 - 用户ID: {}, 购物车项ID: {}, 数量: {}", userId, cartItemId, quantity);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new BusinessException("CART_ITEM_NOT_FOUND", "购物车项不存在"));

        if (!cartItem.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "无权操作此购物车项");
        }

        // 验证库存
        Map<String, Object> skuInfo = productService.getProductSkuInfo(cartItem.getProductId(), cartItem.getProductSku());
        Integer stock = (Integer) skuInfo.get("stock");
        if (stock < quantity) {
            throw new BusinessException("INSUFFICIENT_STOCK", "商品库存不足");
        }

        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        return cartItemRepository.save(cartItem);
    }

    /**
     * 删除购物车商品
     */
    @Transactional
    public void removeFromCart(Long userId, List<Long> cartItemIds) {
        log.info("删除购物车商品 - 用户ID: {}, 购物车项ID: {}", userId, cartItemIds);
        cartItemRepository.deleteByUserIdAndIds(userId, cartItemIds);
    }

    /**
     * 更新购物车商品选中状态
     */
    @Transactional
    public void updateSelected(Long userId, List<Long> cartItemIds, Boolean selected) {
        log.info("更新购物车商品选中状态 - 用户ID: {}, 购物车项ID: {}, 选中状态: {}", userId, cartItemIds, selected);
        cartItemRepository.updateSelectedByUserIdAndIds(userId, cartItemIds, selected);
    }

    /**
     * 获取购物车列表
     */
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    /**
     * 获取选中的购物车商品
     */
    @Transactional(readOnly = true)
    public List<CartItem> getSelectedCartItems(Long userId) {
        return cartItemRepository.findByUserIdAndSelected(userId, true);
    }

    /**
     * 一键购买（直接购买）
     */
    @Transactional
    public Order directBuy(Long userId, CartItemRequest request) {
        log.info("一键购买 - 用户ID: {}, 商品ID: {}, SKU: {}", userId, request.getProductId(), request.getProductSku());

        // 1. 创建订单请求
        OrderCreateRequest orderRequest = new OrderCreateRequest();
        orderRequest.setUserId(userId);
        orderRequest.setItems(new ArrayList<>());

        // 2. 添加商品信息
        OrderCreateRequest.OrderItemRequest itemRequest = new OrderCreateRequest.OrderItemRequest();
        itemRequest.setProductId(request.getProductId());
        itemRequest.setProductSku(request.getProductSku());
        itemRequest.setQuantity(request.getQuantity());
        if (request.getProperties() != null) {
            itemRequest.setProperties(request.getProperties());
        }
        orderRequest.getItems().add(itemRequest);

        // 3. 创建订单
        return orderService.createOrder(orderRequest);
    }

    /**
     * 购物车结算
     */
    @Transactional
    public Order checkout(Long userId, List<Long> cartItemIds) {
        log.info("购物车结算 - 用户ID: {}, 购物车项ID: {}", userId, cartItemIds);

        // 1. 获取选中的购物车商品
        List<CartItem> selectedItems = cartItemRepository.findByUserIdAndSelected(userId, true).stream()
            .filter(item -> cartItemIds.contains(item.getId()))
            .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            throw new BusinessException("NO_ITEMS_SELECTED", "请选择要购买的商品");
        }

        // 2. 创建订单请求
        OrderCreateRequest orderRequest = new OrderCreateRequest();
        orderRequest.setUserId(userId);
        orderRequest.setItems(selectedItems.stream()
            .map(item -> {
                OrderCreateRequest.OrderItemRequest itemRequest = new OrderCreateRequest.OrderItemRequest();
                itemRequest.setProductId(item.getProductId());
                itemRequest.setProductSku(item.getProductSku());
                itemRequest.setQuantity(item.getQuantity());
                try {
                    if (item.getProperties() != null) {
                        itemRequest.setProperties(objectMapper.readValue(item.getProperties(), Map.class));
                    }
                } catch (Exception e) {
                    log.error("反序列化商品属性失败", e);
                }
                return itemRequest;
            })
            .collect(Collectors.toList()));

        // 3. 创建订单
        Order order = orderService.createOrder(orderRequest);

        // 4. 删除已结算的购物车商品
        cartItemRepository.deleteByUserIdAndIds(userId, cartItemIds);

        return order;
    }

    /**
     * 清空购物车
     */
    @Transactional
    public void clearCart(Long userId) {
        log.info("清空购物车 - 用户ID: {}", userId);
        cartItemRepository.deleteByUserId(userId);
    }
} 