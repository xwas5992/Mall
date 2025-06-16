

import com.mall.productservice.model.ProductReviewWorkflowLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductReviewWorkflowLogRepository extends JpaRepository<ProductReviewWorkflowLog, Long> {

    Page<ProductReviewWorkflowLog> findByProductId(Long productId, Pageable pageable);

    Page<ProductReviewWorkflowLog> findByWorkflowId(Long workflowId, Pageable pageable);

    Page<ProductReviewWorkflowLog> findByOperatorId(String operatorId, Pageable pageable);

    Page<ProductReviewWorkflowLog> findByOperatorRole(String operatorRole, Pageable pageable);

    Page<ProductReviewWorkflowLog> findByStatus(String status, Pageable pageable);

    Page<ProductReviewWorkflowLog> findByResult(String result, Pageable pageable);

    Page<ProductReviewWorkflowLog> findByOperation(String operation, Pageable pageable);

    @Query("SELECT l FROM ProductReviewWorkflowLog l WHERE l.productId = :productId AND l.workflowId = :workflowId ORDER BY l.createdAt DESC")
    List<ProductReviewWorkflowLog> findLatestLogsByProductAndWorkflow(
            @Param("productId") Long productId,
            @Param("workflowId") Long workflowId);

    @Query("SELECT l FROM ProductReviewWorkflowLog l WHERE l.productId = :productId AND l.workflowId = :workflowId AND l.reviewLevel = :level ORDER BY l.createdAt DESC")
    List<ProductReviewWorkflowLog> findLatestLogsByProductAndWorkflowAndLevel(
            @Param("productId") Long productId,
            @Param("workflowId") Long workflowId,
            @Param("level") Integer level);

    @Query("SELECT l FROM ProductReviewWorkflowLog l WHERE l.operatorId = :operatorId AND l.status = 'PENDING' ORDER BY l.createdAt ASC")
    List<ProductReviewWorkflowLog> findPendingLogsByOperator(
            @Param("operatorId") String operatorId);

    @Query("SELECT l FROM ProductReviewWorkflowLog l WHERE l.operatorRole = :roleName AND l.status = 'PENDING' ORDER BY l.createdAt ASC")
    List<ProductReviewWorkflowLog> findPendingLogsByRole(
            @Param("roleName") String roleName);

    @Query("SELECT l FROM ProductReviewWorkflowLog l WHERE l.status = 'PENDING' AND l.timeoutAt <= :now")
    List<ProductReviewWorkflowLog> findTimeoutLogs(
            @Param("now") LocalDateTime now);

    @Query("SELECT l FROM ProductReviewWorkflowLog l WHERE l.productId = :productId AND l.workflowId = :workflowId AND l.isAutoApproved = true ORDER BY l.createdAt DESC")
    List<ProductReviewWorkflowLog> findAutoApprovedLogs(
            @Param("productId") Long productId,
            @Param("workflowId") Long workflowId);

    @Query("SELECT COUNT(l) FROM ProductReviewWorkflowLog l WHERE l.workflowId = :workflowId AND l.status = 'COMPLETED' AND l.result = 'APPROVED'")
    Long countApprovedByWorkflow(
            @Param("workflowId") Long workflowId);

    @Query("SELECT COUNT(l) FROM ProductReviewWorkflowLog l WHERE l.workflowId = :workflowId AND l.status = 'COMPLETED' AND l.result = 'REJECTED'")
    Long countRejectedByWorkflow(
            @Param("workflowId") Long workflowId);

    @Query("SELECT COUNT(l) FROM ProductReviewWorkflowLog l WHERE l.workflowId = :workflowId AND l.isTimeout = true")
    Long countTimeoutByWorkflow(
            @Param("workflowId") Long workflowId);

    @Query("SELECT AVG(TIMESTAMPDIFF(SECOND, l.createdAt, l.updatedAt)) FROM ProductReviewWorkflowLog l WHERE l.workflowId = :workflowId AND l.status = 'COMPLETED'")
    Double calculateAverageProcessingTimeByWorkflow(
            @Param("workflowId") Long workflowId);

    @Query("SELECT l.operatorRole, COUNT(l) FROM ProductReviewWorkflowLog l WHERE l.workflowId = :workflowId GROUP BY l.operatorRole")
    List<Object[]> countByRoleAndWorkflow(
            @Param("workflowId") Long workflowId);

    @Query("SELECT l.operation, COUNT(l) FROM ProductReviewWorkflowLog l WHERE l.workflowId = :workflowId GROUP BY l.operation")
    List<Object[]> countByOperationAndWorkflow(
            @Param("workflowId") Long workflowId);

    @Query("SELECT l.result, COUNT(l) FROM ProductReviewWorkflowLog l WHERE l.workflowId = :workflowId GROUP BY l.result")
    List<Object[]> countByResultAndWorkflow(
            @Param("workflowId") Long workflowId);

    @Query("SELECT l FROM ProductReviewWorkflowLog l WHERE l.productId = :productId AND l.workflowId = :workflowId AND l.reviewLevel = :level AND l.status = 'PENDING'")
    ProductReviewWorkflowLog findPendingLogByProductAndWorkflowAndLevel(
            @Param("productId") Long productId,
            @Param("workflowId") Long workflowId,
            @Param("level") Integer level);

    @Query("SELECT l FROM ProductReviewWorkflowLog l WHERE l.productId = :productId AND l.workflowId = :workflowId AND l.status = 'COMPLETED' ORDER BY l.createdAt DESC")
    ProductReviewWorkflowLog findLatestCompletedLog(
            @Param("productId") Long productId,
            @Param("workflowId") Long workflowId);

    @Query("SELECT l FROM ProductReviewWorkflowLog l WHERE l.productId = :productId AND l.workflowId = :workflowId AND l.status = 'COMPLETED' AND l.result = 'APPROVED' ORDER BY l.createdAt DESC")
    ProductReviewWorkflowLog findLatestApprovedLog(
            @Param("productId") Long productId,
            @Param("workflowId") Long workflowId);

    @Query("SELECT l FROM ProductReviewWorkflowLog l WHERE l.productId = :productId AND l.workflowId = :workflowId AND l.status = 'COMPLETED' AND l.result = 'REJECTED' ORDER BY l.createdAt DESC")
    ProductReviewWorkflowLog findLatestRejectedLog(
            @Param("productId") Long productId,
            @Param("workflowId") Long workflowId);

    @Query("SELECT l FROM ProductReviewWorkflowLog l WHERE l.productId = :productId AND l.workflowId = :workflowId AND l.status = 'COMPLETED' AND l.result = 'TRANSFERRED' ORDER BY l.createdAt DESC")
    ProductReviewWorkflowLog findLatestTransferredLog(
            @Param("productId") Long productId,
            @Param("workflowId") Long workflowId);
} 