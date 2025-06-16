package com.mall.orderservice.service;

import com.mall.orderservice.model.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "product-service", path = "/api/v1/products")
public interface ProductService {
    
    /**
     * 获取商品信息
     */
    @GetMapping("/{productId}")
    Map<String, Object> getProduct(@PathVariable("productId") Long productId);
    
    /**
     * 获取商品SKU信息
     */
    @GetMapping("/{productId}/skus/{skuCode}")
    Map<String, Object> getProductSku(@PathVariable("productId") Long productId, 
            @PathVariable("skuCode") String skuCode);
    
    /**
     * 批量获取商品信息
     */
    @PostMapping("/batch")
    List<Map<String, Object>> getProducts(@RequestBody List<Long> productIds);
    
    /**
     * 批量获取商品SKU信息
     */
    @PostMapping("/skus/batch")
    List<Map<String, Object>> getProductSkus(@RequestBody List<String> skuCodes);
    
    /**
     * 扣减库存
     */
    @PostMapping("/stock/decrease")
    void decreaseStock(@RequestBody List<OrderItem> orderItems);
    
    /**
     * 恢复库存
     */
    @PostMapping("/stock/increase")
    void increaseStock(@RequestBody List<OrderItem> orderItems);
    
    /**
     * 检查库存
     */
    @PostMapping("/stock/check")
    boolean checkStock(@RequestBody List<OrderItem> orderItems);
    
    /**
     * 锁定库存
     */
    @PostMapping("/stock/lock")
    void lockStock(@RequestBody List<OrderItem> orderItems);
    
    /**
     * 解锁库存
     */
    @PostMapping("/stock/unlock")
    void unlockStock(@RequestBody List<OrderItem> orderItems);
} 