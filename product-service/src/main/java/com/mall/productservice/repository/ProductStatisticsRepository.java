

import com.mall.productservice.model.ProductStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ProductStatisticsRepository extends JpaRepository<ProductStatistics, Long> {

    ProductStatistics findByProductId(Long productId);

    @Query("SELECT ps FROM ProductStatistics ps WHERE ps.product.id = :productId")
    ProductStatistics getByProductId(@Param("productId") Long productId);

    @Query("SELECT ps FROM ProductStatistics ps WHERE ps.product.category.id = :categoryId")
    Page<ProductStatistics> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT ps FROM ProductStatistics ps WHERE ps.product.brand = :brand")
    Page<ProductStatistics> findByBrand(@Param("brand") String brand, Pageable pageable);

    @Query("SELECT ps FROM ProductStatistics ps WHERE ps.totalSales >= :minSales")
    Page<ProductStatistics> findByMinSales(@Param("minSales") BigDecimal minSales, Pageable pageable);

    @Query("SELECT ps FROM ProductStatistics ps WHERE ps.averageRating >= :minRating")
    Page<ProductStatistics> findByMinRating(@Param("minRating") BigDecimal minRating, Pageable pageable);

    @Query("SELECT ps FROM ProductStatistics ps WHERE ps.viewCount >= :minViews")
    Page<ProductStatistics> findByMinViews(@Param("minViews") Long minViews, Pageable pageable);

    @Query("SELECT ps FROM ProductStatistics ps WHERE ps.lastPurchaseTime >= :startTime")
    List<ProductStatistics> findByLastPurchaseTimeAfter(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT ps FROM ProductStatistics ps WHERE ps.lastReviewTime >= :startTime")
    List<ProductStatistics> findByLastReviewTimeAfter(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT ps FROM ProductStatistics ps WHERE ps.lastViewTime >= :startTime")
    List<ProductStatistics> findByLastViewTimeAfter(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT ps.product.category.name as category, COUNT(ps) as count, " +
           "SUM(ps.viewCount) as totalViews, SUM(ps.purchaseCount) as totalPurchases, " +
           "SUM(ps.totalSales) as totalSales " +
           "FROM ProductStatistics ps " +
           "GROUP BY ps.product.category.name")
    List<Map<String, Object>> getCategoryStatistics();

    @Query("SELECT ps.product.brand as brand, COUNT(ps) as count, " +
           "SUM(ps.viewCount) as totalViews, SUM(ps.purchaseCount) as totalPurchases, " +
           "SUM(ps.totalSales) as totalSales " +
           "FROM ProductStatistics ps " +
           "GROUP BY ps.product.brand")
    List<Map<String, Object>> getBrandStatistics();

    @Query("SELECT ps FROM ProductStatistics ps " +
           "WHERE ps.product.id IN :productIds " +
           "ORDER BY ps.totalSales DESC")
    List<ProductStatistics> findByProductIdsOrderBySalesDesc(@Param("productIds") List<Long> productIds);

    @Query("SELECT ps FROM ProductStatistics ps " +
           "WHERE ps.product.id IN :productIds " +
           "ORDER BY ps.viewCount DESC")
    List<ProductStatistics> findByProductIdsOrderByViewsDesc(@Param("productIds") List<Long> productIds);

    @Query("SELECT ps FROM ProductStatistics ps " +
           "WHERE ps.product.id IN :productIds " +
           "ORDER BY ps.averageRating DESC")
    List<ProductStatistics> findByProductIdsOrderByRatingDesc(@Param("productIds") List<Long> productIds);

    @Query("SELECT ps FROM ProductStatistics ps " +
           "WHERE ps.product.id IN :productIds " +
           "ORDER BY ps.favoriteCount DESC")
    List<ProductStatistics> findByProductIdsOrderByFavoritesDesc(@Param("productIds") List<Long> productIds);

    @Query("SELECT ps FROM ProductStatistics ps " +
           "WHERE ps.product.id IN :productIds " +
           "ORDER BY ps.reviewCount DESC")
    List<ProductStatistics> findByProductIdsOrderByReviewsDesc(@Param("productIds") List<Long> productIds);

    @Query("SELECT ps FROM ProductStatistics ps " +
           "WHERE ps.product.id IN :productIds " +
           "ORDER BY ps.cartCount DESC")
    List<ProductStatistics> findByProductIdsOrderByCartAddsDesc(@Param("productIds") List<Long> productIds);

    @Query("SELECT ps FROM ProductStatistics ps " +
           "WHERE ps.product.id IN :productIds " +
           "ORDER BY ps.shareCount DESC")
    List<ProductStatistics> findByProductIdsOrderBySharesDesc(@Param("productIds") List<Long> productIds);

    @Query("SELECT ps FROM ProductStatistics ps " +
           "WHERE ps.product.id IN :productIds " +
           "ORDER BY ps.searchCount DESC")
    List<ProductStatistics> findByProductIdsOrderBySearchesDesc(@Param("productIds") List<Long> productIds);
} 