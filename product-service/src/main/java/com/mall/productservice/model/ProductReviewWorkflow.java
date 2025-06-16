

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "product_review_workflows")
public class ProductReviewWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "min_price")
    private Double minPrice;

    @Column(name = "max_price")
    private Double maxPrice;

    @Column(name = "review_levels", nullable = false)
    private Integer reviewLevels;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "timeout_hours", nullable = false)
    private Integer timeoutHours;

    @Column(name = "auto_approve", nullable = false)
    private Boolean autoApprove;

    @Column(name = "auto_approve_hours")
    private Integer autoApproveHours;

    @Column(name = "required_attachments")
    private String requiredAttachments;

    @ElementCollection
    @CollectionTable(name = "product_review_workflow_steps", joinColumns = @JoinColumn(name = "workflow_id"))
    @OrderBy("level ASC")
    private List<ReviewStep> steps;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @Data
    @Embeddable
    public static class ReviewStep {
        @Column(name = "level", nullable = false)
        private Integer level;

        @Column(name = "role_name", nullable = false)
        private String roleName;

        @Column(name = "is_final", nullable = false)
        private Boolean isFinal;

        @Column(name = "can_reject", nullable = false)
        private Boolean canReject;

        @Column(name = "can_transfer", nullable = false)
        private Boolean canTransfer;

        @Column(name = "can_comment", nullable = false)
        private Boolean canComment;

        @Column(name = "timeout_hours")
        private Integer timeoutHours;

        @Column(name = "required_checks")
        private String requiredChecks;

        @Column(name = "next_level_conditions")
        private String nextLevelConditions;
    }

    @PrePersist
    public void prePersist() {
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.reviewLevels == null) {
            this.reviewLevels = 1;
        }
        if (this.timeoutHours == null) {
            this.timeoutHours = 24;
        }
        if (this.autoApprove == null) {
            this.autoApprove = false;
        }
    }

    public boolean matchesProduct(Product product) {
        if (categoryId != null && !categoryId.equals(product.getCategory().getId())) {
            return false;
        }
        if (brandId != null && !brandId.equals(product.getBrand().getId())) {
            return false;
        }
        if (minPrice != null && product.getPrice() < minPrice) {
            return false;
        }
        if (maxPrice != null && product.getPrice() > maxPrice) {
            return false;
        }
        return true;
    }

    public ReviewStep getStepByLevel(Integer level) {
        return steps.stream()
                .filter(step -> step.getLevel().equals(level))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid review level: " + level));
    }

    public boolean isLastStep(Integer level) {
        return steps.stream()
                .filter(step -> step.getLevel().equals(level))
                .findFirst()
                .map(ReviewStep::getIsFinal)
                .orElse(false);
    }

    public boolean canRejectAtLevel(Integer level) {
        return steps.stream()
                .filter(step -> step.getLevel().equals(level))
                .findFirst()
                .map(ReviewStep::getCanReject)
                .orElse(false);
    }

    public boolean canTransferAtLevel(Integer level) {
        return steps.stream()
                .filter(step -> step.getLevel().equals(level))
                .findFirst()
                .map(ReviewStep::getCanTransfer)
                .orElse(false);
    }

    public boolean canCommentAtLevel(Integer level) {
        return steps.stream()
                .filter(step -> step.getLevel().equals(level))
                .findFirst()
                .map(ReviewStep::getCanComment)
                .orElse(false);
    }

    public Integer getTimeoutHoursForLevel(Integer level) {
        return steps.stream()
                .filter(step -> step.getLevel().equals(level))
                .findFirst()
                .map(ReviewStep::getTimeoutHours)
                .orElse(timeoutHours);
    }
} 