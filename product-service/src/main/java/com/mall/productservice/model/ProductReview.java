

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_reviews")
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    @Column(length = 500)
    private String comment;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(name = "reviewer_name", nullable = false)
    private String reviewerName;

    @Column(name = "review_time")
    private LocalDateTime reviewTime;

    @Column(name = "next_reviewer_id")
    private Long nextReviewerId;

    @Column(name = "next_reviewer_name")
    private String nextReviewerName;

    @Column(name = "review_level", nullable = false)
    private Integer reviewLevel;

    @Column(name = "is_final_review", nullable = false)
    private Boolean isFinalReview;

    @Column(name = "reject_reason")
    private String rejectReason;

    @Column(name = "review_attachments")
    private String reviewAttachments;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public enum ReviewStatus {
        PENDING("待审核"),
        REVIEWING("审核中"),
        APPROVED("已通过"),
        REJECTED("已拒绝"),
        CANCELLED("已取消");

        private final String description;

        ReviewStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = ReviewStatus.PENDING;
        }
        if (this.reviewLevel == null) {
            this.reviewLevel = 1;
        }
        if (this.isFinalReview == null) {
            this.isFinalReview = false;
        }
    }
} 