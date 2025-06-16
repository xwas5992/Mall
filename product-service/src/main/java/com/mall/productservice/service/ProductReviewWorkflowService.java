

import com.mall.productservice.model.Product;
import com.mall.productservice.model.ProductReview;
import com.mall.productservice.model.ProductReviewWorkflow;
import com.mall.productservice.repository.ProductReviewWorkflowRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductReviewWorkflowService {

    private final ProductReviewWorkflowRepository workflowRepository;

    @Transactional
    public ProductReviewWorkflow createWorkflow(ProductReviewWorkflow workflow) {
        validateWorkflow(workflow);
        return workflowRepository.save(workflow);
    }

    @Transactional
    public ProductReviewWorkflow updateWorkflow(Long id, ProductReviewWorkflow workflow) {
        ProductReviewWorkflow existingWorkflow = workflowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("工作流不存在"));

        validateWorkflow(workflow);
        workflow.setId(id);
        workflow.setCreatedAt(existingWorkflow.getCreatedAt());
        workflow.setVersion(existingWorkflow.getVersion());

        return workflowRepository.save(workflow);
    }

    @Transactional
    public void deleteWorkflow(Long id) {
        ProductReviewWorkflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("工作流不存在"));

        workflow.setIsActive(false);
        workflowRepository.save(workflow);
    }

    @Transactional
    public void activateWorkflow(Long id) {
        ProductReviewWorkflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("工作流不存在"));

        workflow.setIsActive(true);
        workflowRepository.save(workflow);
    }

    @Transactional
    public void deactivateWorkflow(Long id) {
        ProductReviewWorkflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("工作流不存在"));

        workflow.setIsActive(false);
        workflowRepository.save(workflow);
    }

    @Cacheable(value = "workflow", key = "#id")
    public ProductReviewWorkflow getWorkflow(Long id) {
        return workflowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("工作流不存在"));
    }

    @Cacheable(value = "workflow-by-name", key = "#name")
    public ProductReviewWorkflow getWorkflowByName(String name) {
        return workflowRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("工作流不存在"));
    }

    @Cacheable(value = "active-workflows")
    public List<ProductReviewWorkflow> getActiveWorkflows() {
        return workflowRepository.findByIsActive(true);
    }

    public List<ProductReviewWorkflow> findMatchingWorkflows(Product product) {
        return workflowRepository.findMatchingWorkflows(
                product.getCategory().getId(),
                product.getBrand().getId(),
                product.getPrice());
    }

    public Optional<ProductReviewWorkflow> findBestMatchingWorkflow(Product product) {
        List<ProductReviewWorkflow> matchingWorkflows = findMatchingWorkflows(product);
        if (matchingWorkflows.isEmpty()) {
            return Optional.empty();
        }

        // 优先选择最具体的匹配（同时匹配分类和品牌）
        return matchingWorkflows.stream()
                .filter(w -> w.getCategoryId() != null && w.getBrandId() != null)
                .findFirst()
                // 其次选择只匹配分类的
                .or(() -> matchingWorkflows.stream()
                        .filter(w -> w.getCategoryId() != null && w.getBrandId() == null)
                        .findFirst())
                // 再次选择只匹配品牌的
                .or(() -> matchingWorkflows.stream()
                        .filter(w -> w.getCategoryId() == null && w.getBrandId() != null)
                        .findFirst())
                // 最后选择默认工作流
                .or(() -> matchingWorkflows.stream()
                        .filter(w -> w.getCategoryId() == null && w.getBrandId() == null)
                        .findFirst());
    }

    public ProductReviewWorkflow getDefaultWorkflow() {
        List<ProductReviewWorkflow> defaultWorkflows = workflowRepository.findDefaultWorkflows();
        if (defaultWorkflows.isEmpty()) {
            throw new IllegalStateException("未找到默认工作流配置");
        }
        return defaultWorkflows.get(0);
    }

    public List<ProductReviewWorkflow> getWorkflowsByRole(String roleName) {
        return workflowRepository.findByRoleName(roleName);
    }

    public List<ProductReviewWorkflow> getWorkflowsByLevels(Integer levels) {
        return workflowRepository.findByReviewLevels(levels);
    }

    public List<ProductReviewWorkflow> getAutoApproveWorkflows() {
        return workflowRepository.findAutoApproveWorkflows();
    }

    public List<ProductReviewWorkflow> getWorkflowsByMaxTimeoutHours(Integer hours) {
        return workflowRepository.findByMaxTimeoutHours(hours);
    }

    public List<ProductReviewWorkflow> getWorkflowsWithFinalStep() {
        return workflowRepository.findWorkflowsWithFinalStep();
    }

    public List<ProductReviewWorkflow> getWorkflowsWithRejectPermission() {
        return workflowRepository.findWorkflowsWithRejectPermission();
    }

    public List<ProductReviewWorkflow> getWorkflowsWithTransferPermission() {
        return workflowRepository.findWorkflowsWithTransferPermission();
    }

    public List<ProductReviewWorkflow> getWorkflowsWithRequiredAttachments() {
        return workflowRepository.findWorkflowsWithRequiredAttachments();
    }

    public List<ProductReviewWorkflow> getWorkflowsWithRequiredChecks() {
        return workflowRepository.findWorkflowsWithRequiredChecks();
    }

    public List<ProductReviewWorkflow> getWorkflowsWithConditionalNextLevel() {
        return workflowRepository.findWorkflowsWithConditionalNextLevel();
    }

    @CacheEvict(value = {"workflow", "workflow-by-name", "active-workflows"}, allEntries = true)
    public void clearWorkflowCache() {
        // 清除所有工作流相关缓存
    }

    private void validateWorkflow(ProductReviewWorkflow workflow) {
        if (workflow.getName() == null || workflow.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("工作流名称不能为空");
        }

        if (workflow.getSteps() == null || workflow.getSteps().isEmpty()) {
            throw new IllegalArgumentException("工作流步骤不能为空");
        }

        // 验证步骤级别是否连续
        for (int i = 0; i < workflow.getSteps().size(); i++) {
            ProductReviewWorkflow.ReviewStep step = workflow.getSteps().get(i);
            if (step.getLevel() != i + 1) {
                throw new IllegalArgumentException("工作流步骤级别必须连续，从1开始");
            }
        }

        // 验证最后一步是否标记为最终步骤
        ProductReviewWorkflow.ReviewStep lastStep = workflow.getSteps().get(workflow.getSteps().size() - 1);
        if (!lastStep.getIsFinal()) {
            throw new IllegalArgumentException("工作流的最后一步必须标记为最终步骤");
        }

        // 验证超时时间
        if (workflow.getTimeoutHours() != null && workflow.getTimeoutHours() <= 0) {
            throw new IllegalArgumentException("工作流超时时间必须大于0");
        }

        // 验证自动审核时间
        if (workflow.getAutoApprove() && (workflow.getAutoApproveHours() == null || workflow.getAutoApproveHours() <= 0)) {
            throw new IllegalArgumentException("启用自动审核时，自动审核时间必须大于0");
        }

        // 验证价格范围
        if (workflow.getMinPrice() != null && workflow.getMaxPrice() != null && 
                workflow.getMinPrice() > workflow.getMaxPrice()) {
            throw new IllegalArgumentException("最小价格不能大于最大价格");
        }
    }

    public boolean validateReviewOperation(ProductReview review, String operation, String roleName) {
        ProductReviewWorkflow workflow = getWorkflow(review.getWorkflowId());
        ProductReviewWorkflow.ReviewStep currentStep = workflow.getStepByLevel(review.getReviewLevel());

        switch (operation) {
            case "APPROVE":
                return true; // 所有步骤都可以通过
            case "REJECT":
                return currentStep.getCanReject();
            case "TRANSFER":
                return currentStep.getCanTransfer();
            case "COMMENT":
                return currentStep.getCanComment();
            default:
                return false;
        }
    }

    public boolean isLastStep(ProductReview review) {
        ProductReviewWorkflow workflow = getWorkflow(review.getWorkflowId());
        return workflow.isLastStep(review.getReviewLevel());
    }

    public Integer getNextLevel(ProductReview review) {
        ProductReviewWorkflow workflow = getWorkflow(review.getWorkflowId());
        if (isLastStep(review)) {
            return null;
        }
        return review.getReviewLevel() + 1;
    }

    public Integer getTimeoutHours(ProductReview review) {
        ProductReviewWorkflow workflow = getWorkflow(review.getWorkflowId());
        return workflow.getTimeoutHoursForLevel(review.getReviewLevel());
    }
} 