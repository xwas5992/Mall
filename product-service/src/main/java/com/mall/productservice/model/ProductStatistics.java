

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_statistics")
@Data
@NoArgsConstructor
public class ProductStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false)
    private Long favoriteCount = 0L;

    @Column(nullable = false)
    private Long cartCount = 0L;

    @Column(nullable = false)
    private Long purchaseCount = 0L;

    @Column(nullable = false)
    private Long reviewCount = 0L;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(nullable = false)
    private Long searchCount = 0L;

    @Column(nullable = false)
    private Long shareCount = 0L;

    @Column(nullable = false)
    private BigDecimal totalSales = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime lastViewTime;

    @Column(nullable = false)
    private LocalDateTime lastPurchaseTime;

    @Column(nullable = false)
    private LocalDateTime lastReviewTime;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    // 统计周期类型
    public enum PeriodType {
        DAILY,    // 每日统计
        WEEKLY,   // 每周统计
        MONTHLY,  // 每月统计
        YEARLY    // 每年统计
    }

    // 统计指标类型
    public enum MetricType {
        VIEWS,        // 浏览量
        FAVORITES,    // 收藏量
        CART_ADDS,    // 加购量
        PURCHASES,    // 购买量
        REVIEWS,      // 评价量
        RATINGS,      // 评分
        SEARCHES,     // 搜索量
        SHARES,       // 分享量
        SALES         // 销售额
    }
} 