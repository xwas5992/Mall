package com.mall.orderservice.model;

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
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String orderNo;  // 订单编号

    @NotNull
    @Column(nullable = false)
    private Long userId;  // 用户ID

    @NotBlank
    @Column(nullable = false)
    private String userName;  // 用户名

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;  // 订单状态

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;  // 支付方式

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;  // 支付状态

    @NotNull
    @Column(nullable = false)
    private BigDecimal totalAmount;  // 订单总金额

    @NotNull
    @Column(nullable = false)
    private BigDecimal payAmount;  // 实付金额

    @Column
    private BigDecimal freightAmount;  // 运费金额

    @Column
    private BigDecimal discountAmount;  // 优惠金额

    @Column
    private BigDecimal couponAmount;  // 优惠券金额

    @Column
    private BigDecimal pointsAmount;  // 积分抵扣金额

    @NotBlank
    @Column(nullable = false)
    private String receiverName;  // 收货人姓名

    @NotBlank
    @Column(nullable = false)
    private String receiverPhone;  // 收货人电话

    @NotBlank
    @Column(nullable = false)
    private String receiverAddress;  // 收货地址

    @Column
    private String receiverProvince;  // 省份

    @Column
    private String receiverCity;  // 城市

    @Column
    private String receiverDistrict;  // 区县

    @Column
    private String receiverZip;  // 邮编

    @Column
    private String note;  // 订单备注

    @Column
    private LocalDateTime payTime;  // 支付时间

    @Column
    private LocalDateTime deliveryTime;  // 发货时间

    @Column
    private LocalDateTime receiveTime;  // 收货时间

    @Column
    private LocalDateTime commentTime;  // 评价时间

    @Column
    private LocalDateTime cancelTime;  // 取消时间

    @Column
    private String cancelReason;  // 取消原因

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();  // 订单项列表

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();  // 状态历史

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 创建时间

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 更新时间

    @Version
    private Long version;  // 乐观锁版本号

    // 订单状态枚举
    public enum OrderStatus {
        PENDING_PAYMENT,    // 待付款
        PENDING_DELIVERY,   // 待发货
        PENDING_RECEIVE,    // 待收货
        COMPLETED,          // 已完成
        CANCELLED,          // 已取消
        REFUNDING,          // 退款中
        REFUNDED,           // 已退款
        CLOSED              // 已关闭
    }

    // 支付方式枚举
    public enum PaymentType {
        ONLINE_PAY,     // 在线支付
        COD,            // 货到付款
        POINTS_PAY,     // 积分支付
        BALANCE_PAY     // 余额支付
    }

    // 支付状态枚举
    public enum PaymentStatus {
        UNPAID,         // 未支付
        PAID,           // 已支付
        PARTIALLY_PAID, // 部分支付
        REFUNDED,       // 已退款
        PARTIALLY_REFUNDED // 部分退款
    }
} 