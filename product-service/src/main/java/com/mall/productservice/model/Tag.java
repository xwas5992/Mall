

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "tags", indexes = {
    @Index(name = "idx_tag_name", columnList = "name", unique = true),
    @Index(name = "idx_tag_type", columnList = "type"),
    @Index(name = "idx_tag_status", columnList = "status")
})
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TagType type;

    @Column(nullable = false)
    private Boolean status = true;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(length = 200)
    private String iconUrl;

    @Column(length = 50)
    private String color;

    @ManyToMany(mappedBy = "tags")
    private Set<Product> products = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private Long updatedBy;

    public enum TagType {
        CATEGORY,    // 分类标签
        BRAND,       // 品牌标签
        FEATURE,     // 特性标签
        PROMOTION,   // 促销标签
        SEASONAL,    // 季节标签
        CUSTOM       // 自定义标签
    }
} 