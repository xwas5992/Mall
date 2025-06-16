

import com.mall.productservice.dto.ReviewRequest;
import com.mall.productservice.exception.ResourceNotFoundException;
import com.mall.productservice.exception.ReviewException;
import com.mall.productservice.model.Product;
import com.mall.productservice.model.Review;
import com.mall.productservice.repository.ProductRepository;
import com.mall.productservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    @Transactional
    @CacheEvict(value = {"productReviews", "productReviewStats", "verifiedReviews"}, key = "#productId")
    public Review createReview(Long productId, Long userId, String username, ReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new ReviewException("您已经评价过该商品");
        }

        Review review = new Review();
        review.setProduct(product);
        review.setUserId(userId);
        review.setUsername(username);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages());

        return reviewRepository.save(review);
    }

    @Transactional
    @CacheEvict(value = {"productReviews", "productReviewStats", "verifiedReviews"}, key = "#productId")
    public Review updateReview(Long reviewId, Long userId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getUserId().equals(userId)) {
            throw new ReviewException("无权修改此评价");
        }

        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages());

        return reviewRepository.save(review);
    }

    @Transactional
    @CacheEvict(value = {"productReviews", "productReviewStats", "verifiedReviews"}, key = "#productId")
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getUserId().equals(userId)) {
            throw new ReviewException("无权删除此评价");
        }

        review.setIsVisible(false);
        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "productReviews", key = "#productId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Review> getProductReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndIsVisibleTrue(productId, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "userReviews", key = "#userId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Review> getUserReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "productReviewStats", key = "#productId")
    public Map<String, Object> getProductReviewStats(Long productId) {
        Double averageRating = reviewRepository.getAverageRating(productId);
        Long reviewCount = reviewRepository.getReviewCount(productId);
        List<Object[]> ratingDistribution = reviewRepository.getRatingDistribution(productId);

        Map<Integer, Long> distribution = ratingDistribution.stream()
                .collect(Collectors.toMap(
                    arr -> (Integer) arr[0],
                    arr -> (Long) arr[1]
                ));

        return Map.of(
            "averageRating", averageRating != null ? averageRating : 0.0,
            "reviewCount", reviewCount,
            "ratingDistribution", distribution
        );
    }

    @Transactional
    @CacheEvict(value = {"productReviews", "productReviewStats", "verifiedReviews"}, key = "#productId")
    public Review verifyReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        review.setIsVerified(true);
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "verifiedReviews", key = "#productId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Review> getVerifiedReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndIsVerifiedTrueAndIsVisibleTrue(productId, pageable);
    }
} 