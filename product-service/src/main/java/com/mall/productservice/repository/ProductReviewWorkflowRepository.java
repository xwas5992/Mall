

import com.mall.productservice.model.ProductReviewWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductReviewWorkflowRepository extends JpaRepository<ProductReviewWorkflow, Long> {

    Optional<ProductReviewWorkflow> findByName(String name);

    List<ProductReviewWorkflow> findByIsActive(Boolean isActive);

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "(w.categoryId = :categoryId OR w.categoryId IS NULL) AND " +
            "(w.brandId = :brandId OR w.brandId IS NULL) AND " +
            "(w.minPrice IS NULL OR w.minPrice <= :price) AND " +
            "(w.maxPrice IS NULL OR w.maxPrice >= :price)")
    List<ProductReviewWorkflow> findMatchingWorkflows(
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            @Param("price") Double price);

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "w.categoryId = :categoryId AND w.brandId IS NULL")
    List<ProductReviewWorkflow> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "w.brandId = :brandId AND w.categoryId IS NULL")
    List<ProductReviewWorkflow> findByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "w.categoryId IS NULL AND w.brandId IS NULL AND " +
            "w.minPrice IS NULL AND w.maxPrice IS NULL")
    List<ProductReviewWorkflow> findDefaultWorkflows();

    @Query("SELECT COUNT(w) FROM ProductReviewWorkflow w WHERE w.isActive = true")
    Long countActiveWorkflows();

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "EXISTS (SELECT 1 FROM w.steps s WHERE s.roleName = :roleName)")
    List<ProductReviewWorkflow> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "w.reviewLevels = :levels")
    List<ProductReviewWorkflow> findByReviewLevels(@Param("levels") Integer levels);

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "w.autoApprove = true")
    List<ProductReviewWorkflow> findAutoApproveWorkflows();

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "w.timeoutHours <= :hours")
    List<ProductReviewWorkflow> findByMaxTimeoutHours(@Param("hours") Integer hours);

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "EXISTS (SELECT 1 FROM w.steps s WHERE s.isFinal = true)")
    List<ProductReviewWorkflow> findWorkflowsWithFinalStep();

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "EXISTS (SELECT 1 FROM w.steps s WHERE s.canReject = true)")
    List<ProductReviewWorkflow> findWorkflowsWithRejectPermission();

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "EXISTS (SELECT 1 FROM w.steps s WHERE s.canTransfer = true)")
    List<ProductReviewWorkflow> findWorkflowsWithTransferPermission();

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "w.requiredAttachments IS NOT NULL")
    List<ProductReviewWorkflow> findWorkflowsWithRequiredAttachments();

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "EXISTS (SELECT 1 FROM w.steps s WHERE s.requiredChecks IS NOT NULL)")
    List<ProductReviewWorkflow> findWorkflowsWithRequiredChecks();

    @Query("SELECT w FROM ProductReviewWorkflow w WHERE w.isActive = true AND " +
            "EXISTS (SELECT 1 FROM w.steps s WHERE s.nextLevelConditions IS NOT NULL)")
    List<ProductReviewWorkflow> findWorkflowsWithConditionalNextLevel();
} 