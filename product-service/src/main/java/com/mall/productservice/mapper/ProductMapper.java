package com.mall.productservice.mapper;

import com.mall.productservice.dto.ProductRequest;
import com.mall.productservice.dto.ProductResponse;
import com.mall.productservice.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    
    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setBrand(product.getBrand());
        response.setCategory(product.getCategory());
        response.setImageUrl(product.getImageUrl());
        response.setStatus(product.getStatus());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        response.setCategoryId(product.getCategoryId());
        
        return response;
    }
    
    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }
        
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setBrand(request.getBrand());
        product.setCategoryId(request.getCategoryId());
        product.setImageUrl(request.getImageUrl());
        product.setStatus(request.getStatus());
        
        return product;
    }
} 