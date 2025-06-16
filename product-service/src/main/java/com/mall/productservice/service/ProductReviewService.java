

import com.mall.productservice.model.Product;
import com.mall.productservice.model.ProductReview;
import com.mall.productservice.model.ProductReviewHistory;
import com.mall.productservice.repository.ProductRepository;
import com.mall.productservice.repository.ProductReviewHistoryRepository;
import com.mall.productservice.repository.ProductReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductReviewHistoryRepository historyRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Transactional
    public ProductReview submitForReview(Long productId, Long submitterId, String submitterName) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("商品不存在"));

        // 检查是否已有进行中的审核
        List<ProductReview> activeReviews = reviewRepository.findActiveReviewsByProductIdAndStatus(productId);
        if (!activeReviews.isEmpty()) {
            throw new IllegalStateException("该商品已有进行中的审核");
        }

        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setStatus(ProductReview.ReviewStatus.PENDING);
        review.setReviewerId(submitterId);
        review.setReviewerName(submitterName);
        review.setReviewLevel(1);
        review.setIsFinalReview(false);

        review = reviewRepository.save(review);

        // 创建审核历史记录
        createReviewHistory(review, null, ProductReview.ReviewStatus.PENDING,
                submitterId, submitterName, "提交审核", ProductReviewHistory.OperationType.SUBMIT);

        // 发送通知
        notificationService.sendReviewNotification(review, "商品审核已提交");

        return review;
    }

    @Transactional
    public ProductReview assignReviewer(Long reviewId, Long reviewerId, String reviewerName, Long operatorId, String operatorName) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("审核记录不存在"));

        if (review.getStatus() != ProductReview.ReviewStatus.PENDING) {
            throw new IllegalStateException("只能分配待审核状态的审核");
        }

        ProductReview.ReviewStatus oldStatus = review.getStatus();
        review.setStatus(ProductReview.ReviewStatus.REVIEWING);
        review.setReviewerId(reviewerId);
        review.setReviewerName(reviewerName);
        review.setReviewTime(LocalDateTime.now());

        review = reviewRepository.save(review);

        // 创建审核历史记录
        createReviewHistory(review, oldStatus, ProductReview.ReviewStatus.REVIEWING,
                operatorId, operatorName, "分配审核人", ProductReviewHistory.OperationType.ASSIGN);

        // 发送通知
        notificationService.sendReviewNotification(review, "您有新的审核任务");

        return review;
    }

    @Transactional
    public ProductReview approveReview(Long reviewId, Long reviewerId, String reviewerName, String comment) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("审核记录不存在"));

        if (review.getStatus() != ProductReview.ReviewStatus.REVIEWING || !review.getReviewerId().equals(reviewerId)) {
            throw new IllegalStateException("无权进行此操作");
        }

        ProductReview.ReviewStatus oldStatus = review.getStatus();
        review.setStatus(ProductReview.ReviewStatus.APPROVED);
        review.setComment(comment);
        review.setReviewTime(LocalDateTime.now());

        // 如果是最终审核，更新商品状态
        if (review.getIsFinalReview()) {
            Product product = review.getProduct();
            product.setStatus(Product.Status.ON_SALE);
            productRepository.save(product);
        }

        review = reviewRepository.save(review);

        // 创建审核历史记录
        createReviewHistory(review, oldStatus, ProductReview.ReviewStatus.APPROVED,
                reviewerId, reviewerName, comment, ProductReviewHistory.OperationType.APPROVE);

        // 发送通知
        notificationService.sendReviewNotification(review, "审核已通过");

        return review;
    }

    @Transactional
    public ProductReview rejectReview(Long reviewId, Long reviewerId, String reviewerName, String rejectReason) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("审核记录不存在"));

        if (review.getStatus() != ProductReview.ReviewStatus.REVIEWING || !review.getReviewerId().equals(reviewerId)) {
            throw new IllegalStateException("无权进行此操作");
        }

        ProductReview.ReviewStatus oldStatus = review.getStatus();
        review.setStatus(ProductReview.ReviewStatus.REJECTED);
        review.setRejectReason(rejectReason);
        review.setReviewTime(LocalDateTime.now());

        review = reviewRepository.save(review);

        // 创建审核历史记录
        createReviewHistory(review, oldStatus, ProductReview.ReviewStatus.REJECTED,
                reviewerId, reviewerName, rejectReason, ProductReviewHistory.OperationType.REJECT);

        // 发送通知
        notificationService.sendReviewNotification(review, "审核未通过");

        return review;
    }

    @Transactional
    public ProductReview transferReview(Long reviewId, Long fromReviewerId, Long toReviewerId, String toReviewerName, String comment) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("审核记录不存在"));

        if (review.getStatus() != ProductReview.ReviewStatus.REVIEWING || !review.getReviewerId().equals(fromReviewerId)) {
            throw new IllegalStateException("无权进行此操作");
        }

        review.setReviewerId(toReviewerId);
        review.setReviewerName(toReviewerName);
        review.setReviewTime(LocalDateTime.now());

        review = reviewRepository.save(review);

        // 创建审核历史记录
        createReviewHistory(review, review.getStatus(), review.getStatus(),
                fromReviewerId, review.getReviewerName(), comment, ProductReviewHistory.OperationType.TRANSFER);

        // 发送通知
        notificationService.sendReviewNotification(review, "审核任务已转交");

        return review;
    }

    @Transactional
    public ProductReview cancelReview(Long reviewId, Long operatorId, String operatorName, String comment) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("审核记录不存在"));

        if (review.getStatus() == ProductReview.ReviewStatus.APPROVED) {
            throw new IllegalStateException("已通过的审核不能取消");
        }

        ProductReview.ReviewStatus oldStatus = review.getStatus();
        review.setStatus(ProductReview.ReviewStatus.CANCELLED);

        review = reviewRepository.save(review);

        // 创建审核历史记录
        createReviewHistory(review, oldStatus, ProductReview.ReviewStatus.CANCELLED,
                operatorId, operatorName, comment, ProductReviewHistory.OperationType.CANCEL);

        // 发送通知
        notificationService.sendReviewNotification(review, "审核已取消");

        return review;
    }

    @Transactional
    public ProductReview addComment(Long reviewId, Long operatorId, String operatorName, String comment) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("审核记录不存在"));

        // 创建审核历史记录
        createReviewHistory(review, review.getStatus(), review.getStatus(),
                operatorId, operatorName, comment, ProductReviewHistory.OperationType.COMMENT);

        return review;
    }

    @Cacheable(value = "review", key = "#reviewId")
    public ProductReview getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("审核记录不存在"));
    }

    @Cacheable(value = "reviews", key = "#status + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductReview> getReviewsByStatus(ProductReview.ReviewStatus status, Pageable pageable) {
        return reviewRepository.findByStatus(status, pageable);
    }

    @Cacheable(value = "reviewer-reviews", key = "#reviewerId + '-' + #status + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductReview> getReviewsByReviewer(Long reviewerId, ProductReview.ReviewStatus status, Pageable pageable) {
        return reviewRepository.findByReviewerIdAndStatus(reviewerId, status, pageable);
    }

    public List<ProductReview> getActiveReviewsByProduct(Long productId) {
        return reviewRepository.findActiveReviewsByProductId(productId);
    }

    public List<ProductReview> getOverdueReviews(Long reviewerId, LocalDateTime deadline) {
        return reviewRepository.findOverdueReviews(reviewerId, deadline);
    }

    @Cacheable(value = "review-history", key = "#reviewId")
    public List<ProductReviewHistory> getReviewHistory(Long reviewId) {
        return historyRepository.findByReviewId(reviewId, Pageable.unpaged()).getContent();
    }

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    @Transactional
    public void cleanupExpiredReviews() {
        LocalDateTime deadline = LocalDateTime.now().minusDays(7);
        List<ProductReview> overdueReviews = reviewRepository.findAllOverdueReviews(deadline);
        
        for (ProductReview review : overdueReviews) {
            review.setStatus(ProductReview.ReviewStatus.CANCELLED);
            reviewRepository.save(review);
            
            createReviewHistory(review, ProductReview.ReviewStatus.REVIEWING, ProductReview.ReviewStatus.CANCELLED,
                    review.getReviewerId(), review.getReviewerName(), "系统自动取消超时审核", ProductReviewHistory.OperationType.CANCEL);
            
            notificationService.sendReviewNotification(review, "审核已超时自动取消");
        }
    }

    private ProductReviewHistory createReviewHistory(ProductReview review, ProductReview.ReviewStatus fromStatus,
                                                   ProductReview.ReviewStatus toStatus, Long operatorId, String operatorName,
                                                   String comment, ProductReviewHistory.OperationType operationType) {
        ProductReviewHistory history = new ProductReviewHistory();
        history.setReview(review);
        history.setFromStatus(fromStatus != null ? fromStatus : review.getStatus());
        history.setToStatus(toStatus);
        history.setOperatorId(operatorId);
        history.setOperatorName(operatorName);
        history.setComment(comment);
        history.setOperationType(operationType);
        history.setReviewLevel(review.getReviewLevel());
        history.setIsFinalReview(review.getIsFinalReview());
        history.setNextReviewerId(review.getNextReviewerId());
        history.setNextReviewerName(review.getNextReviewerName());
        history.setRejectReason(review.getRejectReason());
        history.setReviewAttachments(review.getReviewAttachments());

        return historyRepository.save(history);
    }

    @CacheEvict(value = {"review", "reviews", "reviewer-reviews", "review-history"}, allEntries = true)
    public void clearReviewCache() {
        // 清除所有审核相关缓存
    }
} 