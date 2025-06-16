

import com.mall.productservice.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsByName(String name);

    Page<Tag> findByType(Tag.TagType type, Pageable pageable);

    Page<Tag> findByStatus(Boolean status, Pageable pageable);

    @Query("SELECT t FROM Tag t WHERE t.type = :type AND t.status = true ORDER BY t.sortOrder ASC")
    List<Tag> findActiveByType(@Param("type") Tag.TagType type);

    @Query("SELECT t FROM Tag t WHERE t.name LIKE %:keyword% OR t.description LIKE %:keyword%")
    Page<Tag> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT t FROM Tag t JOIN t.products p WHERE p.id = :productId")
    Set<Tag> findByProductId(@Param("productId") Long productId);

    @Query("SELECT t FROM Tag t WHERE t.id IN :tagIds")
    Set<Tag> findByIds(@Param("tagIds") Set<Long> tagIds);

    @Query("SELECT t FROM Tag t WHERE t.type = :type AND t.status = true AND t.id IN " +
           "(SELECT t2.id FROM Tag t2 JOIN t2.products p WHERE p.id = :productId)")
    Set<Tag> findActiveByTypeAndProductId(
            @Param("type") Tag.TagType type,
            @Param("productId") Long productId);

    @Query("SELECT t, COUNT(p) as productCount FROM Tag t LEFT JOIN t.products p " +
           "WHERE t.status = true GROUP BY t ORDER BY productCount DESC")
    Page<Object[]> findPopularTags(Pageable pageable);

    @Query("SELECT t.type, COUNT(t) as count FROM Tag t WHERE t.status = true GROUP BY t.type")
    List<Object[]> getTagTypeStats();

    @Query("SELECT t FROM Tag t WHERE t.status = true AND t.type = :type " +
           "AND t.id IN (SELECT t2.id FROM Tag t2 JOIN t2.products p " +
           "WHERE p.id IN (SELECT p2.id FROM Product p2 JOIN p2.tags t3 " +
           "WHERE t3.id = :tagId))")
    List<Tag> findRelatedTags(
            @Param("tagId") Long tagId,
            @Param("type") Tag.TagType type,
            Pageable pageable);
} 