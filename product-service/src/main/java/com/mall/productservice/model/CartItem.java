package com.mall.productservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long productId;
    private String productName;
    private String productImage;
    private Double productPrice;
    private Integer quantity;
    // 可扩展：规格、品牌等
} 