

import com.mall.productservice.model.Review;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long userId;
    private String username;
    private Integer rating;
    private String content;
    private List<String> images;
    private Boolean isVerified;
    private LocalDateTime createdAt;

    public static ReviewResponse fromReview(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setProductId(review.getProduct().getId());
        response.setProductName(review.getProduct().getName());
        response.setUserId(review.getUserId());
        response.setUsername(review.getUsername());
        response.setRating(review.getRating());
        response.setContent(review.getContent());
        response.setImages(review.getImages());
        response.setIsVerified(review.getIsVerified());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
} 