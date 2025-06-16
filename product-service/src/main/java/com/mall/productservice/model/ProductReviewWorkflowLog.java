

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_review_workflow_logs")
@EqualsAndHashCode(callSuper = false)
public class ProductReviewWorkflowLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long workflowId;

    @Column(nullable = false)
    private String workflowName;

    @Column(nullable = false)
    private Integer reviewLevel;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private String operatorId;

    @Column(nullable = false)
    private String operatorName;

    @Column(nullable = false)
    private String operatorRole;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String result;

    @Column
    private String nextOperatorId;

    @Column
    private String nextOperatorName;

    @Column
    private String nextOperatorRole;

    @Column
    private Integer nextLevel;

    @Column
    private LocalDateTime timeoutAt;

    @Column
    private Boolean isTimeout;

    @Column
    private Boolean isAutoApproved;

    @Column
    private String attachments;

    @Column
    private String checkItems;

    @Column
    private String metadata;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public enum Operation {
        SUBMIT,     // 提交审核
        APPROVE,    // 通过
        REJECT,     // 拒绝
        TRANSFER,   // 转审
        COMMENT,    // 评论
        TIMEOUT,    // 超时
        AUTO_APPROVE, // 自动通过
        CANCEL      // 取消审核
    }

    public enum Status {
        PENDING,    // 待处理
        PROCESSING, // 处理中
        COMPLETED,  // 已完成
        CANCELLED   // 已取消
    }

    public enum Result {
        APPROVED,   // 已通过
        REJECTED,   // 已拒绝
        TRANSFERRED,// 已转审
        TIMEOUT,    // 已超时
        CANCELLED   // 已取消
    }
} 