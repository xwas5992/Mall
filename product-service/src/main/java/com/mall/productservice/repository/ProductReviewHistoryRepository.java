

import com.mall.productservice.model.ProductReview;
import com.mall.productservice.model.ProductReviewHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductReviewHistoryRepository extends JpaRepository<ProductReviewHistory, Long> {

    List<ProductReviewHistory> findByReviewOrderByOperationTimeDesc(ProductReview review);

    Page<ProductReviewHistory> findByReviewId(Long reviewId, Pageable pageable);

    @Query("SELECT h FROM ProductReviewHistory h WHERE h.review.product.id = :productId ORDER BY h.operationTime DESC")
    Page<ProductReviewHistory> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT h FROM ProductReviewHistory h WHERE h.operatorId = :operatorId AND h.operationTime >= :startTime ORDER BY h.operationTime DESC")
    Page<ProductReviewHistory> findByOperatorAndTimeRange(
            @Param("operatorId") Long operatorId,
            @Param("startTime") LocalDateTime startTime,
            Pageable pageable);

    @Query("SELECT h FROM ProductReviewHistory h WHERE h.review.id = :reviewId AND h.operationType = :operationType ORDER BY h.operationTime DESC")
    List<ProductReviewHistory> findByReviewAndOperationType(
            @Param("reviewId") Long reviewId,
            @Param("operationType") ProductReviewHistory.OperationType operationType);

    @Query("SELECT h FROM ProductReviewHistory h WHERE h.review.product.id = :productId AND h.operationType = :operationType ORDER BY h.operationTime DESC")
    List<ProductReviewHistory> findByProductAndOperationType(
            @Param("productId") Long productId,
            @Param("operationType") ProductReviewHistory.OperationType operationType);

    @Query("SELECT h FROM ProductReviewHistory h WHERE h.review.id = :reviewId AND h.fromStatus = :fromStatus AND h.toStatus = :toStatus ORDER BY h.operationTime DESC")
    List<ProductReviewHistory> findByStatusChange(
            @Param("reviewId") Long reviewId,
            @Param("fromStatus") ProductReview.ReviewStatus fromStatus,
            @Param("toStatus") ProductReview.ReviewStatus toStatus);

    @Query("SELECT COUNT(h) FROM ProductReviewHistory h WHERE h.operatorId = :operatorId AND h.operationType = :operationType AND h.operationTime >= :startTime")
    Long countOperationsByOperatorAndType(
            @Param("operatorId") Long operatorId,
            @Param("operationType") ProductReviewHistory.OperationType operationType,
            @Param("startTime") LocalDateTime startTime);

    @Query("SELECT h.operationType, COUNT(h) FROM ProductReviewHistory h WHERE h.review.id = :reviewId GROUP BY h.operationType")
    List<Object[]> countOperationsByType(@Param("reviewId") Long reviewId);

    @Query("SELECT h FROM ProductReviewHistory h WHERE h.review.id = :reviewId AND h.operationTime >= :startTime AND h.operationTime <= :endTime ORDER BY h.operationTime DESC")
    List<ProductReviewHistory> findByTimeRange(
            @Param("reviewId") Long reviewId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT h FROM ProductReviewHistory h WHERE h.review.id = :reviewId AND h.nextReviewerId = :reviewerId ORDER BY h.operationTime DESC")
    List<ProductReviewHistory> findByNextReviewer(
            @Param("reviewId") Long reviewId,
            @Param("reviewerId") Long reviewerId);

    @Query("SELECT h FROM ProductReviewHistory h WHERE h.review.id = :reviewId AND h.reviewLevel = :level ORDER BY h.operationTime DESC")
    List<ProductReviewHistory> findByReviewLevel(
            @Param("reviewId") Long reviewId,
            @Param("level") Integer level);

    @Query("SELECT h FROM ProductReviewHistory h WHERE h.review.id = :reviewId AND h.isFinalReview = true ORDER BY h.operationTime DESC")
    List<ProductReviewHistory> findFinalReviewHistory(@Param("reviewId") Long reviewId);

    @Query("SELECT h FROM ProductReviewHistory h WHERE h.review.id = :reviewId AND h.rejectReason IS NOT NULL ORDER BY h.operationTime DESC")
    List<ProductReviewHistory> findRejectHistory(@Param("reviewId") Long reviewId);
} 