

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_recommendations", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_product_id", columnList = "productId"),
    @Index(name = "idx_score", columnList = "score"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class ProductRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecommendationType type;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false)
    private Boolean isShown = false;

    @Column(nullable = false)
    private Boolean isClicked = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime clickedAt;

    public enum RecommendationType {
        SEARCH_HISTORY,    // 基于搜索历史
        VIEW_HISTORY,      // 基于浏览历史
        PURCHASE_HISTORY,  // 基于购买历史
        POPULAR,          // 基于热门商品
        CATEGORY,         // 基于分类相似
        BRAND,            // 基于品牌相似
        PRICE_RANGE,      // 基于价格区间
        COLLABORATIVE     // 基于协同过滤
    }
} 