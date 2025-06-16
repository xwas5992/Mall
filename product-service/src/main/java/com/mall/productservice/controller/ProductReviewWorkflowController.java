

import com.mall.productservice.model.ProductReviewWorkflow;
import com.mall.productservice.service.ProductReviewWorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/review/workflows")
@RequiredArgsConstructor
@Tag(name = "商品审核工作流管理", description = "商品审核工作流配置的CRUD接口")
public class ProductReviewWorkflowController {

    private final ProductReviewWorkflowService workflowService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建工作流", description = "创建新的商品审核工作流配置")
    public ResponseEntity<ProductReviewWorkflow> createWorkflow(
            @Valid @RequestBody ProductReviewWorkflow workflow) {
        return ResponseEntity.ok(workflowService.createWorkflow(workflow));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新工作流", description = "更新指定ID的商品审核工作流配置")
    public ResponseEntity<ProductReviewWorkflow> updateWorkflow(
            @Parameter(description = "工作流ID") @PathVariable Long id,
            @Valid @RequestBody ProductReviewWorkflow workflow) {
        return ResponseEntity.ok(workflowService.updateWorkflow(id, workflow));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除工作流", description = "软删除指定ID的商品审核工作流配置")
    public ResponseEntity<Void> deleteWorkflow(
            @Parameter(description = "工作流ID") @PathVariable Long id) {
        workflowService.deleteWorkflow(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "激活工作流", description = "激活指定ID的商品审核工作流配置")
    public ResponseEntity<Void> activateWorkflow(
            @Parameter(description = "工作流ID") @PathVariable Long id) {
        workflowService.activateWorkflow(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "停用工作流", description = "停用指定ID的商品审核工作流配置")
    public ResponseEntity<Void> deactivateWorkflow(
            @Parameter(description = "工作流ID") @PathVariable Long id) {
        workflowService.deactivateWorkflow(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取工作流", description = "获取指定ID的商品审核工作流配置详情")
    public ResponseEntity<ProductReviewWorkflow> getWorkflow(
            @Parameter(description = "工作流ID") @PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getWorkflow(id));
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "根据名称获取工作流", description = "根据名称获取商品审核工作流配置详情")
    public ResponseEntity<ProductReviewWorkflow> getWorkflowByName(
            @Parameter(description = "工作流名称") @PathVariable String name) {
        return ResponseEntity.ok(workflowService.getWorkflowByName(name));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取活动工作流列表", description = "获取所有处于活动状态的商品审核工作流配置")
    public ResponseEntity<List<ProductReviewWorkflow>> getActiveWorkflows() {
        return ResponseEntity.ok(workflowService.getActiveWorkflows());
    }

    @GetMapping("/default")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取默认工作流", description = "获取系统默认的商品审核工作流配置")
    public ResponseEntity<ProductReviewWorkflow> getDefaultWorkflow() {
        return ResponseEntity.ok(workflowService.getDefaultWorkflow());
    }

    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "根据角色获取工作流", description = "获取指定角色可访问的商品审核工作流配置列表")
    public ResponseEntity<List<ProductReviewWorkflow>> getWorkflowsByRole(
            @Parameter(description = "角色名称") @PathVariable String roleName) {
        return ResponseEntity.ok(workflowService.getWorkflowsByRole(roleName));
    }

    @GetMapping("/levels/{levels}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "根据级别数获取工作流", description = "获取指定审核级别数的商品审核工作流配置列表")
    public ResponseEntity<List<ProductReviewWorkflow>> getWorkflowsByLevels(
            @Parameter(description = "审核级别数") @PathVariable Integer levels) {
        return ResponseEntity.ok(workflowService.getWorkflowsByLevels(levels));
    }

    @GetMapping("/auto-approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取自动审核工作流", description = "获取所有支持自动审核的商品审核工作流配置列表")
    public ResponseEntity<List<ProductReviewWorkflow>> getAutoApproveWorkflows() {
        return ResponseEntity.ok(workflowService.getAutoApproveWorkflows());
    }

    @GetMapping("/timeout/{hours}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "根据超时时间获取工作流", description = "获取指定最大超时时间的商品审核工作流配置列表")
    public ResponseEntity<List<ProductReviewWorkflow>> getWorkflowsByMaxTimeoutHours(
            @Parameter(description = "超时小时数") @PathVariable Integer hours) {
        return ResponseEntity.ok(workflowService.getWorkflowsByMaxTimeoutHours(hours));
    }

    @GetMapping("/final-step")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取有最终步骤的工作流", description = "获取所有包含最终审核步骤的商品审核工作流配置列表")
    public ResponseEntity<List<ProductReviewWorkflow>> getWorkflowsWithFinalStep() {
        return ResponseEntity.ok(workflowService.getWorkflowsWithFinalStep());
    }

    @GetMapping("/reject-permission")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取有拒绝权限的工作流", description = "获取所有包含拒绝权限的商品审核工作流配置列表")
    public ResponseEntity<List<ProductReviewWorkflow>> getWorkflowsWithRejectPermission() {
        return ResponseEntity.ok(workflowService.getWorkflowsWithRejectPermission());
    }

    @GetMapping("/transfer-permission")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取有转审权限的工作流", description = "获取所有包含转审权限的商品审核工作流配置列表")
    public ResponseEntity<List<ProductReviewWorkflow>> getWorkflowsWithTransferPermission() {
        return ResponseEntity.ok(workflowService.getWorkflowsWithTransferPermission());
    }

    @GetMapping("/required-attachments")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取需要附件的工作流", description = "获取所有要求上传附件的商品审核工作流配置列表")
    public ResponseEntity<List<ProductReviewWorkflow>> getWorkflowsWithRequiredAttachments() {
        return ResponseEntity.ok(workflowService.getWorkflowsWithRequiredAttachments());
    }

    @GetMapping("/required-checks")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取需要检查项的工作流", description = "获取所有包含必填检查项的商品审核工作流配置列表")
    public ResponseEntity<List<ProductReviewWorkflow>> getWorkflowsWithRequiredChecks() {
        return ResponseEntity.ok(workflowService.getWorkflowsWithRequiredChecks());
    }

    @GetMapping("/conditional-next")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "获取有条件流转的工作流", description = "获取所有包含条件流转的商品审核工作流配置列表")
    public ResponseEntity<List<ProductReviewWorkflow>> getWorkflowsWithConditionalNextLevel() {
        return ResponseEntity.ok(workflowService.getWorkflowsWithConditionalNextLevel());
    }

    @PostMapping("/cache/clear")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "清除工作流缓存", description = "清除所有商品审核工作流配置的缓存数据")
    public ResponseEntity<Void> clearWorkflowCache() {
        workflowService.clearWorkflowCache();
        return ResponseEntity.ok().build();
    }
} 