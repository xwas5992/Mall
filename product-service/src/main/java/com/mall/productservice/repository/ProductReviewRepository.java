

import com.mall.productservice.model.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    Optional<ProductReview> findByProductIdAndStatus(Long productId, ProductReview.ReviewStatus status);

    Page<ProductReview> findByStatus(ProductReview.ReviewStatus status, Pageable pageable);

    Page<ProductReview> findByReviewerIdAndStatus(Long reviewerId, ProductReview.ReviewStatus status, Pageable pageable);

    Page<ProductReview> findByNextReviewerIdAndStatus(Long nextReviewerId, ProductReview.ReviewStatus status, Pageable pageable);

    @Query("SELECT pr FROM ProductReview pr WHERE pr.product.id = :productId AND pr.status != 'CANCELLED' ORDER BY pr.createdAt DESC")
    List<ProductReview> findActiveReviewsByProductId(@Param("productId") Long productId);

    @Query("SELECT pr FROM ProductReview pr WHERE pr.reviewerId = :reviewerId AND pr.status = 'REVIEWING' AND pr.reviewTime <= :deadline")
    List<ProductReview> findOverdueReviews(@Param("reviewerId") Long reviewerId, @Param("deadline") LocalDateTime deadline);

    @Query("SELECT COUNT(pr) FROM ProductReview pr WHERE pr.status = :status AND pr.reviewTime >= :startTime AND pr.reviewTime <= :endTime")
    Long countReviewsByStatusAndTimeRange(
            @Param("status") ProductReview.ReviewStatus status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT pr.reviewerId, COUNT(pr) FROM ProductReview pr WHERE pr.status = :status AND pr.reviewTime >= :startTime GROUP BY pr.reviewerId")
    List<Object[]> countReviewsByReviewerAndStatus(
            @Param("status") ProductReview.ReviewStatus status,
            @Param("startTime") LocalDateTime startTime);

    @Query("SELECT AVG(TIMESTAMPDIFF(HOUR, pr.createdAt, pr.reviewTime)) FROM ProductReview pr WHERE pr.status = :status AND pr.reviewTime IS NOT NULL")
    Double getAverageReviewTime(@Param("status") ProductReview.ReviewStatus status);

    @Query("SELECT pr FROM ProductReview pr WHERE pr.product.id = :productId AND pr.status = 'APPROVED' AND pr.isFinalReview = true")
    Optional<ProductReview> findFinalApprovedReview(@Param("productId") Long productId);

    @Query("SELECT pr FROM ProductReview pr WHERE pr.product.id = :productId AND pr.status = 'REJECTED' ORDER BY pr.reviewTime DESC")
    List<ProductReview> findRejectedReviews(@Param("productId") Long productId);

    @Query("SELECT pr FROM ProductReview pr WHERE pr.status = 'PENDING' AND pr.reviewLevel = :level ORDER BY pr.createdAt ASC")
    Page<ProductReview> findPendingReviewsByLevel(@Param("level") Integer level, Pageable pageable);

    @Query("SELECT pr FROM ProductReview pr WHERE pr.status = 'REVIEWING' AND pr.reviewTime <= :deadline")
    List<ProductReview> findAllOverdueReviews(@Param("deadline") LocalDateTime deadline);

    @Query("SELECT pr FROM ProductReview pr WHERE pr.product.id = :productId AND pr.status IN ('PENDING', 'REVIEWING')")
    List<ProductReview> findActiveReviewsByProductIdAndStatus(@Param("productId") Long productId);

    @Query("SELECT COUNT(pr) FROM ProductReview pr WHERE pr.status = 'PENDING' AND pr.reviewLevel = :level")
    Long countPendingReviewsByLevel(@Param("level") Integer level);

    @Query("SELECT COUNT(pr) FROM ProductReview pr WHERE pr.status = 'REVIEWING' AND pr.reviewerId = :reviewerId")
    Long countReviewingByReviewer(@Param("reviewerId") Long reviewerId);

    @Query("SELECT pr FROM ProductReview pr WHERE pr.status = 'PENDING' AND pr.reviewLevel = :level AND pr.nextReviewerId = :reviewerId")
    Page<ProductReview> findAssignedPendingReviews(
            @Param("level") Integer level,
            @Param("reviewerId") Long reviewerId,
            Pageable pageable);
} 