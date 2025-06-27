package com.mall.productservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    
    @NotBlank(message = "商品名称不能为空")
    private String name;
    
    private String description;
    
    @NotNull(message = "商品价格不能为空")
    @Min(value = 0, message = "商品价格必须大于等于0")
    private BigDecimal price;
    
    @NotNull(message = "商品库存不能为空")
    @Min(value = 0, message = "商品库存必须大于等于0")
    private Integer stock;
    
    private String brand;
    
    private Integer categoryId;
    
    private String imageUrl;
    
    private Boolean status = true;
} 