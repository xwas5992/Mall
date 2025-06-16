

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "refund_items")
public class RefundItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_order_id", nullable = false)
    private RefundOrder refundOrder;  // 关联退款订单

    @NotNull
    @Column(nullable = false)
    private Long orderItemId;  // 关联订单项ID

    @NotNull
    @Column(nullable = false)
    private Long productId;  // 商品ID

    @NotBlank
    @Column(nullable = false)
    private String productName;  // 商品名称

    @NotBlank
    @Column(nullable = false)
    private String productSku;  // 商品SKU

    @Column
    private String productImage;  // 商品图片

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer quantity;  // 退款数量

    @NotNull
    @Column(nullable = false)
    private BigDecimal unitPrice;  // 商品单价

    @NotNull
    @Column(nullable = false)
    private BigDecimal totalPrice;  // 退款总价

    @Column
    private String properties;  // 商品属性（JSON格式）

    @Column
    private String reason;  // 退款原因

    @Column
    private String description;  // 退款说明

    @Column
    private String images;  // 退款图片（JSON格式，多张图片URL）
} 