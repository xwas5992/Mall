
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "refund_orders")
public class RefundOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String refundNo;  // 退款单号

    @NotNull
    @Column(nullable = false)
    private Long orderId;  // 关联订单ID

    @NotBlank
    @Column(nullable = false)
    private String orderNo;  // 关联订单编号

    @NotNull
    @Column(nullable = false)
    private Long userId;  // 用户ID

    @NotBlank
    @Column(nullable = false)
    private String userName;  // 用户名

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RefundType refundType;  // 退款类型

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RefundStatus status;  // 退款状态

    @NotNull
    @Column(nullable = false)
    private BigDecimal refundAmount;  // 退款金额

    @Column
    private String reason;  // 退款原因

    @Column
    private String description;  // 退款说明

    @Column
    private String rejectReason;  // 拒绝原因

    @Column
    private String logisticsCompany;  // 退货物流公司

    @Column
    private String logisticsNo;  // 退货物流单号

    @Column
    private LocalDateTime applyTime;  // 申请时间

    @Column
    private LocalDateTime approveTime;  // 审核时间

    @Column
    private LocalDateTime rejectTime;  // 拒绝时间

    @Column
    private LocalDateTime refundTime;  // 退款时间

    @Column
    private LocalDateTime returnTime;  // 退货时间

    @Column
    private LocalDateTime receiveTime;  // 收货时间

    @Column
    private String operatorId;  // 操作人ID

    @Column
    private String operatorName;  // 操作人姓名

    @OneToMany(mappedBy = "refundOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefundItem> refundItems = new ArrayList<>();  // 退款商品列表

    @OneToMany(mappedBy = "refundOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefundStatusHistory> statusHistory = new ArrayList<>();  // 状态历史

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 创建时间

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 更新时间

    @Version
    private Long version;  // 乐观锁版本号

    // 退款类型枚举
    public enum RefundType {
        REFUND_ONLY,     // 仅退款
        REFUND_RETURN    // 退款退货
    }

    // 退款状态枚举
    public enum RefundStatus {
        PENDING_APPROVE,     // 待审核
        APPROVED,            // 已审核
        REJECTED,            // 已拒绝
        PENDING_RETURN,      // 待退货
        RETURNED,            // 已退货
        PENDING_RECEIVE,     // 待收货
        RECEIVED,            // 已收货
        REFUNDING,           // 退款中
        REFUNDED,            // 已退款
        CANCELLED            // 已取消
    }
} 