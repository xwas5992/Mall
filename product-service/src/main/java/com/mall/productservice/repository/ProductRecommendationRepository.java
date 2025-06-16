

import com.mall.productservice.model.ProductRecommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRecommendationRepository extends JpaRepository<ProductRecommendation, Long> {

    Page<ProductRecommendation> findByUserIdAndIsShownFalseOrderByScoreDesc(
            Long userId, Pageable pageable);

    @Query("SELECT pr FROM ProductRecommendation pr " +
           "WHERE pr.userId = :userId AND pr.isShown = false " +
           "AND pr.type = :type " +
           "ORDER BY pr.score DESC")
    Page<ProductRecommendation> findByUserIdAndTypeAndIsShownFalse(
            @Param("userId") Long userId,
            @Param("type") ProductRecommendation.RecommendationType type,
            Pageable pageable);

    @Query("SELECT pr.productId, COUNT(pr) as clickCount " +
           "FROM ProductRecommendation pr " +
           "WHERE pr.isClicked = true AND pr.clickedAt >= :startTime " +
           "GROUP BY pr.productId " +
           "ORDER BY clickCount DESC")
    List<Object[]> findPopularProducts(@Param("startTime") LocalDateTime startTime, Pageable pageable);

    @Query("SELECT pr FROM ProductRecommendation pr " +
           "WHERE pr.userId = :userId AND pr.isShown = true " +
           "AND pr.createdAt >= :startTime " +
           "ORDER BY pr.createdAt DESC")
    List<ProductRecommendation> findShownRecommendations(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime);

    @Query("SELECT pr.type, COUNT(pr) as count, " +
           "SUM(CASE WHEN pr.isClicked = true THEN 1 ELSE 0 END) as clicks " +
           "FROM ProductRecommendation pr " +
           "WHERE pr.isShown = true AND pr.createdAt >= :startTime " +
           "GROUP BY pr.type")
    List<Object[]> getRecommendationStats(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT pr.productId, AVG(pr.score) as avgScore " +
           "FROM ProductRecommendation pr " +
           "WHERE pr.type = :type AND pr.createdAt >= :startTime " +
           "GROUP BY pr.productId " +
           "ORDER BY avgScore DESC")
    List<Object[]> getTopRatedProductsByType(
            @Param("type") ProductRecommendation.RecommendationType type,
            @Param("startTime") LocalDateTime startTime,
            Pageable pageable);

    void deleteByUserIdAndCreatedAtBefore(Long userId, LocalDateTime time);

    @Query("SELECT COUNT(pr) FROM ProductRecommendation pr " +
           "WHERE pr.userId = :userId AND pr.isShown = true " +
           "AND pr.createdAt >= :startTime")
    long countShownRecommendations(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime);

    @Query("SELECT COUNT(pr) FROM ProductRecommendation pr " +
           "WHERE pr.userId = :userId AND pr.isClicked = true " +
           "AND pr.clickedAt >= :startTime")
    long countClickedRecommendations(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime);
} 