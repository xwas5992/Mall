

import com.mall.productservice.model.ProductReview;
import com.mall.productservice.model.ProductReviewHistory;
import com.mall.productservice.service.ProductReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "商品审核", description = "商品审核相关接口")
public class ProductReviewController {

    private final ProductReviewService reviewService;

    @PostMapping("/products/{productId}/submit")
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "提交审核", description = "提交商品进行审核")
    public ResponseEntity<ProductReview> submitForReview(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "提交人ID") @RequestParam Long submitterId,
            @Parameter(description = "提交人姓名") @RequestParam String submitterName) {
        return ResponseEntity.ok(reviewService.submitForReview(productId, submitterId, submitterName));
    }

    @PostMapping("/{reviewId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "分配审核人", description = "分配审核人处理审核任务")
    public ResponseEntity<ProductReview> assignReviewer(
            @Parameter(description = "审核ID") @PathVariable Long reviewId,
            @Parameter(description = "审核人ID") @RequestParam Long reviewerId,
            @Parameter(description = "审核人姓名") @RequestParam String reviewerName,
            @Parameter(description = "操作人ID") @RequestParam Long operatorId,
            @Parameter(description = "操作人姓名") @RequestParam String operatorName) {
        return ResponseEntity.ok(reviewService.assignReviewer(reviewId, reviewerId, reviewerName, operatorId, operatorName));
    }

    @PostMapping("/{reviewId}/approve")
    @PreAuthorize("hasRole('REVIEWER')")
    @Operation(summary = "通过审核", description = "审核人通过审核")
    public ResponseEntity<ProductReview> approveReview(
            @Parameter(description = "审核ID") @PathVariable Long reviewId,
            @Parameter(description = "审核人ID") @RequestParam Long reviewerId,
            @Parameter(description = "审核人姓名") @RequestParam String reviewerName,
            @Parameter(description = "审核意见") @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(reviewService.approveReview(reviewId, reviewerId, reviewerName, comment));
    }

    @PostMapping("/{reviewId}/reject")
    @PreAuthorize("hasRole('REVIEWER')")
    @Operation(summary = "拒绝审核", description = "审核人拒绝审核")
    public ResponseEntity<ProductReview> rejectReview(
            @Parameter(description = "审核ID") @PathVariable Long reviewId,
            @Parameter(description = "审核人ID") @RequestParam Long reviewerId,
            @Parameter(description = "审核人姓名") @RequestParam String reviewerName,
            @Parameter(description = "拒绝原因") @RequestParam String rejectReason) {
        return ResponseEntity.ok(reviewService.rejectReview(reviewId, reviewerId, reviewerName, rejectReason));
    }

    @PostMapping("/{reviewId}/transfer")
    @PreAuthorize("hasRole('REVIEWER')")
    @Operation(summary = "转交审核", description = "审核人转交审核任务")
    public ResponseEntity<ProductReview> transferReview(
            @Parameter(description = "审核ID") @PathVariable Long reviewId,
            @Parameter(description = "原审核人ID") @RequestParam Long fromReviewerId,
            @Parameter(description = "新审核人ID") @RequestParam Long toReviewerId,
            @Parameter(description = "新审核人姓名") @RequestParam String toReviewerName,
            @Parameter(description = "转交说明") @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(reviewService.transferReview(reviewId, fromReviewerId, toReviewerId, toReviewerName, comment));
    }

    @PostMapping("/{reviewId}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MERCHANT')")
    @Operation(summary = "取消审核", description = "取消审核任务")
    public ResponseEntity<ProductReview> cancelReview(
            @Parameter(description = "审核ID") @PathVariable Long reviewId,
            @Parameter(description = "操作人ID") @RequestParam Long operatorId,
            @Parameter(description = "操作人姓名") @RequestParam String operatorName,
            @Parameter(description = "取消原因") @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(reviewService.cancelReview(reviewId, operatorId, operatorName, comment));
    }

    @PostMapping("/{reviewId}/comment")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER', 'MERCHANT')")
    @Operation(summary = "添加评论", description = "为审核添加评论")
    public ResponseEntity<ProductReview> addComment(
            @Parameter(description = "审核ID") @PathVariable Long reviewId,
            @Parameter(description = "操作人ID") @RequestParam Long operatorId,
            @Parameter(description = "操作人姓名") @RequestParam String operatorName,
            @Parameter(description = "评论内容") @RequestParam String comment) {
        return ResponseEntity.ok(reviewService.addComment(reviewId, operatorId, operatorName, comment));
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "获取审核详情", description = "获取审核任务的详细信息")
    public ResponseEntity<ProductReview> getReview(
            @Parameter(description = "审核ID") @PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "获取审核列表", description = "根据状态获取审核任务列表")
    public ResponseEntity<Page<ProductReview>> getReviewsByStatus(
            @Parameter(description = "审核状态") @PathVariable ProductReview.ReviewStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByStatus(status, pageable));
    }

    @GetMapping("/reviewer/{reviewerId}/status/{status}")
    @Operation(summary = "获取审核人的审核列表", description = "获取指定审核人的审核任务列表")
    public ResponseEntity<Page<ProductReview>> getReviewsByReviewer(
            @Parameter(description = "审核人ID") @PathVariable Long reviewerId,
            @Parameter(description = "审核状态") @PathVariable ProductReview.ReviewStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByReviewer(reviewerId, status, pageable));
    }

    @GetMapping("/products/{productId}/active")
    @Operation(summary = "获取商品的进行中审核", description = "获取指定商品的进行中的审核任务")
    public ResponseEntity<List<ProductReview>> getActiveReviewsByProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getActiveReviewsByProduct(productId));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取超时审核", description = "获取超时的审核任务")
    public ResponseEntity<List<ProductReview>> getOverdueReviews(
            @Parameter(description = "审核人ID") @RequestParam Long reviewerId,
            @Parameter(description = "截止时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadline) {
        return ResponseEntity.ok(reviewService.getOverdueReviews(reviewerId, deadline));
    }

    @GetMapping("/{reviewId}/history")
    @Operation(summary = "获取审核历史", description = "获取审核任务的历史记录")
    public ResponseEntity<List<ProductReviewHistory>> getReviewHistory(
            @Parameter(description = "审核ID") @PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewHistory(reviewId));
    }

    @PostMapping("/cache/clear")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "清除缓存", description = "清除所有审核相关的缓存")
    public ResponseEntity<Void> clearReviewCache() {
        reviewService.clearReviewCache();
        return ResponseEntity.ok().build();
    }
} 