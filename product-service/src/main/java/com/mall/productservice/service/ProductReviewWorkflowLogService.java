

import com.mall.productservice.model.ProductReview;
import com.mall.productservice.model.ProductReviewWorkflow;
import com.mall.productservice.model.ProductReviewWorkflowLog;
import com.mall.productservice.repository.ProductReviewWorkflowLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewWorkflowLogService {

    private final ProductReviewWorkflowLogRepository logRepository;
    private final NotificationService notificationService;

    @Transactional
    public ProductReviewWorkflowLog createLog(ProductReview review, ProductReviewWorkflow workflow,
                                            String operation, String operatorId, String operatorName,
                                            String operatorRole, String comment, String nextOperatorId,
                                            String nextOperatorName, String nextOperatorRole,
                                            Integer nextLevel, LocalDateTime timeoutAt) {
        ProductReviewWorkflowLog log = new ProductReviewWorkflowLog();
        log.setProductId(review.getProductId());
        log.setWorkflowId(workflow.getId());
        log.setWorkflowName(workflow.getName());
        log.setReviewLevel(review.getReviewLevel());
        log.setOperation(operation);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperatorRole(operatorRole);
        log.setComment(comment);
        log.setStatus(ProductReviewWorkflowLog.Status.PROCESSING.name());
        log.setResult(determineResult(operation));
        log.setNextOperatorId(nextOperatorId);
        log.setNextOperatorName(nextOperatorName);
        log.setNextOperatorRole(nextOperatorRole);
        log.setNextLevel(nextLevel);
        log.setTimeoutAt(timeoutAt);
        log.setIsTimeout(false);
        log.setIsAutoApproved(false);
        log.setAttachments(review.getAttachments());
        log.setCheckItems(review.getCheckItems());
        log.setMetadata(review.getMetadata());

        log = logRepository.save(log);
        notifyLogCreated(log);
        return log;
    }

    @Transactional
    public void updateLogStatus(Long logId, String status, String result) {
        ProductReviewWorkflowLog log = logRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("审批记录不存在"));

        log.setStatus(status);
        log.setResult(result);
        logRepository.save(log);
        notifyLogStatusUpdated(log);
    }

    @Transactional
    public void markLogAsTimeout(Long logId) {
        ProductReviewWorkflowLog log = logRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("审批记录不存在"));

        log.setIsTimeout(true);
        log.setStatus(ProductReviewWorkflowLog.Status.COMPLETED.name());
        log.setResult(ProductReviewWorkflowLog.Result.TIMEOUT.name());
        logRepository.save(log);
        notifyLogTimeout(log);
    }

    @Transactional
    public void markLogAsAutoApproved(Long logId) {
        ProductReviewWorkflowLog log = logRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("审批记录不存在"));

        log.setIsAutoApproved(true);
        log.setStatus(ProductReviewWorkflowLog.Status.COMPLETED.name());
        log.setResult(ProductReviewWorkflowLog.Result.APPROVED.name());
        logRepository.save(log);
        notifyLogAutoApproved(log);
    }

    public Page<ProductReviewWorkflowLog> getLogsByProduct(Long productId, Pageable pageable) {
        return logRepository.findByProductId(productId, pageable);
    }

    public Page<ProductReviewWorkflowLog> getLogsByWorkflow(Long workflowId, Pageable pageable) {
        return logRepository.findByWorkflowId(workflowId, pageable);
    }

    public Page<ProductReviewWorkflowLog> getLogsByOperator(String operatorId, Pageable pageable) {
        return logRepository.findByOperatorId(operatorId, pageable);
    }

    public Page<ProductReviewWorkflowLog> getLogsByRole(String roleName, Pageable pageable) {
        return logRepository.findByOperatorRole(roleName, pageable);
    }

    public List<ProductReviewWorkflowLog> getLatestLogsByProductAndWorkflow(Long productId, Long workflowId) {
        return logRepository.findLatestLogsByProductAndWorkflow(productId, workflowId);
    }

    public List<ProductReviewWorkflowLog> getLatestLogsByProductAndWorkflowAndLevel(
            Long productId, Long workflowId, Integer level) {
        return logRepository.findLatestLogsByProductAndWorkflowAndLevel(productId, workflowId, level);
    }

    public List<ProductReviewWorkflowLog> getPendingLogsByOperator(String operatorId) {
        return logRepository.findPendingLogsByOperator(operatorId);
    }

    public List<ProductReviewWorkflowLog> getPendingLogsByRole(String roleName) {
        return logRepository.findPendingLogsByRole(roleName);
    }

    public ProductReviewWorkflowLog getPendingLogByProductAndWorkflowAndLevel(
            Long productId, Long workflowId, Integer level) {
        return logRepository.findPendingLogByProductAndWorkflowAndLevel(productId, workflowId, level);
    }

    public ProductReviewWorkflowLog getLatestCompletedLog(Long productId, Long workflowId) {
        return logRepository.findLatestCompletedLog(productId, workflowId);
    }

    public ProductReviewWorkflowLog getLatestApprovedLog(Long productId, Long workflowId) {
        return logRepository.findLatestApprovedLog(productId, workflowId);
    }

    public ProductReviewWorkflowLog getLatestRejectedLog(Long productId, Long workflowId) {
        return logRepository.findLatestRejectedLog(productId, workflowId);
    }

    public ProductReviewWorkflowLog getLatestTransferredLog(Long productId, Long workflowId) {
        return logRepository.findLatestTransferredLog(productId, workflowId);
    }

    public Map<String, Object> getWorkflowStatistics(Long workflowId) {
        Map<String, Object> stats = Map.of(
                "approvedCount", logRepository.countApprovedByWorkflow(workflowId),
                "rejectedCount", logRepository.countRejectedByWorkflow(workflowId),
                "timeoutCount", logRepository.countTimeoutByWorkflow(workflowId),
                "averageProcessingTime", logRepository.calculateAverageProcessingTimeByWorkflow(workflowId),
                "roleDistribution", convertRoleDistribution(logRepository.countByRoleAndWorkflow(workflowId)),
                "operationDistribution", convertOperationDistribution(logRepository.countByOperationAndWorkflow(workflowId)),
                "resultDistribution", convertResultDistribution(logRepository.countByResultAndWorkflow(workflowId))
        );
        return stats;
    }

    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    @Transactional
    public void processTimeoutLogs() {
        List<ProductReviewWorkflowLog> timeoutLogs = logRepository.findTimeoutLogs(LocalDateTime.now());
        for (ProductReviewWorkflowLog log : timeoutLogs) {
            markLogAsTimeout(log.getId());
        }
    }

    private String determineResult(String operation) {
        return switch (operation) {
            case "APPROVE" -> ProductReviewWorkflowLog.Result.APPROVED.name();
            case "REJECT" -> ProductReviewWorkflowLog.Result.REJECTED.name();
            case "TRANSFER" -> ProductReviewWorkflowLog.Result.TRANSFERRED.name();
            case "CANCEL" -> ProductReviewWorkflowLog.Result.CANCELLED.name();
            default -> null;
        };
    }

    private Map<String, Long> convertRoleDistribution(List<Object[]> roleCounts) {
        return roleCounts.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    private Map<String, Long> convertOperationDistribution(List<Object[]> operationCounts) {
        return operationCounts.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    private Map<String, Long> convertResultDistribution(List<Object[]> resultCounts) {
        return resultCounts.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    private void notifyLogCreated(ProductReviewWorkflowLog log) {
        notificationService.sendNotification(
                log.getNextOperatorId(),
                "新的审核任务",
                String.format("您有一个新的商品审核任务，商品ID：%d，工作流：%s，级别：%d",
                        log.getProductId(), log.getWorkflowName(), log.getReviewLevel())
        );
    }

    private void notifyLogStatusUpdated(ProductReviewWorkflowLog log) {
        if (log.getNextOperatorId() != null) {
            notificationService.sendNotification(
                    log.getNextOperatorId(),
                    "审核任务状态更新",
                    String.format("商品审核任务状态已更新，商品ID：%d，工作流：%s，级别：%d，状态：%s",
                            log.getProductId(), log.getWorkflowName(), log.getReviewLevel(), log.getStatus())
            );
        }
    }

    private void notifyLogTimeout(ProductReviewWorkflowLog log) {
        notificationService.sendNotification(
                log.getOperatorId(),
                "审核任务超时",
                String.format("您的商品审核任务已超时，商品ID：%d，工作流：%s，级别：%d",
                        log.getProductId(), log.getWorkflowName(), log.getReviewLevel())
        );
    }

    private void notifyLogAutoApproved(ProductReviewWorkflowLog log) {
        notificationService.sendNotification(
                log.getOperatorId(),
                "审核任务自动通过",
                String.format("商品审核任务已自动通过，商品ID：%d，工作流：%s，级别：%d",
                        log.getProductId(), log.getWorkflowName(), log.getReviewLevel())
        );
    }
} 