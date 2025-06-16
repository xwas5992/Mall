
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "refund_status_history")
public class RefundStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_order_id", nullable = false)
    private RefundOrder refundOrder;  // 关联退款订单

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RefundOrder.RefundStatus fromStatus;  // 原状态

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RefundOrder.RefundStatus toStatus;  // 新状态

    @NotBlank
    @Column(nullable = false)
    private String operatorId;  // 操作人ID

    @NotBlank
    @Column(nullable = false)
    private String operator;  // 操作人

    @Column
    private String remark;  // 备注

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationType operationType;  // 操作类型

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 创建时间

    // 操作类型枚举
    public enum OperationType {
        APPLY,      // 申请退款
        APPROVE,    // 审核通过
        REJECT,     // 审核拒绝
        RETURN,     // 退货
        RECEIVE,    // 确认收货
        REFUND,     // 退款
        CANCEL      // 取消退款
    }
} 