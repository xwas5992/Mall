
import com.mall.productservice.model.SearchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    Page<SearchHistory> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT sh FROM SearchHistory sh WHERE sh.userId = :userId AND sh.createdAt >= :startTime ORDER BY sh.createdAt DESC")
    List<SearchHistory> findRecentByUserId(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT sh.keyword, COUNT(sh) as count FROM SearchHistory sh " +
           "WHERE sh.userId = :userId AND sh.createdAt >= :startTime " +
           "GROUP BY sh.keyword ORDER BY count DESC")
    List<Object[]> findPopularKeywordsByUserId(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT sh.category, COUNT(sh) as count FROM SearchHistory sh " +
           "WHERE sh.userId = :userId AND sh.category IS NOT NULL AND sh.createdAt >= :startTime " +
           "GROUP BY sh.category ORDER BY count DESC")
    List<Object[]> findPopularCategoriesByUserId(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT sh.brand, COUNT(sh) as count FROM SearchHistory sh " +
           "WHERE sh.userId = :userId AND sh.brand IS NOT NULL AND sh.createdAt >= :startTime " +
           "GROUP BY sh.brand ORDER BY count DESC")
    List<Object[]> findPopularBrandsByUserId(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);

    void deleteByUserIdAndCreatedAtBefore(Long userId, LocalDateTime time);

    @Query("SELECT COUNT(sh) FROM SearchHistory sh WHERE sh.userId = :userId AND sh.createdAt >= :startTime")
    long countByUserIdAndTimeRange(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);
} 