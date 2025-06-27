package com.mall.productservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String brand;
    private String category;
    private String imageUrl;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer categoryId;
} 