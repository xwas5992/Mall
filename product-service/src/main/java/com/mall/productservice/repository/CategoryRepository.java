

import com.mall.productservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentIdOrderBySortOrderAsc(Long parentId);

    List<Category> findByParentIdAndIsVisibleTrueOrderBySortOrderAsc(Long parentId);

    @Query("SELECT c FROM Category c WHERE c.level = 1 AND c.isVisible = true ORDER BY c.sortOrder ASC")
    List<Category> findRootCategories();

    @Query("SELECT c FROM Category c WHERE c.parentId = :parentId AND c.isVisible = true ORDER BY c.sortOrder ASC")
    List<Category> findVisibleChildren(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Category c WHERE c.id IN :ids AND c.isVisible = true ORDER BY c.sortOrder ASC")
    List<Category> findByIdsAndVisible(@Param("ids") List<Long> ids);

    boolean existsByNameAndParentId(String name, Long parentId);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.parentId = :parentId")
    long countChildren(@Param("parentId") Long parentId);

    @Query("SELECT MAX(c.level) FROM Category c")
    Integer findMaxLevel();
} 