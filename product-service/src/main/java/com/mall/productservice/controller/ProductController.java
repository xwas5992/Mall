package com.mall.productservice.controller;

import com.mall.productservice.dto.ProductRequest;
import com.mall.productservice.dto.ProductResponse;
import com.mall.productservice.mapper.ProductMapper;
import com.mall.productservice.model.Product;
import com.mall.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品相关的所有接口")
public class ProductController {
    
    private final ProductService productService;
    private final ProductMapper productMapper;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建商品", description = "创建一个新的商品")
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(description = "商品信息") @Valid @RequestBody ProductRequest request) {
        Product product = productService.createProduct(request);
        return new ResponseEntity<>(productMapper.toResponse(product), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "根据ID获取商品详细信息")
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "商品ID") @PathVariable Long id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }
    
    @GetMapping
    @Operation(summary = "获取商品列表", description = "分页获取商品列表")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Product> products = productService.getAllProducts(pageRequest);
        Page<ProductResponse> response = products.map(productMapper::toResponse);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/active")
    @Operation(summary = "获取上架商品", description = "分页获取所有上架商品")
    public ResponseEntity<Page<ProductResponse>> getActiveProducts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.getActiveProducts(PageRequest.of(page, size));
        Page<ProductResponse> response = products.map(productMapper::toResponse);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @Operation(summary = "关键词搜索商品", description = "根据关键词搜索商品")
    public ResponseEntity<Page<ProductResponse>> searchByKeyword(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.searchByKeyword(keyword, PageRequest.of(page, size));
        Page<ProductResponse> response = products.map(productMapper::toResponse);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search/category")
    @Operation(summary = "按分类搜索商品", description = "根据分类搜索商品")
    public ResponseEntity<Page<ProductResponse>> searchByCategory(
            @Parameter(description = "商品分类") @RequestParam String category,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.searchByCategory(category, PageRequest.of(page, size));
        Page<ProductResponse> response = products.map(productMapper::toResponse);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search/brand")
    @Operation(summary = "按品牌搜索商品", description = "根据品牌搜索商品")
    public ResponseEntity<Page<ProductResponse>> searchByBrand(
            @Parameter(description = "商品品牌") @RequestParam String brand,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.searchByBrand(brand, PageRequest.of(page, size));
        Page<ProductResponse> response = products.map(productMapper::toResponse);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search/price")
    @Operation(summary = "按价格范围搜索商品", description = "根据价格范围搜索商品")
    public ResponseEntity<Page<ProductResponse>> searchByPriceRange(
            @Parameter(description = "最低价格") @RequestParam BigDecimal minPrice,
            @Parameter(description = "最高价格") @RequestParam BigDecimal maxPrice,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.searchByPriceRange(minPrice, maxPrice, PageRequest.of(page, size));
        Page<ProductResponse> response = products.map(productMapper::toResponse);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search/filters")
    @Operation(summary = "多条件搜索商品", description = "根据多个条件搜索商品")
    public ResponseEntity<Page<ProductResponse>> searchWithFilters(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "商品分类") @RequestParam(required = false) String category,
            @Parameter(description = "商品分类ID") @RequestParam(required = false) Integer categoryId,
            @Parameter(description = "商品品牌") @RequestParam(required = false) String brand,
            @Parameter(description = "最低价格") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "最高价格") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "排序方式") @RequestParam(required = false) String sortBy,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        // 创建排序对象
        Sort sort = createSort(sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        Page<Product> products = productService.searchWithFilters(keyword, category, categoryId, brand, minPrice, maxPrice, pageRequest);
        Page<ProductResponse> response = products.map(productMapper::toResponse);
        return ResponseEntity.ok(response);
    }
    
    // 创建排序对象
    private Sort createSort(String sortBy) {
        if (sortBy == null || sortBy.isEmpty() || "default".equals(sortBy)) {
            return Sort.by(Sort.Direction.DESC, "id"); // 默认按ID降序
        }
        
        switch (sortBy) {
            case "price-asc":
                return Sort.by(Sort.Direction.ASC, "price");
            case "price-desc":
                return Sort.by(Sort.Direction.DESC, "price");
            case "sales":
                return Sort.by(Sort.Direction.DESC, "sales");
            case "rating":
                return Sort.by(Sort.Direction.DESC, "rating");
            case "newest":
                return Sort.by(Sort.Direction.DESC, "createTime");
            case "name-asc":
                return Sort.by(Sort.Direction.ASC, "name");
            case "name-desc":
                return Sort.by(Sort.Direction.DESC, "name");
            default:
                return Sort.by(Sort.Direction.DESC, "id");
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新商品", description = "根据ID更新商品信息")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "商品ID") @PathVariable Long id,
            @Parameter(description = "商品信息") @Valid @RequestBody ProductRequest request) {
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除商品", description = "根据ID删除商品")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "商品ID") @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // 管理员专用接口
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取商品总数", description = "获取系统商品总数")
    public ResponseEntity<Long> getProductCount() {
        long count = productService.getProductCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取所有分类", description = "获取所有商品分类")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/brands")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取所有品牌", description = "获取所有商品品牌")
    public ResponseEntity<List<String>> getAllBrands() {
        List<String> brands = productService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取商品统计", description = "获取商品相关统计数据")
    public ResponseEntity<Map<String, Object>> getProductStatistics() {
        Map<String, Object> statistics = productService.getProductStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "服务健康状态检查")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "product-service");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }
} 