

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "search_history", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_keyword", columnList = "keyword"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String keyword;

    @Column(length = 50)
    private String category;

    @Column(length = 50)
    private String brand;

    private Double minPrice;

    private Double maxPrice;

    @Column(nullable = false)
    private Integer resultCount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(nullable = false)
    private Boolean isSuccessful = true;

    @Column(length = 500)
    private String errorMessage;
} 