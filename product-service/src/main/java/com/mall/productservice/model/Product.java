package com.mall.productservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stock;
    
    @Column(length = 100)
    private String brand;
    
    @Column(length = 100)
    private String category;
    
    @Column(length = 500)
    private String imageUrl;
    
    @Column(nullable = false)
    private Boolean status = true; // true: 上架, false: 下架
    
    @Column(name = "category_id")
    private Integer categoryId;
    
    // 首页展示相关字段
    @Column(name = "featured_on_homepage", nullable = false)
    private Boolean featuredOnHomepage = false; // 是否在首页展示
    
    @Column(name = "homepage_sort_order")
    private Integer homepageSortOrder = 0; // 首页排序顺序，数字越小越靠前
    
    @Column(name = "homepage_display_title", length = 100)
    private String homepageDisplayTitle; // 首页显示标题（可选，为空则使用商品名称）
    
    @Column(name = "homepage_display_description", length = 200)
    private String homepageDisplayDescription; // 首页显示描述（可选，为空则使用商品描述）
    
    // 排序相关字段
    @Column(name = "sales", nullable = false)
    private Integer sales = 0; // 销量
    
    @Column(name = "rating", precision = 3, scale = 2, nullable = false)
    private BigDecimal rating = BigDecimal.valueOf(5.0); // 评分，默认5.0
    
    @CreationTimestamp
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime; // 创建时间，用于最新排序
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
} 