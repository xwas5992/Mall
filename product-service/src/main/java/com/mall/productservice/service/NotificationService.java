

import com.mall.productservice.model.ProductReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String REVIEW_NOTIFICATION_TOPIC = "product-review-notifications";

    public void sendReviewNotification(ProductReview review, String message) {
        try {
            ReviewNotification notification = ReviewNotification.builder()
                    .reviewId(review.getId())
                    .productId(review.getProduct().getId())
                    .productName(review.getProduct().getName())
                    .reviewerId(review.getReviewerId())
                    .reviewerName(review.getReviewerName())
                    .status(review.getStatus())
                    .message(message)
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaTemplate.send(REVIEW_NOTIFICATION_TOPIC, notification);
            log.info("发送审核通知成功: reviewId={}, message={}", review.getId(), message);
        } catch (Exception e) {
            log.error("发送审核通知失败: reviewId={}, message={}", review.getId(), message, e);
        }
    }

    @lombok.Builder
    @lombok.Data
    public static class ReviewNotification {
        private Long reviewId;
        private Long productId;
        private String productName;
        private Long reviewerId;
        private String reviewerName;
        private ProductReview.ReviewStatus status;
        private String message;
        private Long timestamp;
    }
} 