package com.mall.productservice.dto;

import lombok.Data;

@Data
public class HomepageProductRequest {
    private Long productId;
    private Boolean featuredOnHomepage;
    private Integer homepageSortOrder;
    private String homepageDisplayTitle;
    private String homepageDisplayDescription;
} 