

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;  // 所属订单

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Order.OrderStatus fromStatus;  // 原状态

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Order.OrderStatus toStatus;  // 新状态

    @NotBlank
    @Column(nullable = false)
    private String operator;  // 操作人

    @Column
    private String operatorId;  // 操作人ID

    @Column
    private String remark;  // 备注

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 创建时间

    // 操作类型枚举
    public enum OperationType {
        CREATE,         // 创建订单
        PAY,           // 支付
        DELIVER,       // 发货
        RECEIVE,       // 收货
        COMPLETE,      // 完成
        CANCEL,        // 取消
        REFUND,        // 退款
        CLOSE,         // 关闭
        SYSTEM         // 系统操作
    }

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationType operationType;  // 操作类型
} 