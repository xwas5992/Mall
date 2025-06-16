

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;  // 所属订单

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
    private Integer quantity;  // 购买数量

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;  // 商品单价

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;  // 商品总价

    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;  // 优惠金额

    @Column(precision = 10, scale = 2)
    private BigDecimal payAmount;  // 实付金额

    @Column
    private String properties;  // 商品属性（JSON格式）

    @Column
    private Boolean isCommented;  // 是否已评价

    @Column
    private LocalDateTime commentTime;  // 评价时间

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 创建时间

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 更新时间

    @Version
    private Long version;  // 乐观锁版本号
} 