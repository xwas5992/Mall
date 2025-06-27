package com.mall.productservice.repository;

import com.mall.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // 根据关键词搜索商品（名称、描述、品牌、分类）
    @Query("SELECT p FROM Product p WHERE p.status = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // 根据分类搜索商品
    Page<Product> findByCategoryAndStatus(String category, Boolean status, Pageable pageable);
    
    // 根据品牌搜索商品
    Page<Product> findByBrandAndStatus(String brand, Boolean status, Pageable pageable);
    
    // 获取上架商品
    Page<Product> findByStatus(Boolean status, Pageable pageable);
    
    // 根据价格范围搜索商品
    @Query("SELECT p FROM Product p WHERE p.status = true AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice, 
                                  @Param("maxPrice") java.math.BigDecimal maxPrice, 
                                  Pageable pageable);
    
    // 根据多个条件搜索商品
    @Query("SELECT p FROM Product p WHERE p.status = true AND " +
           "(:keyword IS NULL OR " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')))) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:brand IS NULL OR p.brand = :brand) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchWithFilters(@Param("keyword") String keyword,
                                   @Param("category") String category,
                                   @Param("brand") String brand,
                                   @Param("minPrice") java.math.BigDecimal minPrice,
                                   @Param("maxPrice") java.math.BigDecimal maxPrice,
                                   Pageable pageable);

    // 根据categoryId搜索商品
    Page<Product> findByCategoryIdAndStatus(Integer categoryId, Boolean status, Pageable pageable);

    // 多条件搜索，支持categoryId
    @Query("SELECT p FROM Product p WHERE p.status = true AND " +
           "(:keyword IS NULL OR " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')))) AND " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
           "(:brand IS NULL OR p.brand = :brand) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchWithFiltersByCategoryId(@Param("keyword") String keyword,
                                               @Param("categoryId") Integer categoryId,
                                               @Param("brand") String brand,
                                               @Param("minPrice") java.math.BigDecimal minPrice,
                                               @Param("maxPrice") java.math.BigDecimal maxPrice,
                                               Pageable pageable);

    // 管理员相关方法
    
    // 根据状态统计商品数量
    long countByStatus(Boolean status);
    
    // 统计库存不足的商品数量
    long countByStockLessThan(Integer stock);
    
    // 获取所有分类
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL")
    List<String> findAllCategories();
    
    // 获取所有品牌
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL")
    List<String> findAllBrands();
    
    // 获取分类统计
    @Query("SELECT p.category as category, COUNT(p) as count FROM Product p WHERE p.category IS NOT NULL GROUP BY p.category")
    List<Map<String, Object>> getCategoryStatistics();
    
    // 获取品牌统计
    @Query("SELECT p.brand as brand, COUNT(p) as count FROM Product p WHERE p.brand IS NOT NULL GROUP BY p.brand")
    List<Map<String, Object>> getBrandStatistics();
    
    // 首页商品相关方法
    
    // 获取首页展示的商品，按排序顺序排列
    List<Product> findByFeaturedOnHomepageTrueOrderByHomepageSortOrderAsc();
    
    // 获取所有可设置为首页的商品（上架状态）
    List<Product> findByStatusTrueOrderByNameAsc();
    
    // 获取首页商品的最大排序值
    @Query("SELECT MAX(p.homepageSortOrder) FROM Product p WHERE p.featuredOnHomepage = true")
    Integer findMaxHomepageSortOrder();
    
    // 统计首页商品数量
    long countByFeaturedOnHomepageTrue();
    
    // 根据首页展示状态获取商品
    Page<Product> findByFeaturedOnHomepage(Boolean featuredOnHomepage, Pageable pageable);
} 