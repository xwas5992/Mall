

import com.mall.productservice.dto.ReviewRequest;
import com.mall.productservice.dto.ReviewResponse;
import com.mall.productservice.exception.ReviewException;
import com.mall.productservice.model.Review;
import com.mall.productservice.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
@Tag(name = "商品评价", description = "商品评价相关接口")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "创建商品评价", description = "用户对商品进行评价")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        String username = jwt.getClaimAsString("username");
        if (username == null) {
            throw new ReviewException("用户信息不完整");
        }
        Review review = reviewService.createReview(productId, userId, username, request);
        return ResponseEntity.ok(ReviewResponse.fromReview(review));
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "更新商品评价", description = "用户更新自己的评价")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        Review review = reviewService.updateReview(reviewId, userId, request);
        return ResponseEntity.ok(ReviewResponse.fromReview(review));
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "删除商品评价", description = "用户删除自己的评价")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "获取商品评价列表", description = "分页获取商品的评价列表")
    public ResponseEntity<Page<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @Parameter(description = "分页参数") Pageable pageable) {
        Page<Review> reviews = reviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(reviews.map(ReviewResponse::fromReview));
    }

    @GetMapping("/stats")
    @Operation(summary = "获取商品评价统计", description = "获取商品的评价统计数据，包括平均评分、评价数量和评分分布")
    public ResponseEntity<Map<String, Object>> getProductReviewStats(
            @PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviewStats(productId));
    }

    @GetMapping("/verified")
    @Operation(summary = "获取已认证评价", description = "获取商品的已认证评价列表")
    public ResponseEntity<Page<ReviewResponse>> getVerifiedReviews(
            @PathVariable Long productId,
            @Parameter(description = "分页参数") Pageable pageable) {
        Page<Review> reviews = reviewService.getVerifiedReviews(productId, pageable);
        return ResponseEntity.ok(reviews.map(ReviewResponse::fromReview));
    }

    @PostMapping("/{reviewId}/verify")
    @Operation(summary = "认证评价", description = "管理员认证商品评价")
    public ResponseEntity<ReviewResponse> verifyReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId) {
        Review review = reviewService.verifyReview(reviewId);
        return ResponseEntity.ok(ReviewResponse.fromReview(review));
    }
} 