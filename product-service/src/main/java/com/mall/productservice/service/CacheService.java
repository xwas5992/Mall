package com.mall.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存商品信息
     */
    @Cacheable(value = "product", key = "#productId")
    public Object cacheProduct(Long productId, Object productData) {
        log.info("缓存商品信息: {}", productId);
        return productData;
    }

    /**
     * 获取缓存的商品信息
     */
    @Cacheable(value = "product", key = "#productId")
    public Object getProductFromCache(Long productId) {
        log.info("从缓存获取商品信息: {}", productId);
        return null;
    }

    /**
     * 清除商品缓存
     */
    @CacheEvict(value = "product", key = "#productId")
    public void evictProductCache(Long productId) {
        log.info("清除商品缓存: {}", productId);
    }

    /**
     * 缓存商品列表
     */
    public void cacheProductList(String key, List<?> productList, long expirationMinutes) {
        redisTemplate.opsForValue().set("product_list:" + key, productList, expirationMinutes, TimeUnit.MINUTES);
        log.info("缓存商品列表: {} -> {} 个商品", key, productList.size());
    }

    /**
     * 获取缓存的商品列表
     */
    @SuppressWarnings("unchecked")
    public List<?> getProductListFromCache(String key) {
        List<?> productList = (List<?>) redisTemplate.opsForValue().get("product_list:" + key);
        log.info("从缓存获取商品列表: {} -> {}", key, productList != null ? productList.size() + " 个商品" : "null");
        return productList;
    }

    /**
     * 缓存分类信息
     */
    @Cacheable(value = "category", key = "#categoryId")
    public Object cacheCategory(Long categoryId, Object categoryData) {
        log.info("缓存分类信息: {}", categoryId);
        return categoryData;
    }

    /**
     * 获取缓存的分类信息
     */
    @Cacheable(value = "category", key = "#categoryId")
    public Object getCategoryFromCache(Long categoryId) {
        log.info("从缓存获取分类信息: {}", categoryId);
        return null;
    }

    /**
     * 清除分类缓存
     */
    @CacheEvict(value = "category", key = "#categoryId")
    public void evictCategoryCache(Long categoryId) {
        log.info("清除分类缓存: {}", categoryId);
    }

    /**
     * 缓存首页商品
     */
    @Cacheable(value = "homepage", key = "'featured'")
    public Object cacheHomepageProducts(Object homepageData) {
        log.info("缓存首页商品信息");
        return homepageData;
    }

    /**
     * 获取缓存的首页商品
     */
    @Cacheable(value = "homepage", key = "'featured'")
    public Object getHomepageProductsFromCache() {
        log.info("从缓存获取首页商品信息");
        return null;
    }

    /**
     * 清除首页商品缓存
     */
    @CacheEvict(value = "homepage", key = "'featured'")
    public void evictHomepageCache() {
        log.info("清除首页商品缓存");
    }

    /**
     * 缓存搜索结果
     */
    public void cacheSearchResults(String searchKey, Object searchResults, long expirationMinutes) {
        redisTemplate.opsForValue().set("search:" + searchKey, searchResults, expirationMinutes, TimeUnit.MINUTES);
        log.info("缓存搜索结果: {} -> {} 个结果", searchKey, searchResults);
    }

    /**
     * 获取缓存的搜索结果
     */
    public Object getSearchResultsFromCache(String searchKey) {
        Object results = redisTemplate.opsForValue().get("search:" + searchKey);
        log.info("从缓存获取搜索结果: {} -> {}", searchKey, results != null ? "存在" : "不存在");
        return results;
    }

    /**
     * 缓存购物车信息
     */
    public void cacheCart(Long userId, Object cartData, long expirationMinutes) {
        String key = "cart:" + userId;
        redisTemplate.opsForValue().set(key, cartData, expirationMinutes, TimeUnit.MINUTES);
        log.info("缓存购物车信息: 用户{}", userId);
    }

    /**
     * 获取缓存的购物车信息
     */
    public Object getCartFromCache(Long userId) {
        String key = "cart:" + userId;
        Object cart = redisTemplate.opsForValue().get(key);
        log.info("从缓存获取购物车信息: 用户{} -> {}", userId, cart != null ? "存在" : "不存在");
        return cart;
    }

    /**
     * 删除购物车缓存
     */
    public void deleteCartCache(Long userId) {
        String key = "cart:" + userId;
        redisTemplate.delete(key);
        log.info("删除购物车缓存: 用户{}", userId);
    }

    /**
     * 缓存热门商品
     */
    public void cacheHotProducts(String key, List<?> hotProducts, long expirationMinutes) {
        redisTemplate.opsForValue().set("hot_products:" + key, hotProducts, expirationMinutes, TimeUnit.MINUTES);
        log.info("缓存热门商品: {} -> {} 个商品", key, hotProducts.size());
    }

    /**
     * 获取缓存的热门商品
     */
    @SuppressWarnings("unchecked")
    public List<?> getHotProductsFromCache(String key) {
        List<?> hotProducts = (List<?>) redisTemplate.opsForValue().get("hot_products:" + key);
        log.info("从缓存获取热门商品: {} -> {}", key, hotProducts != null ? hotProducts.size() + " 个商品" : "null");
        return hotProducts;
    }

    /**
     * 缓存商品统计信息
     */
    public void cacheProductStats(String key, Object stats, long expirationMinutes) {
        redisTemplate.opsForValue().set("stats:" + key, stats, expirationMinutes, TimeUnit.MINUTES);
        log.info("缓存商品统计信息: {}", key);
    }

    /**
     * 获取缓存的商品统计信息
     */
    public Object getProductStatsFromCache(String key) {
        Object stats = redisTemplate.opsForValue().get("stats:" + key);
        log.info("从缓存获取商品统计信息: {} -> {}", key, stats != null ? "存在" : "不存在");
        return stats;
    }

    /**
     * 清除所有商品相关缓存
     */
    public void clearAllProductCache() {
        // 这里可以添加清除所有商品相关缓存的逻辑
        log.info("清除所有商品相关缓存");
    }

    /**
     * 设置缓存过期时间
     */
    public void setExpire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
        log.info("设置缓存过期时间: {} -> {} {}", key, timeout, unit);
    }

    /**
     * 检查key是否存在
     */
    public boolean hasKey(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        log.info("检查key是否存在: {} -> {}", key, exists);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 删除缓存
     */
    public void deleteCache(String key) {
        redisTemplate.delete(key);
        log.info("删除缓存: {}", key);
    }
} 