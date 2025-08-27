package com.mall.productservice.controller;

import com.mall.productservice.dto.HomepageProductRequest;
import com.mall.productservice.model.Product;
import com.mall.productservice.service.HomepageProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/homepage")
@RequiredArgsConstructor
@Tag(name = "首页商品管理", description = "管理首页展示的商品")
public class HomepageProductController {

    private final HomepageProductService homepageProductService;

    @GetMapping("/health")
    @Operation(summary = "服务健康检查", description = "检查服务运行状态")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "homepage-product-service",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(health);
    }

    @GetMapping("/products/count")
    @Operation(summary = "获取商品总数", description = "获取系统中商品的总数量")
    public ResponseEntity<Long> getProductCount() {
        long totalCount = homepageProductService.getProductRepository().count();
        return ResponseEntity.ok(totalCount);
    }

    @GetMapping("/categories")
    @Operation(summary = "获取商品分类列表", description = "获取系统中所有可用的商品分类")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = homepageProductService.getProductRepository()
            .findAllCategories();
        if (categories.isEmpty()) {
            categories = List.of("电子产品", "服装", "家居", "图书", "其他");
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取商品统计信息", description = "获取商品的统计信息，包括总数、分类统计等")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        long totalProducts = homepageProductService.getProductRepository().count();
        long featuredProducts = homepageProductService.getProductRepository().countByFeaturedOnHomepageTrue();
        long activeProducts = homepageProductService.getProductRepository().countByStatus(true);
        
        Map<String, Object> statistics = Map.of(
            "totalProducts", totalProducts,
            "featuredProducts", featuredProducts,
            "activeProducts", activeProducts,
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/products")
    @Operation(summary = "获取首页展示的商品列表", description = "获取当前在首页展示的所有商品，按排序顺序排列")
    public ResponseEntity<List<Product>> getHomepageProducts() {
        List<Product> products = homepageProductService.getHomepageProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/available")
    @Operation(summary = "获取可设置为首页的商品", description = "获取所有上架状态的可设置为首页的商品")
    public ResponseEntity<List<Product>> getAvailableProductsForHomepage() {
        List<Product> products = homepageProductService.getAvailableProductsForHomepage();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/featured")
    @Operation(summary = "分页获取首页商品", description = "分页获取设置为首页展示的商品")
    public ResponseEntity<Page<Product>> getFeaturedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = homepageProductService.getProductRepository().findByFeaturedOnHomepage(true, pageable);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/products/{productId}/feature")
    @Operation(summary = "设置商品首页展示状态", description = "设置指定商品是否在首页展示")
    public ResponseEntity<Product> setProductHomepageStatus(
            @PathVariable Long productId,
            @RequestParam Boolean featuredOnHomepage) {
        Product product = homepageProductService.setProductHomepageStatus(productId, featuredOnHomepage);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/products/{productId}/sort")
    @Operation(summary = "更新首页商品排序", description = "更新指定商品在首页的排序顺序")
    public ResponseEntity<Product> updateHomepageSortOrder(
            @PathVariable Long productId,
            @RequestParam Integer sortOrder) {
        Product product = homepageProductService.updateHomepageSortOrder(productId, sortOrder);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/products/display-info")
    @Operation(summary = "更新首页商品显示信息", description = "更新商品在首页的显示标题、描述等信息")
    public ResponseEntity<Product> updateHomepageDisplayInfo(@RequestBody HomepageProductRequest request) {
        Product product = homepageProductService.updateHomepageDisplayInfo(request);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/products/batch")
    @Operation(summary = "批量设置首页商品", description = "批量设置多个商品的首页展示信息")
    public ResponseEntity<List<Product>> batchSetHomepageProducts(@RequestBody List<HomepageProductRequest> requests) {
        List<Product> products = homepageProductService.batchSetHomepageProducts(requests);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/products/{productId}/feature")
    @Operation(summary = "从首页下架商品", description = "将指定商品从首页展示中移除")
    public ResponseEntity<Product> removeFromHomepage(@PathVariable Long productId) {
        Product product = homepageProductService.setProductHomepageStatus(productId, false);
        return ResponseEntity.ok(product);
    }
} 