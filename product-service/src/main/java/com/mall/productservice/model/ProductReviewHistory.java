

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_review_histories")
public class ProductReviewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ProductReview review;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductReview.ReviewStatus fromStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductReview.ReviewStatus toStatus;

    @Column(name = "operator_id", nullable = false)
    private Long operatorId;

    @Column(name = "operator_name", nullable = false)
    private String operatorName;

    @Column(length = 500)
    private String comment;

    @Column(name = "operation_time", nullable = false)
    private LocalDateTime operationTime;

    @Column(name = "operation_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    @Column(name = "review_level", nullable = false)
    private Integer reviewLevel;

    @Column(name = "is_final_review", nullable = false)
    private Boolean isFinalReview;

    @Column(name = "next_reviewer_id")
    private Long nextReviewerId;

    @Column(name = "next_reviewer_name")
    private String nextReviewerName;

    @Column(name = "reject_reason")
    private String rejectReason;

    @Column(name = "review_attachments")
    private String reviewAttachments;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum OperationType {
        SUBMIT("提交审核"),
        APPROVE("通过审核"),
        REJECT("拒绝审核"),
        CANCEL("取消审核"),
        ASSIGN("分配审核"),
        TRANSFER("转交审核"),
        COMMENT("添加评论");

        private final String description;

        OperationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.operationTime == null) {
            this.operationTime = LocalDateTime.now();
        }
    }
} 