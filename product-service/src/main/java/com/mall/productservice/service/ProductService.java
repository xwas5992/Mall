package com.mall.productservice.service;

import com.mall.productservice.dto.ProductRequest;
import com.mall.productservice.model.Product;
import com.mall.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    // 创建商品
    @Transactional
    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setBrand(request.getBrand());
        product.setCategoryId(request.getCategoryId());
        product.setImageUrl(request.getImageUrl());
        product.setStatus(request.getStatus());
        
        return productRepository.save(product);
    }
    
    // 根据ID获取商品
    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
    }
    
    // 获取所有商品
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    // 获取上架商品
    @Transactional(readOnly = true)
    public Page<Product> getActiveProducts(Pageable pageable) {
        return productRepository.findByStatus(true, pageable);
    }
    
    // 关键词搜索商品
    @Transactional(readOnly = true)
    public Page<Product> searchByKeyword(String keyword, Pageable pageable) {
        return productRepository.searchByKeyword(keyword, pageable);
    }
    
    // 根据分类搜索商品
    @Transactional(readOnly = true)
    public Page<Product> searchByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryAndStatus(category, true, pageable);
    }
    
    // 根据品牌搜索商品
    @Transactional(readOnly = true)
    public Page<Product> searchByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrandAndStatus(brand, true, pageable);
    }
    
    // 根据价格范围搜索商品
    @Transactional(readOnly = true)
    public Page<Product> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }
    
    // 多条件搜索商品，支持categoryId
    @Transactional(readOnly = true)
    public Page<Product> searchWithFilters(String keyword, String category, Integer categoryId, String brand, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (categoryId != null) {
            return productRepository.searchWithFiltersByCategoryId(keyword, categoryId, brand, minPrice, maxPrice, pageable);
        } else {
            return productRepository.searchWithFilters(keyword, category, brand, minPrice, maxPrice, pageable);
        }
    }
    
    // 更新商品
    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        Product product = getProduct(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setBrand(request.getBrand());
        product.setCategoryId(request.getCategoryId());
        product.setImageUrl(request.getImageUrl());
        product.setStatus(request.getStatus());
        
        return productRepository.save(product);
    }
    
    // 删除商品
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // 获取商品总数
    @Transactional(readOnly = true)
    public long getProductCount() {
        return productRepository.count();
    }

    // 获取所有分类
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    // 获取所有品牌
    @Transactional(readOnly = true)
    public List<String> getAllBrands() {
        return productRepository.findAllBrands();
    }

    // 获取商品统计
    @Transactional(readOnly = true)
    public Map<String, Object> getProductStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总商品数
        long totalProducts = productRepository.count();
        statistics.put("totalProducts", totalProducts);
        
        // 上架商品数
        long activeProducts = productRepository.countByStatus(true);
        statistics.put("activeProducts", activeProducts);
        
        // 下架商品数
        long inactiveProducts = productRepository.countByStatus(false);
        statistics.put("inactiveProducts", inactiveProducts);
        
        // 库存不足商品数（库存小于10）
        long lowStockProducts = productRepository.countByStockLessThan(10);
        statistics.put("lowStockProducts", lowStockProducts);
        
        // 分类统计
        List<Map<String, Object>> categoryStats = productRepository.getCategoryStatistics();
        statistics.put("categoryStatistics", categoryStats);
        
        // 品牌统计
        List<Map<String, Object>> brandStats = productRepository.getBrandStatistics();
        statistics.put("brandStatistics", brandStats);
        
        return statistics;
    }
} 