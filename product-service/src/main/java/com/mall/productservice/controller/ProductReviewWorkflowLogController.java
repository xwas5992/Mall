

import com.mall.productservice.model.ProductReviewWorkflowLog;
import com.mall.productservice.service.ProductReviewWorkflowLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/review/workflow-logs")
@RequiredArgsConstructor
@Tag(name = "商品审核工作流记录", description = "商品审核工作流审批记录的管理接口")
public class ProductReviewWorkflowLogController {

    private final ProductReviewWorkflowLogService logService;

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取商品审核记录", description = "分页获取指定商品的审核记录")
    public ResponseEntity<Page<ProductReviewWorkflowLog>> getLogsByProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            Pageable pageable) {
        return ResponseEntity.ok(logService.getLogsByProduct(productId, pageable));
    }

    @GetMapping("/workflow/{workflowId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取工作流审核记录", description = "分页获取指定工作流的审核记录")
    public ResponseEntity<Page<ProductReviewWorkflowLog>> getLogsByWorkflow(
            @Parameter(description = "工作流ID") @PathVariable Long workflowId,
            Pageable pageable) {
        return ResponseEntity.ok(logService.getLogsByWorkflow(workflowId, pageable));
    }

    @GetMapping("/operator/{operatorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取操作员审核记录", description = "分页获取指定操作员的审核记录")
    public ResponseEntity<Page<ProductReviewWorkflowLog>> getLogsByOperator(
            @Parameter(description = "操作员ID") @PathVariable String operatorId,
            Pageable pageable) {
        return ResponseEntity.ok(logService.getLogsByOperator(operatorId, pageable));
    }

    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取角色审核记录", description = "分页获取指定角色的审核记录")
    public ResponseEntity<Page<ProductReviewWorkflowLog>> getLogsByRole(
            @Parameter(description = "角色名称") @PathVariable String roleName,
            Pageable pageable) {
        return ResponseEntity.ok(logService.getLogsByRole(roleName, pageable));
    }

    @GetMapping("/product/{productId}/workflow/{workflowId}/latest")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取最新审核记录", description = "获取指定商品和工作流的最新审核记录")
    public ResponseEntity<List<ProductReviewWorkflowLog>> getLatestLogsByProductAndWorkflow(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "工作流ID") @PathVariable Long workflowId) {
        return ResponseEntity.ok(logService.getLatestLogsByProductAndWorkflow(productId, workflowId));
    }

    @GetMapping("/product/{productId}/workflow/{workflowId}/level/{level}/latest")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取指定级别最新审核记录", description = "获取指定商品、工作流和级别的最新审核记录")
    public ResponseEntity<List<ProductReviewWorkflowLog>> getLatestLogsByProductAndWorkflowAndLevel(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "工作流ID") @PathVariable Long workflowId,
            @Parameter(description = "审核级别") @PathVariable Integer level) {
        return ResponseEntity.ok(logService.getLatestLogsByProductAndWorkflowAndLevel(productId, workflowId, level));
    }

    @GetMapping("/operator/{operatorId}/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取待处理审核记录", description = "获取指定操作员的待处理审核记录")
    public ResponseEntity<List<ProductReviewWorkflowLog>> getPendingLogsByOperator(
            @Parameter(description = "操作员ID") @PathVariable String operatorId) {
        return ResponseEntity.ok(logService.getPendingLogsByOperator(operatorId));
    }

    @GetMapping("/role/{roleName}/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取角色待处理审核记录", description = "获取指定角色的待处理审核记录")
    public ResponseEntity<List<ProductReviewWorkflowLog>> getPendingLogsByRole(
            @Parameter(description = "角色名称") @PathVariable String roleName) {
        return ResponseEntity.ok(logService.getPendingLogsByRole(roleName));
    }

    @GetMapping("/product/{productId}/workflow/{workflowId}/level/{level}/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取指定级别待处理审核记录", description = "获取指定商品、工作流和级别的待处理审核记录")
    public ResponseEntity<ProductReviewWorkflowLog> getPendingLogByProductAndWorkflowAndLevel(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "工作流ID") @PathVariable Long workflowId,
            @Parameter(description = "审核级别") @PathVariable Integer level) {
        return ResponseEntity.ok(logService.getPendingLogByProductAndWorkflowAndLevel(productId, workflowId, level));
    }

    @GetMapping("/product/{productId}/workflow/{workflowId}/completed")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取最新完成审核记录", description = "获取指定商品和工作流的最新完成审核记录")
    public ResponseEntity<ProductReviewWorkflowLog> getLatestCompletedLog(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "工作流ID") @PathVariable Long workflowId) {
        return ResponseEntity.ok(logService.getLatestCompletedLog(productId, workflowId));
    }

    @GetMapping("/product/{productId}/workflow/{workflowId}/approved")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取最新通过审核记录", description = "获取指定商品和工作流的最新通过审核记录")
    public ResponseEntity<ProductReviewWorkflowLog> getLatestApprovedLog(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "工作流ID") @PathVariable Long workflowId) {
        return ResponseEntity.ok(logService.getLatestApprovedLog(productId, workflowId));
    }

    @GetMapping("/product/{productId}/workflow/{workflowId}/rejected")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取最新拒绝审核记录", description = "获取指定商品和工作流的最新拒绝审核记录")
    public ResponseEntity<ProductReviewWorkflowLog> getLatestRejectedLog(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "工作流ID") @PathVariable Long workflowId) {
        return ResponseEntity.ok(logService.getLatestRejectedLog(productId, workflowId));
    }

    @GetMapping("/product/{productId}/workflow/{workflowId}/transferred")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取最新转审审核记录", description = "获取指定商品和工作流的最新转审审核记录")
    public ResponseEntity<ProductReviewWorkflowLog> getLatestTransferredLog(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "工作流ID") @PathVariable Long workflowId) {
        return ResponseEntity.ok(logService.getLatestTransferredLog(productId, workflowId));
    }

    @GetMapping("/workflow/{workflowId}/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取工作流统计信息", description = "获取指定工作流的审核统计信息")
    public ResponseEntity<Map<String, Object>> getWorkflowStatistics(
            @Parameter(description = "工作流ID") @PathVariable Long workflowId) {
        return ResponseEntity.ok(logService.getWorkflowStatistics(workflowId));
    }

    @PostMapping("/{logId}/timeout")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "标记审核记录超时", description = "将指定审核记录标记为超时状态")
    public ResponseEntity<Void> markLogAsTimeout(
            @Parameter(description = "审核记录ID") @PathVariable Long logId) {
        logService.markLogAsTimeout(logId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{logId}/auto-approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "标记审核记录自动通过", description = "将指定审核记录标记为自动通过状态")
    public ResponseEntity<Void> markLogAsAutoApproved(
            @Parameter(description = "审核记录ID") @PathVariable Long logId) {
        logService.markLogAsAutoApproved(logId);
        return ResponseEntity.ok().build();
    }
} 